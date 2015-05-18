package pack.service.impl;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pack.model.Ngram;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.service.NgramService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Service
public class NgramServiceImpl implements NgramService {

    @Autowired
    NgramRepository ngramRepository;

    /**
     * Распарсить текст из файла так, чтобы разбить на Ngram
     * //TODO указать признак конца(начала) предложений или это в методе buildNgram??
     *
     * @param file
     */
    public String loadFile(String file) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            StringBuilder textBuilder = new StringBuilder("<s> ");
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
            }
            textBuilder.append(" </s>");
            return textBuilder.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    @Override
    public void buildNgram(String text, int ngramSize) {
        List<Ngram> ngrams = new ArrayList<>();
        //TODO
        String[] words = text.split("[\\s\\.]+");
        HashSet<String> wordsSet = Sets.newHashSet(words);
        if (words.length < ngramSize) {
            return;
        }
        for (int i = 0; i < words.length - ngramSize + 1; i++) {
            Ngram ngram = new Ngram(ngramSize);
            List<Token> tokens = new ArrayList<>();
            Token t;
            for (int j = 0; j < ngramSize; j++) {
                t = new Token();
                String word = words[i + j];
                t.setToken(word);
                t.setNgram(ngram);
                tokens.add(t);
            }
            ngram.setTokenList(tokens);
            ngram.setProbability(calculateNgramProvability(ngram, words, wordsSet.size()));
            ngrams.add(ngram);
            ngramRepository.save(ngram);
        }
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
