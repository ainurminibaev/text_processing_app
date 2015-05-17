package pack.service.impl;

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
        Multiset<String> initialMultiSet = Multisets.unmodifiableMultiset(HashMultiset.create(wordsList));
        Multiset<String> wordsSet = HashMultiset.create(wordsList);
        List<Ngram> generatedSentence = new ArrayList<>();
        int maxMatchingSize = ngramSize;
        while (!wordsSet.isEmpty()) {
            Ngram ngram = findBestMatchingAndClean(ngrams, wordsSet, maxMatchingSize);
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
            cleanWordSet(ngram, wordsSet);
            if (maxMatchingSize < ngramSize) {
                replaceFreeOcurances(ngram, initialMultiSet, wordsSet);
            }
            generatedSentence.add(ngram);
        }

        return getSentenceFromNgram(generatedSentence, HashMultiset.create(wordsList));
    }

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

    private void cleanWordSet(Ngram ngram, Multiset<String> wordsSet) {
        for (Token token : ngram.getTokenList()) {
            wordsSet.remove(token.getToken());
        }
    }

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
