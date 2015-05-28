package pack2.model;

import pack2.Constants;

import java.io.Serializable;
import java.util.*;

/**
 * Created by giylmi on 21.05.2015.
 */
public class Data implements Serializable{

//    public List<String> tokens;
    public Map<Integer, List<Ngram>> ngramMap;
    public Map<Integer, Map<Ngram, LinkedList<Ngram>>> nextNgramMapMap;

    private transient List<Ngram> firstNgrams;

    public Data() {
//        tokens = new ArrayList<>();
        ngramMap = new HashMap<>();
        nextNgramMapMap = new HashMap<>();
    }

    public Ngram getFirstNgram(int ngramSize) {
        Random random = new Random();
        if (firstNgrams != null) return firstNgrams.get(random.nextInt() % firstNgrams.size());
        firstNgrams = new ArrayList<>();
        List<Ngram> ngrams = ngramMap.get(ngramSize);
        for (Ngram ngram: ngrams) {
            if (ngram.tokens[0].equals(Constants.START_TOKEN))
                firstNgrams.add(ngram);
        }
        Collections.shuffle(firstNgrams);
        return firstNgrams.get(0);
    }

    public void addNgram(Ngram ngram) {
        if (ngramMap.get(ngram.size) == null) ngramMap.put(ngram.size, new LinkedList<Ngram>());
        ngramMap.get(ngram.size).add(ngram);
        if (nextNgramMapMap.get(ngram.size) == null) nextNgramMapMap.put(ngram.size, new HashMap<Ngram, LinkedList<Ngram>>());
        Map<Ngram, LinkedList<Ngram>> nextNgramsMap = nextNgramMapMap.get(ngram.size);
        Ngram excludeLast = ngram.excludeLastTokenNgram();
        LinkedList<Ngram> ngrams = nextNgramsMap.get(excludeLast);
        if (ngrams == null) nextNgramsMap.put(excludeLast, ngrams = new LinkedList<Ngram>());
        ngrams.add(bsearch(ngrams, ngram), ngram);
    }

    private double EPS = 1e-10;
    private int bsearch(LinkedList<Ngram> ngrams, Ngram key) {
        int i = -1, j = ngrams.size() -1;
        if (j == -1) return 0;
        while (i < j - 1) {
            int m = (i + j) / 2;
            Ngram ngram = ngrams.get(m);
            if (Math.abs(ngram.probability - key.probability) <= EPS) return m;
            if ((ngram.probability - key.probability) > EPS) {
                i = m;
            } else {
                j = m;
            }
        }
        Ngram ngram = ngrams.get(j);
        if ((key.probability - ngram.probability) > EPS)
            return j;
        return j + 1;
    }
}
