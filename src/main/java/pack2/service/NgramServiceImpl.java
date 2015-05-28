package pack2.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pack2.Constants;
import pack2.model.Data;
import pack2.model.Ngram;
import pack2.repository.DataWriter;
import pack2.repository.TextParser;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by giylmi on 22.05.2015.
 */
@Service
public class NgramServiceImpl implements NgramService {

    Logger logger = LoggerFactory.getLogger(NgramServiceImpl.class);

    @Autowired
    private DataWriter dataWriter;
    @Autowired
    private TextParser textParser;

    private boolean enableGoodTuring;
    private HashMultiset<String> wordsMap;
    private HashMultiset<Ngram> bigramsMap;

    @Override
    public Data buildNgram(String inputFolder, int ngramSize, String outputFolder, double notUsedWordsProbability) throws FileNotFoundException {
        logger.info("Building Data with size=" + ngramSize + ", from folder=" + inputFolder);
        String text = textParser.loadFolder(inputFolder);

        //TODO  [\\s,;\\n\\t]+
        logger.info("Building Data from text");
        Data data = new Data(ngramSize);
        ArrayList<String> wordsList = Lists.newArrayList(text.split("[\\s\\n\\t]+"));
        cleanWordSetFromTrunk(wordsList, notUsedWordsProbability);
        String[] words = new String[wordsList.size()];
        wordsList.toArray(words);
        HashSet<String> wordsSet = Sets.newHashSet(words);
        if (words.length < ngramSize) {
            return null;
        }
        int p = 0;
        for (int i = 0; i < words.length - ngramSize + 1; i++) {
            Ngram ngram = new Ngram(ngramSize);
            String[] tokens = new String[ngramSize];
            boolean canBeNgram = true;
            for (int j = 0; j < ngramSize; j++) {
                String word = words[i + j];
                if ((word.equals(Constants.START_TOKEN) && j != 0) || (word.equals(Constants.END_TOKEN) && j != ngramSize - 1)) {
                    canBeNgram = false;
                    break;
                }
                tokens[j] = word;
            }
            if (canBeNgram) {
                ngram.fillTokens(tokens);
                ngram.probability = calculateNgramProbability(ngram, words, wordsSet.size());
                data.addNgram(ngram);
            }

            if (i * 100.0 / words.length - p > 10) {
                logger.info((int) (i * 100.0 / words.length) + "% words have been parsed");
                p = (int) (i * 100.0 / words.length);
            }
        }
        logger.info("All words have been parsed!");
        data.sortNextNgrams();
        if (enableGoodTuring) {
            System.out.println("GT Smoothing");
            goodTuringSmoothing(data);
        }
        if (outputFolder != null) {
            dataWriter.writeData(data, outputFolder);
        }
        System.out.println("All words have been parsed!");

        return data;
    }

    private void goodTuringSmoothing(Data data) {
        final Multiset<Ngram> ngramMultiset = HashMultiset.create(data.ngrams);
        ArrayList<Ngram> oneOccuranceNgrams = Lists.newArrayList(Iterables.transform(Iterables.filter(ngramMultiset.entrySet(), new Predicate<Multiset.Entry<Ngram>>() {
            @Override
            public boolean apply(Multiset.Entry<Ngram> input) {
                return input.getCount() == 1;
            }
        }), new Function<Multiset.Entry<Ngram>, Ngram>() {
            @Override
            public Ngram apply(Multiset.Entry<Ngram> input) {
                return input.getElement();
            }
        }));
        double oneOccurrence = 0;
        for (Ngram oneOccurrenceNgram : oneOccuranceNgrams) {
            oneOccurrence += oneOccurrenceNgram.probability;
        }
        data.probsOfOneOccurrenceNgrams = oneOccurrence;
    }

    @Override
    @Cacheable(value = "cache", cacheManager = "cacheManager")
    public int getCountOfSubString(String left, String right, String[] words) {
//        int count = 0;
//        for (int i = 0; i < words.length - 1; i++) {
//            if (left.equals(words[i]) && (right == null || right.equals(words[i + 1]))) {
//                count++;
//            }
//        }
        if (right == null) {
            return getWordsCountMap(words).count(left);
        }
        Ngram ngram = new Ngram(2);
        ngram.fillTokens(left, right);
        return getBigramCountMap(words).count(ngram);
    }

    public Multiset<Ngram> getBigramCountMap(String[] words) {
        if (bigramsMap == null) {
            List<Ngram> bigrams = new ArrayList<>();
            for (int i = 0; i < words.length - 1; i++) {
                Ngram ngram = new Ngram(2);
                ngram.fillTokens(words[i], words[i + 1]);
                bigrams.add(ngram);
            }
            System.out.println("build bigram map");
            bigramsMap = HashMultiset.create(bigrams);
        }
        return bigramsMap;
    }

    public Multiset<String> getWordsCountMap(String[] words) {
        if (wordsMap == null) {
            System.out.println("build word map");
            wordsMap = HashMultiset.create(Arrays.asList(words));
        }
        return wordsMap;
    }

    /**
     * Расчет вероятность для Ngram
     * Pn = P(w1,w2)*P(w2,w3) ....
     *
     * @return
     */
    private Double calculateNgramProbability(Ngram ngram, String[] words, int wordSetSize) {
        double totalProbab = 1;
        for (int i = 0; i < ngram.tokens.length - 1; i++) {
            String[] tokens = ngram.tokens;
            totalProbab *= getProbabilityForPair(tokens[i], tokens[i + 1], words, wordSetSize);
        }
        return totalProbab;
    }

    @Cacheable(value = "cache", cacheManager = "cacheManager")
    private Double getProbabilityForPair(String left, String right, String[] words, int wordSetSize) {
        return (getCountOfSubString(left, right, words) + 1) / ((double) getCountOfSubString(left, null, words) + wordSetSize);
    }


    /**
     * Избавляемся от пустышек, символов
     *
     * @param wordsList
     */
    private void cleanWordSetFromTrunk(ArrayList<String> wordsList, double uselessWordsProbability) {
        //для начала нужно иметь эти данные для "сырых" строк
        logger.info("cleaning word set");
        String[] wordsArray = wordsList.toArray(new String[wordsList.size()]);
        HashSet<String> wordsSet = Sets.newHashSet(wordsArray);
        double min = Double.MAX_VALUE;
        String minToken = null;
        for (int i = 0; i < wordsList.size(); i++) {
            String token = wordsList.get(i);
            //пропускаем признаки старта и конца
            if (token.equals(Constants.END_TOKEN) || token.equals(Constants.START_TOKEN)) {
                continue;
            }
            Double wordProbability = getCountOfSubString(token, null, wordsArray) / (double) wordsSet.size();
            if (wordProbability < uselessWordsProbability) {
                token = Constants.UNKNOWN_WORD_MARKER;
            }
            if (wordProbability < min && wordProbability != 0) {
                min = wordProbability;
                minToken = token;
            }
            //удаляем старое слово, чтобы вставить очищенное
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
        logger.info("cleaning word set ended");
    }


}
