package pack.model;

import java.util.ArrayList;
import java.util.List;

public class NgramsCortege {

    private ArrayList<Ngram> mNgrams;
    private Double mProbability;
    private String mWord;

    public NgramsCortege() {
        mNgrams = new ArrayList<>();
    }

    public List<Ngram> getNgrams() {
        return mNgrams;
    }

    public void setNgrams(ArrayList<Ngram> ngrams) {
        mNgrams = ngrams;
    }

    public Double getProbability() {
        return mProbability;
    }

    public void setProbability(Double probability) {
        mProbability = probability;
    }

    public void add(Ngram ngram) {
        mNgrams.add(ngram);
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String word) {
        mWord = word;
    }

    @Override
    public String toString() {
        return mNgrams.get(0).toString() + " & " + mNgrams.get(1).toString();
    }
}
