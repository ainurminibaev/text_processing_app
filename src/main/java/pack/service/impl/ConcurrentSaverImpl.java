package pack.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.model.BaseObject;
import pack.model.Ngram;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.ConcurrentSaver;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ainurminibaev on 20.05.15.
 * <p/>
 * Сохранение объектов в заднем потоке,
 * позволяет не обращать внимание на запись в БД
 */
@Service
public class ConcurrentSaverImpl implements ConcurrentSaver {

    @Autowired
    NgramRepository ngramRepository;

    @Autowired
    TokenRepository tokenRepository;

    private volatile boolean saveInterrupted;


    // очередь объектов
    BlockingQueue<Pair<Class, BaseObject>> saveQueue;

    // нужно для того, чтобы хранить токены для поиска и переиспользования
    HashMap<String, Token> tokenHashMap;

    public ConcurrentSaverImpl() {
        saveQueue = new LinkedBlockingQueue<>();
        tokenHashMap = new HashMap<>();
        saveInterrupted = true;
    }


    private void runSaveCircle() {
        saveInterrupted = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (saveQueue) {
                    try {
                        while (!saveInterrupted && !saveQueue.isEmpty()) {
                            Pair<Class, BaseObject> saveObj = saveQueue.poll();
                            // тк имеются ссылки на объекты, объекты из очереди могут быть уже сохранены
                            if (saveObj.getValue().getId() != null) {
                                continue;
                            }
                            BaseObject newObject = null;
                            // сохраняем объект
                            if (saveObj.getLeft().equals(Ngram.class)) {
                                newObject = ngramRepository.save((Ngram) saveObj.getValue());
                            } else if (saveObj.getLeft().equals(Token.class)) {
                                newObject = tokenRepository.save((Token) saveObj.getValue());
                            }
                            if (newObject != null) {
                                // для сохраненного объекта сетим id на старых объект, тк на него могут быть ссылки
                                // из других мест(например старый токен)
                                saveObj.getValue().setId(newObject.getId());
                            }
                        }
                    } catch (Exception e) {
                        saveQueue.clear();
                    }
                    saveInterrupted = true;
                    if (saveQueue.isEmpty()) {
                        saveQueue.notifyAll();
                    }
                }
            }
        }).start();
    }

    @Override
    public void addToQueue(BaseObject toSave) {
        // тк имеются ссылки на объекты, объекты из очереди могут быть уже сохранены
        if (toSave == null || toSave.getId() != null) {
            return;
        }
        try {

            Class<? extends BaseObject> saveObjectClass = toSave.getClass();
            saveQueue.put(Pair.<Class, BaseObject>of(saveObjectClass, toSave));


            // токен добавляем в мап чтобы потом было легко искать
            if (saveObjectClass.equals(Token.class)) {
                Token token = (Token) toSave;
                tokenHashMap.put(token.getToken(), token);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (saveInterrupted) {
            runSaveCircle();
        }
    }

    /**
     * Ожидание очереди сохранения, только тогда сможем продолжить
     */
    @Override
    public void waitSaving() {
        synchronized (saveQueue) {
            System.out.println("waiting of saving");
            if (!saveQueue.isEmpty()) {
                try {
                    saveQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            interruptAll();
        }
    }

    @Override
    public void interruptAll() {
        saveQueue.clear();
        saveInterrupted = true;
        tokenHashMap.clear();
    }

    /**
     * Находит похожие токены, для переиспользования
     * id может быть null
     * засетится потом, когда сохранится
     *
     * @param token
     * @return
     */
    @Override
    public Token findSimilar(String token) {
        Token cachedToken = tokenHashMap.get(token);
        if (cachedToken == null) {
            return tokenRepository.findOneByToken(token);
        }
        return cachedToken;
    }
}
