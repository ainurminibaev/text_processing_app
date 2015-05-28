package pack2.model;

import pack2.Constants;
import pack2.Util;

import java.io.Serializable;
import java.util.*;

/**
 * Created by giylmi on 21.05.2015.
 */
public class Data implements Serializable {

//    public List<String> tokens;
    public Integer ngramSize;
    public LinkedList<Ngram> ngrams;
    public Map<Ngram, LinkedList<Ngram>> nextNgramMap;
    public double probsOfOneOccurrenceNgrams;
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
            firstNgrams.add(ngram);
        }
    }

    private void addToNextNgramsMap(Ngram ngram) {
        Ngram excludeLast = ngram.excludeLastTokenNgram();
        LinkedList<Ngram> ngrams = nextNgramMap.get(excludeLast);
        if (ngrams == null) nextNgramMap.put(excludeLast, ngrams = new LinkedList<Ngram>());
        ngrams.add(ngram);
    }

    private void addToNgramMap(Ngram ngram) {
        ngrams.add(ngram);
    }

    public void sortNextNgrams() {
        for (LinkedList list: nextNgramMap.values()) {
            Collections.sort(list, new Comparator<Ngram>() {
                @Override
                public int compare(Ngram o1, Ngram o2) {
                    return ((Double)o2.probability).compareTo(o2.probability);
                }
            });
        }
        Collections.sort(firstNgrams, new Comparator<Ngram>() {
            @Override
            public int compare(Ngram o1, Ngram o2) {
                return ((Double)o2.probability).compareTo(o2.probability);
            }
        });
    }
}
