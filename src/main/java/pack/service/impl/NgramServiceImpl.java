package pack.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pack.Constants;
import pack.Util;
import pack.model.Ngram;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.NgramService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.BreakIterator;
import java.util.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Service
public class NgramServiceImpl implements NgramService {

    @Autowired
    NgramRepository ngramRepository;
    @Autowired
    TokenRepository tokenRepository;

    /**
     * Распарсить текст из файла так, чтобы разбить на Ngram
     * //TODO указать признак конца(начала) предложений или это в методе buildNgram??
     *
     * @param file
     */
    public String loadFile(String file) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            StringBuilder textBuilder = new StringBuilder();
            BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                textBuilder.append(Util.getMarkedLine(line, sentenceIterator));
                textBuilder.append('\n');
            }
            return textBuilder.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }


    @Override
    public void buildNgram(String text, int ngramSize) {
        //TODO  [\\s,;\\n\\t]+
        ArrayList<String> wordsList = Lists.newArrayList(text.split("[\\s\\n\\t]+"));
        for (int i = 0; i < wordsList.size(); i++) {
            String token = wordsList.get(i);
            //пропускаем признаки старта и конца
            if (token.equals(Constants.END_FLAG) || token.equals(Constants.START_FLAG)) {
                continue;
            }
            //удаляем старое слово, чтобы слова вставить
            wordsList.remove(i);
            token = token.replaceAll("[^\\w,]", "");
            if (token.trim().length() == 0) {
                //пропускаем слова-пустышки
                i--;
                continue;
            }
            //избавляемся от всего, кроме букв и запятых
            wordsList.add(i, token);
        }
        String[] words = new String[wordsList.size()];
        wordsList.toArray(words);
        HashSet<String> wordsSet = Sets.newHashSet(words);
        if (words.length < ngramSize) {
            return;
        }
        int p = 0;
        for (int i = 0; i < words.length - ngramSize + 1; i++) {
            Ngram ngram = new Ngram(ngramSize);
            List<Token> tokens = new ArrayList<>();
            Token t;
            boolean canBeNgram = true;
            for (int j = 0; j < ngramSize; j++) {
                String word = words[i + j];
                if ((word.equals(Constants.START_FLAG) && j != 0) || (word.equals(Constants.END_FLAG) && j != ngramSize - 1)) {
                    canBeNgram = false;
                    break;
                }
                t = tokenRepository.findOneByToken(word);
                if (t == null) {
                    t = new Token();
                    t.setToken(word);
                }
                tokens.add(t);
            }
            if (canBeNgram) {
                for (Token token: tokens){
                    tokenRepository.save(token);
                }
                ngram.setTokenList(tokens);
                ngram.setProbability(calculateNgramProvability(ngram, words, wordsSet.size()));
                ngramRepository.save(ngram);
            }

            if (i * 100.0 / words.length - p > 10) {
                System.out.println(i * 100.0 / words.length + " words have been parsed");
                p = (int)(i * 100.0 / words.length);
            }
        }
        System.out.println("All words have been parsed!");
    }

    private boolean hasEndFlag(String token) {
        token = token.replaceAll(" ", "");
        return token.indexOf(token.length() - 1) == '.';
    }

    private boolean hasStartFlag(String token) {
        token = token.replaceAll(" ", "");
        return Character.isUpperCase(token.charAt(0));
    }

    /**
     * Расчет вероятность для Ngram
     * Pn = P(w1,w2)*P(w2,w3) ....
     *
     * @return
     */
    private Double calculateNgramProvability(Ngram ngram, String[] words, int wordSetSize) {
        double totalProbab = 1;
        for (int i = 0; i < ngram.getTokenList().size() - 1; i++) {
            List<Token> tokens = ngram.getTokenList();
            totalProbab *= getProbabilityForPair(tokens.get(i).getToken(), tokens.get(i + 1).getToken(), words, wordSetSize);
        }
        return totalProbab;
    }

    /**
     * Вероятность для Биграммы(Ngram N=2)
     * используется сглаживание по Лапласу
     *
     * @return
     */
    @Cacheable(value = "cache", cacheManager = "cacheManager")
    private Double getProbabilityForPair(String left, String right, String[] words, int wordSetSize) {
        return (getCountOfSubString(left, right, words) + 1) / ((double) getCountOfSubString(left, null, words) + wordSetSize);
    }

    /**
     * Количество вхождений слов в текущее множество слов
     * Нужен для расчета P(w1,w2) и P(w)
     * поэтому второй аргумент может быть null
     *
     * @return
     */
    @Override
    @Cacheable(value = "cache", cacheManager = "cacheManager")
    public int getCountOfSubString(String left, String right, String[] words) {
        int count = 0;
        for (int i = 0; i < words.length - 1; i++) {
            if (left.equals(words[i]) && (right == null || right.equals(words[i + 1]))) {
                count++;
            }
        }
        return count;
    }
}
