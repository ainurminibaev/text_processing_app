package pack2.service;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack2.model.Ngram;
import pack2.model.NgramsCortege;
import pack2.repository.DataReader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by giylmi on 28.05.2015.
 */
@Service
public class ReplacerImpl implements Replacer {

    @Autowired
    DataReader dataReader;

    @Override
    public String replace(String initialSentence, int guessNum) {
        int ngramSize = dataReader.getNgramSize();
        System.out.println(initialSentence);
        String[] words = initialSentence.split("\\s");
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("?")) {
                List<Ngram> ngrams = buildAllNGrams(words, i, ngramSize);
                List<Ngram> bestNgrams = findBestMatchedNgram(ngrams, ngramSize);

                Set<Ngram> bestSet = new HashSet<>(bestNgrams);
                bestNgrams = Lists.newArrayList(bestSet);
                sortNgrams(bestNgrams);

                List<NgramsCortege> ngramsCorteges = getBOBM(bestNgrams, createRegexes(words, i, ngramSize));
                System.out.println("variants:");
                if (ngramsCorteges != null && ngramsCorteges.size() != 0) {
                    sortNgramsCortages(ngramsCorteges);
                    for (NgramsCortege nc : ngramsCorteges) {
                        if (guessNum == 0) {
                            break;
                        }
                        guessNum--;
                        System.out.println(printNgramCortege(nc, words, i));
                    }
                }
            }
        }
        return null;
    }

    private List<NgramsCortege> getBOBM(List<Ngram> bestNgrams, ArrayList<Pattern> patterns) {
        List<NgramsCortege> ngramsCortege = new ArrayList<>();
        if (patterns.size() == 0) return null;
        if (patterns.size() == 1) {
            for (Ngram n : bestNgrams) {
                String tokenStrings = listToString(n.tokens);
                Matcher matcher = patterns.get(0).matcher(tokenStrings);
                String replacedWord = matcher.group(1);
                if (!replacedWord.equals("<s>") || !replacedWord.equals("</s>")) {
                    NgramsCortege nc = new NgramsCortege();
                    nc.setWord(matcher.group(1));
                    nc.setProbability(n.probability);
                    nc.add(n);
                }
            }
            return ngramsCortege;
        }
        for (int i = 0; i < bestNgrams.size(); i++) {
            String replacedWord = null;
            String tokenStrings = listToString(bestNgrams.get(i).tokens);
            for (Pattern p : patterns) {
                Matcher matcher = p.matcher(tokenStrings);
                if (matcher.matches()) {
                    replacedWord = matcher.group(1);
                    break;
                }
            }
            if (replacedWord == null || replacedWord.equals("<s>") || replacedWord.equals("</s>")) continue;
            NgramsCortege ng = new NgramsCortege();
            ng.add(bestNgrams.get(i));
            ng.setWord(replacedWord);
            ng.setProbability(bestNgrams.get(i).probability);
            for (int j = i + 1; j < bestNgrams.size(); j++) {
                for (String t : bestNgrams.get(j).tokens) {
                    if (replacedWord.equals(t)) {
                        for (Pattern p : patterns) {
                            Matcher matcher = p.matcher(tokenStrings);
                            if (matcher.matches()) {
                                ng.add(bestNgrams.get(j));
                                ng.setProbability(bestNgrams.get(i).probability + bestNgrams.get(j).probability);
                                bestNgrams.remove(j);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            ngramsCortege.add(ng);
        }
        return ngramsCortege;
    }

    private String printNgramCortege(NgramsCortege ngramsCortege, String[] words, int position) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i == position) {
                s.append(ngramsCortege.getWord());
                s.append(" ");
            } else {
                s.append(words[i]);
                s.append(" ");
            }
        }
        return s.toString();
    }

    private ArrayList<Pattern> createRegexes(String[] words, int position, int ngramSize) {
        ArrayList<Pattern> patterns = new ArrayList<>();
        int p = -1 * ngramSize + 1;
        for (int i = p; i <= 0; i++) {
            StringBuilder regex = new StringBuilder();
            regex.append(".*");
            boolean createRegexSuccess = true;
            for (int j = i; j < i + ngramSize; j++) {
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

    private String listToString(String[] list) {
        StringBuilder sb = new StringBuilder();
        for (String t : list) {
            sb.append(t);
            sb.append(" ");
        }
        return (sb.toString());
    }

    private void sortNgrams(List<Ngram> bestNgrams) {
        Collections.sort(bestNgrams, Collections.reverseOrder(new Comparator<Ngram>() {
            @Override
            public int compare(Ngram o1, Ngram o2) {
                return ((Double) o1.probability).compareTo((Double) o2.probability);
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
        List<Ngram> ngramsBySize = dataReader.getData().ngramMap.get(ngramSize);
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
        for (int i = 0; i < ngram.tokens.length; i++) {
            String token = ngram.tokens[i];
            String skippedToken = skippedNgram.tokens[i];
            if (!token.equals(skippedToken) && !skippedToken.equals("?")) {
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
            Ngram ngram = new Ngram(ngramSize);
            String[] tokens = new String[ngramSize];
            for (int j = 0; j < ngramSize; j++) {
                tokens[j] = words[i + j];
            }
            ngram.fillTokens(tokens);
            ngrams.add(ngram);
        }

        return ngrams;
    }
}
