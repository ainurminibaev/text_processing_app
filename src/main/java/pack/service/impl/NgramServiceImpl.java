package pack.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
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

    public static final String START_FLAG = " <s> ";
    private static final String END_FLAG = " </s> ";
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
            BreakIterator sentenceIterator =
                    BreakIterator.getSentenceInstance(Locale.ENGLISH);
            while ((line = reader.readLine()) != null) {
                boolean isStartFlag = true;
                sentenceIterator.setText(line);
                int flagIndex = sentenceIterator.current();
                Map<Integer, Boolean> insertMap = new HashMap<>();
                while (flagIndex != -1) {
                    if (isStartFlag) {
                        insertMap.put(flagIndex, isStartFlag);
                        isStartFlag = false;
                    } else {
                        insertMap.put(flagIndex - 1, isStartFlag);
                        isStartFlag = true;
                    }
                    flagIndex = sentenceIterator.next();
                }
                StringBuilder str = new StringBuilder(line.length());
                for (int i = 0; i < line.length(); i++) {
                    if (insertMap.containsKey(i)) {
                        Boolean isStart = insertMap.get(i);
                        str.append(isStart ? START_FLAG : END_FLAG);
                    }
                    str.append(line.charAt(i));
                }
                textBuilder.append(str.toString());
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
//        for (int i = 0; i < wordsList.size(); i++) {
//            int currentIndex = i;
//            String token = wordsList.get(currentIndex);
//            if (hasStartFlag(token)) {
//                wordsList.add(currentIndex, START_FLAG);
//                currentIndex++;
//            }
//            if (hasEndFlag(token)) {
//                if (currentIndex + 1 >= wordsList.size()) {
//                    wordsList.add(END_FLAG);
//                    break;
//                } else {
//                    wordsList.add(currentIndex + 1, END_FLAG);
//                }
//                currentIndex++;
//            }
//            wordsList.remove(currentIndex);
//            i = currentIndex;
//            wordsList.add(currentIndex, token.replaceAll("[^\\w]", ""));
//        }
//TODO clean symbols except ,
        String[] words = new String[wordsList.size()];
        wordsList.toArray(words);
        HashSet<String> wordsSet = Sets.newHashSet(words);
        if (words.length < ngramSize) {
            return;
        }
        for (int i = 0; i < words.length - ngramSize + 1; i++) {
            Ngram ngram = new Ngram(ngramSize);
            List<Token> tokens = new ArrayList<>();
            Token t;
            for (int j = 0; j < ngramSize; j++) {
                String word = words[i + j];
                t = tokenRepository.findOneByToken(word);
                if (t == null) {
                    t = new Token();
                    t.setToken(word);
                    tokenRepository.save(t);
                }
                tokens.add(t);
            }
            ngram.setTokenList(tokens);
            ngram.setProbability(calculateNgramProvability(ngram, words, wordsSet.size()));
            ngramRepository.save(ngram);
        }
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
