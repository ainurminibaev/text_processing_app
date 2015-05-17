package pack.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.model.Ngram;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.service.Replacer;

import java.util.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Service
public class ReplacerImpl implements Replacer {

    @Autowired
    NgramRepository ngramRepository;

    @Override
    @Transactional
    public String replace(String initialSentence, int ngramSize) {
        System.out.println(initialSentence);
        String[] words = initialSentence.split("\\s");
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("?")) {
                List<Ngram> ngrams = buildAllNGrams(words, i, ngramSize);
                List<Ngram> bestNgrams = findBestMatchedNgram(ngrams, ngramSize);
                System.out.println("missing word with index = " + i + " can be replaced with(sorted):");
                Set<Ngram> bestSet = new HashSet<>(bestNgrams);
                bestNgrams = Lists.newArrayList(bestSet);
                sortNgrams(bestNgrams);

                for (Ngram ngram : bestNgrams) {
                    System.out.println(ngram);
                }
            }
        }
        return null;
    }

    private void sortNgrams(List<Ngram> bestNgrams) {
        Collections.sort(bestNgrams, Collections.reverseOrder(new Comparator<Ngram>() {
            @Override
            public int compare(Ngram o1, Ngram o2) {
                return o1.getProbability().compareTo(o2.getProbability());
            }
        }));
    }

    private List<Ngram> findBestMatchedNgram(List<Ngram> skippedNgrams, int ngramSize) {
        List<Ngram> ngramsBySize = ngramRepository.findByNgramSize(ngramSize);
        List<Ngram> matchedNgrams = new ArrayList<>();
        for (Ngram ngram : ngramsBySize) {
            for (Ngram skippedNgram : skippedNgrams) {
                if (ngramMatchedToSkipped(ngram, skippedNgram)) {
                    matchedNgrams.add(ngram);
                }
            }
        }
        return matchedNgrams;
    }

    private boolean ngramMatchedToSkipped(Ngram ngram, Ngram skippedNgram) {
        for (int i = 0; i < ngram.getTokenList().size(); i++) {
            Token token = ngram.getTokenList().get(i);
            Token skippedToken = skippedNgram.getTokenList().get(i);
            if (!token.equals(skippedToken) && !skippedToken.getToken().equals("?")) {
                return false;
            }
        }
        return true;
    }

    private List<Ngram> buildAllNGrams(String[] words, int skipIndex, int ngramSize) {
        int startPos = skipIndex - ngramSize + 1;

        List<Ngram> ngrams = new ArrayList<>();
        for (int i = startPos; i < startPos + ngramSize - 1; i++) {
            if (i < 0) {
                continue;
            }
            Ngram ngram = new Ngram();
            ngram.setNgramSize(ngramSize);
            List<Token> tokens = new ArrayList<>();
            for (int j = 0; j < ngramSize; j++) {
                Token t = new Token();
                t.setToken(words[i + j]);
                tokens.add(t);
            }
            ngram.setTokenList(tokens);
            ngrams.add(ngram);
        }

        return ngrams;
    }
}
