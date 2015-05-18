package pack.service.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.model.Ngram;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.service.SentenceBuilder;

import java.util.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Service
public class SentenceBuilderImpl implements SentenceBuilder {

    @Autowired
    NgramRepository ngramRepository;

    @Override
    @Transactional
    public String buildSentence(final String[] words, final int ngramSize) {
        List<Ngram> ngrams = Lists.newArrayList(ngramRepository.findAll());
        ArrayList<String> wordsList = Lists.newArrayList(words);
        // сет из всех слов
        Multiset<String> initialMultiSet = Multisets.unmodifiableMultiset(HashMultiset.create(wordsList));
        // сет из которого будем постепенно удалять слова
        Multiset<String> wordsSet = HashMultiset.create(wordsList);
        List<Ngram> generatedSentence = new ArrayList<>();
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
                Ngram tailNgram = new Ngram();
                List<Token> tokens = new ArrayList<>();
                for (String word : wordsSet) {
                    tokens.add(new Token(word));
                }
                tailNgram.setTokenList(tokens);
                wordsSet.clear();
                break;
            }
            if (maxMatchingSize < ngramSize) {
                replaceFreeOcurances(ngram, initialMultiSet, wordsSet);
            }
            //уменьшаем множество слов на те ngram которые были найдены
            cleanWordSet(ngram, wordsSet);
            generatedSentence.add(ngram);
        }

        return getSentenceFromNgram(generatedSentence, HashMultiset.create(wordsList));
    }

    @Override
    @Transactional
    public String buildRandomSentence(int n) {
        if (n < 2) throw new RuntimeException("n should be >= 2");
        Ngram random = ngramRepository.randomNgramByNgramSize(n);
        StringBuilder text = new StringBuilder();
        addNgramStringBuilder(random, text);
        text.append(buildSentenceFromFirstNgram(random));
        return text.toString();
    }

    private String buildSentenceFromFirstNgram(Ngram random) {
        final List<Token> tokens = random.getTokenList();
        Set<Ngram> ngramSet = Sets.filter(tokens.get(1).getNgramSet(), new Predicate<Ngram>() {
            @Override
            public boolean apply(Ngram ngram) {
                return ngram.getTokenList().get(0).getToken().equals(tokens.get(1).getToken());
            }
        });
        for (int i = 2; i < tokens.size(); i++) {
            final int finalI = i;
            ngramSet = Sets.intersection(ngramSet, Sets.filter(tokens.get(i).getNgramSet(), new Predicate<Ngram>() {
                @Override
                public boolean apply(Ngram ngram) {
                    return ngram.getTokenList().get(finalI - 1).getToken().equals(tokens.get(finalI).getToken());
                }
            }));
        }
        Ngram[] ngrams = ngramSet.toArray(new Ngram[ngramSet.size()]);
        Arrays.sort(ngrams, Ngram.BY_PROBABILITY_DESC_COMPARATOR);
        Random randomer = new Random();
        double total = totalPropability(ngrams);
        double probability = randomer.nextDouble() % total;
        double sum = 0;
        for (Ngram ngram: ngrams) {
            if (probability < sum + ngram.getProbability()) {
                StringBuilder text = new StringBuilder();
                text.append(ngram.getTokenList().get(ngram.getTokenList().size() - 1)).append(" ");
                return text.append(buildSentenceFromFirstNgram(ngram)).toString();
            }
            else {
                sum += ngram.getProbability();
            }
        }
        //нграмы кончились - предложению конец
        return "";
    }

    private double totalPropability(Ngram[] ngrams) {
        double total = 0;
        for (Ngram ngram: ngrams){
            total += ngram.getProbability();
        }
        return total;
    }

    private void addNgramStringBuilder(Ngram random, StringBuilder text) {
        for (Token token: random.getTokenList()) {
            text.append(token.getToken()).append(" ");
        }
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
    private void replaceFreeOcurances(Ngram ngram, Multiset<String> initSet, Multiset<String> leftoverWordsSet) {
        if (leftoverWordsSet.size() == 0) {
            return;
        }
        for (Token token : ngram.getTokenList()) {
            if (!initSet.contains(token.getToken())) {
                Iterator<String> wsIterator = leftoverWordsSet.iterator();
                if (wsIterator.hasNext()) {
                    token.setToken(wsIterator.next());
                    wsIterator.remove();
                }
            }
        }
    }

    /**
     * Из энграм генерим предложение
     *
     * @return
     */
    private String getSentenceFromNgram(List<Ngram> generatedSentence, Multiset<String> wordSet) {
        StringBuilder sentenceBuilder = new StringBuilder();
        for (Ngram ngram : generatedSentence) {
            for (Token token : ngram.getTokenList()) {
                if (wordSet.contains(token.getToken())) {
                    sentenceBuilder.append(token.getToken());
                    sentenceBuilder.append(' ');
                    wordSet.remove(token.getToken());
                }
            }
        }
        return sentenceBuilder.toString();
    }

    /**
     * удаляем из сета использованные слова
     *
     * @param ngram
     * @param wordsSet
     */
    private void cleanWordSet(Ngram ngram, Multiset<String> wordsSet) {
        for (Token token : ngram.getTokenList()) {
            wordsSet.remove(token.getToken());
        }
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
                if (maxMatching == null || (ngram.getProbability().compareTo(maxMatching.getProbability()) > 0 && ngram.getProbability().compareTo(1.0) <= 0)) {
                    maxMatching = ngram;
                }
            }
        }
        return maxMatching;
    }


    private boolean ngramContainsAllNeededTokens(Ngram ngram, Multiset<String> wordsSet, int ngramSize) {
        int matchCount = 0;
        for (Token token : ngram.getTokenList()) {
            if (wordsSet.contains(token.getToken())) {
                matchCount++;
            }
        }
        return matchCount == ngramSize;
    }
}
