package pack2.model;

import pack2.Constants;
import pack2.Util;

import java.io.Serializable;
import java.util.*;

/**
 * Created by giylmi on 21.05.2015.
 */
public class Data implements Serializable{

//    public List<String> tokens;
    public Integer ngramSize;
    public LinkedList<Ngram> ngrams;
    public Map<Ngram, LinkedList<Ngram>> nextNgramMap;

    private LinkedList<Ngram> firstNgrams;

    public Data(int ngramSize) {
//        tokens = new ArrayList<>();
        this.ngramSize = ngramSize;
        ngrams = new LinkedList<>();
        nextNgramMap = new HashMap<>();
        firstNgrams = new LinkedList<>();
    }

    public Ngram getFirstNgram() {
        return Util.randomNgram(firstNgrams);
    }

    public void addNgram(Ngram ngram) {
        addToNgramMap(ngram);
        addToNextNgramsMap(ngram);
        addToFirstNgramsMap(ngram);
    }

    private void addToFirstNgramsMap(Ngram ngram) {
        if (ngram.tokens[0].equals(Constants.START_TOKEN)) {
            ngrams.add(bsearch(ngrams, ngram), ngram);
        }
    }

    private void addToNextNgramsMap(Ngram ngram) {
        Ngram excludeLast = ngram.excludeLastTokenNgram();
        LinkedList<Ngram> ngrams = nextNgramMap.get(excludeLast);
        if (ngrams == null) nextNgramMap.put(excludeLast, ngrams = new LinkedList<Ngram>());
        ngrams.add(bsearch(ngrams, ngram), ngram);
    }

    private void addToNgramMap(Ngram ngram) {
        ngrams.add(ngram);
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
