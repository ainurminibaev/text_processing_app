package pack2.service;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack2.Util;
import pack2.model.Data;
import pack2.model.Ngram;
import pack2.repository.DataReader;

import java.util.*;

/**
 * Created by giylmi on 21.05.2015.
 */
@Service
public class SentenceBuilderImpl implements SentenceBuilder {

    @Autowired
    private DataReader dataReader;

    @Override
    public String buildSentence() {
        Data data = dataReader.getData();
        if (data == null) throw new RuntimeException("NO DATA FOUND");
        Map<Ngram, LinkedList<Ngram>> nextNgramMap = data.nextNgramMap;
        Ngram ngram = data.getFirstNgram();
        StringBuilder sentence = new StringBuilder();
        appendNgram(sentence, ngram);
        do {
            Ngram newNgram = ngram.excludeFirstTokenNgram();
            List<Ngram> nextNgrams = nextNgramMap.get(newNgram);
            if (nextNgrams == null || nextNgrams.isEmpty()) return sentence.toString();
            Ngram nextNgram = Util.randomNgram(nextNgrams);
            sentence.append(nextNgram.tokens[nextNgram.size - 1]).append(" ");
            ngram = nextNgram;
        } while (!ngram.tokens[data.ngramSize - 1].equals("</s>"));
        return sentence.toString();
    }

    @Override
    public String buildSentence(String[] words) {
        Data data = dataReader.getData();
        List<Ngram> ngrams = data.ngrams;
        ArrayList<String> wordsList = Lists.newArrayList(words);
        // сет из всех слов
        Multiset<String> initialMultiSet = Multisets.unmodifiableMultiset(HashMultiset.create(wordsList));
        // сет из которого будем постепенно удалять слова
        Multiset<String> wordsSet = HashMultiset.create(wordsList);
        List<Ngram> generatedSentence = new ArrayList<>();
        Integer ngramSize = data.ngramSize;
        int maxMatchingSize = ngramSize;
        while (!wordsSet.isEmpty()) {
            //находим лучший Ngram для текущего множества
            Ngram ngram = findBestMatchingAndClean(ngrams, wordsSet, maxMatchingSize);
            //не нашли ищем меньший энграм
            if (ngram == null && maxMatchingSize > 0) {
                maxMatchingSize--;
                continue;
            } else if (ngram == null && maxMatchingSize == 0) {
                //create Ngram from all other's
                Ngram tailNgram = new Ngram(ngramSize);
                String[] tokens = new String[wordsSet.size()];
                wordsSet.toArray(tokens);
                tailNgram.tokens = tokens;
                wordsSet.clear();
                break;
            }
            if (maxMatchingSize < ngramSize) {
                replaceFreeOccurances(ngram, initialMultiSet, wordsSet);
            }
            //уменьшаем множество слов на те ngram которые были найдены
            cleanWordSet(ngram, wordsSet);
            generatedSentence.add(ngram);
        }

        return getSentenceFromNgram(generatedSentence, HashMultiset.create(wordsList));
    }

    /**
     * Из энграм генерим предложение
     *
     * @return
     */
    private String getSentenceFromNgram(List<Ngram> generatedSentence, Multiset<String> wordSet) {
        StringBuilder sentenceBuilder = new StringBuilder();
        for (Ngram ngram : generatedSentence) {
            for (String token : ngram.tokens) {
                if (wordSet.contains(token)) {
                    sentenceBuilder.append(token);
                    sentenceBuilder.append(' ');
                    wordSet.remove(token);
                }
            }
        }
        return sentenceBuilder.toString();
    }

    private void appendNgram(StringBuilder sentence, Ngram ngram) {
        for (String token : ngram.tokens)
            sentence.append(token).append(" ");
    }

    /**
     * Находим лучший Ngram
     *
     * @param ngrams
     * @param wordsSet
     * @param ngramSize
     * @return
     */
    private Ngram findBestMatchingAndClean(List<Ngram> ngrams, Multiset<String> wordsSet, int ngramSize) {
        Ngram maxMatching = null;
        for (Ngram ngram : ngrams) {
            if (ngramContainsAllNeededTokens(ngram, wordsSet, ngramSize)) {
                if (maxMatching == null || (((Double) ngram.probability).compareTo((Double) maxMatching.probability) > 0 && ((Double) ngram.probability).compareTo(1.0) <= 0)) {
                    maxMatching = ngram;
                }
            }
        }
        return maxMatching;
    }

    private boolean ngramContainsAllNeededTokens(Ngram ngram, Multiset<String> wordsSet, int ngramSize) {
        int matchCount = 0;
        for (String token : ngram.tokens) {
            if (wordsSet.contains(token)) {
                matchCount++;
            }
        }
        return matchCount == ngramSize;
    }

    /**
     * В найденном ngram меняем все неиспользуемые в множестве слова на слова из множества
     * Переделать так чтобы брал не первое слово, а самое подходящее
     * Пример: сет [книга, играть]
     * энграм:
     *
     * @param ngram
     * @param initSet
     * @param leftoverWordsSet
     */
    private void replaceFreeOccurances(Ngram ngram, Multiset<String> initSet, Multiset<String> leftoverWordsSet) {
        if (leftoverWordsSet.size() == 0) {
            return;
        }
        for (int i = 0; i < ngram.tokens.length; i++) {
            if (!initSet.contains(ngram.tokens[i])) {
                Iterator<String> wsIterator = leftoverWordsSet.iterator();
                if (wsIterator.hasNext()) {
                    ngram.tokens[i] = (wsIterator.next());
                    wsIterator.remove();
                }
            }
        }
    }

    /**
     * удаляем из сета использованные слова
     *
     * @param ngram
     * @param wordsSet
     */
    private void cleanWordSet(Ngram ngram, Multiset<String> wordsSet) {
        for (String token : ngram.tokens) {
            wordsSet.remove(token);
        }
    }

}
