package pack.service.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pack.Constants;
import pack.model.Ngram;
import pack.model.NgramsCortege;
import pack.model.Token;
import pack.repository.NgramRepository;
import pack.service.Replacer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Service
public class ReplacerImpl implements Replacer {

    private int missingPosition;

    @Autowired
    NgramRepository ngramRepository;

    @Override
    @Transactional
    public String replace(String initialSentence, int guessNum) {
        int ngramSize = ngramRepository.findAny().getNgramSize();
        System.out.println(initialSentence);
        String[] words = initialSentence.split("\\s");
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("?")) {
                missingPosition = i;
                List<Ngram> ngrams = buildAllNGrams(words, i, ngramSize);
                List<Ngram> bestNgrams = findBestMatchedNgram(ngrams, ngramSize);

                Set<Ngram> bestSet = new HashSet<>(bestNgrams);
                bestNgrams = Lists.newArrayList(bestSet);
                sortNgrams(bestNgrams);

                List<NgramsCortege> ngramsCorteges = getBOBM(bestNgrams, createRegexes(words, i));
                System.out.println("best matches ngrams:");
                if (ngramsCorteges != null && ngramsCorteges.size() != 0) {
                    sortNgramsCortages(ngramsCorteges);
                    for (NgramsCortege nc : ngramsCorteges) {
                        System.out.println(nc);
                    }
                    guessNum = ngramsCorteges.size();
                }
                for (Ngram n : bestNgrams) {
                    if (guessNum == 0) {
                        break;
                    }
                    guessNum--;
                    System.out.println(n);
                }
            }
        }
        return null;
    }

    private List<NgramsCortege> getBOBM(List<Ngram> bestNgrams, ArrayList<Pattern> patterns) {
        if (patterns.size() <= 1) {
            return null;
        }
        List<NgramsCortege> ngramsCortege = new ArrayList<>();
        if (patterns.size() == 0) return null;
        for (int i = 0; i < bestNgrams.size(); i++) {
            String replacedWord = null;
            String tokenStrings = listToString(bestNgrams.get(i).getTokenList());
            for (Pattern p : patterns) {
                Matcher matcher = p.matcher(tokenStrings);
                if (matcher.matches()) {
                    replacedWord = matcher.group(1);
                    break;
                }
            }
            if (replacedWord == null) continue;
            for (int j = i + 1; j < bestNgrams.size(); j++) {
                for (Token t : bestNgrams.get(j).getTokenList()) {
                    if (replacedWord.equals(t.getToken())) {
                        for (Pattern p : patterns) {
                            Matcher matcher = p.matcher(tokenStrings);
                            if (matcher.matches()) {
                                NgramsCortege ng = new NgramsCortege();
                                ng.add(bestNgrams.get(i));
                                ng.add(bestNgrams.get(j));
                                ng.setProbability(bestNgrams.get(i).getProbability() + bestNgrams.get(j).getProbability());
                                ngramsCortege.add(ng);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return ngramsCortege;
    }

    private ArrayList<Pattern> createRegexes(String[] words, int position) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        int p = -1 * Constants.NGRAM + 1;
        for (int i = p; i <= 0; i++) {
            StringBuilder regex = new StringBuilder();
            regex.append(".*");
            boolean createRegexSuccess = true;
            for (int j = i; j < i + Constants.NGRAM; j++) {
                if (position + j >= 0 && position + j < words.length) {
                    if (j == 0) {
                        if (i == 0) regex.append("\\s");
                        regex.append("([\\S]+) ");
                    } else
                        regex.append(words[position + j] + " ");

                } else {
                    createRegexSuccess = false;
                    break;
                }
            }
            if (createRegexSuccess) {
                regex.append(".*");
                patterns.add(Pattern.compile(regex.toString()));
            }
        }
        return patterns;
    }

    private String listToString(List<Token> list) {
        StringBuilder sb = new StringBuilder();
        for (Token t : list) {
            sb.append(t.getToken());
            sb.append(" ");
        }
        return (sb.toString());
    }

    private void sortNgrams(List<Ngram> bestNgrams) {
        Collections.sort(bestNgrams, Collections.reverseOrder(new Comparator<Ngram>() {
            @Override
            public int compare(Ngram o1, Ngram o2) {
                return o1.getProbability().compareTo(o2.getProbability());
            }
        }));
    }

    private void sortNgramsCortages(List<NgramsCortege> ngramsCorteges) {
        Collections.sort(ngramsCorteges, Collections.reverseOrder(new Comparator<NgramsCortege>() {
            @Override
            public int compare(NgramsCortege o1, NgramsCortege o2) {
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

    /**
     * Создать все ngram's размера ngramSize в котором участвует пропущенное слово
     *
     * @return
     */
    private List<Ngram> buildAllNGrams(String[] words, int skipIndex, int ngramSize) {
        int startPos = skipIndex - ngramSize + 1;

        List<Ngram> ngrams = new ArrayList<>();
        for (int i = startPos; i < startPos + ngramSize; i++) {
            if (i < 0 || i + ngramSize > words.length) {
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
