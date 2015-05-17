package pack.model;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Entity
@Table(name = "ngram")
public class Ngram extends BaseObject {

    @OneToMany(mappedBy = "ngram", cascade = CascadeType.ALL)
    private List<Token> tokenList;
    private Double probability;
    private Integer ngramSize;

    public Ngram(Integer ngramSize) {
        this.ngramSize = ngramSize;
    }

    public Ngram() {
    }

    public List<Token> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<Token> tokenList) {
        this.tokenList = tokenList;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getNgramSize() {
        return ngramSize;
    }

    public void setNgramSize(Integer ngramSize) {
        this.ngramSize = ngramSize;
    }

    @Override
    public String toString() {
        return "Ngram{" +
                "tokenList=" + tokenList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ngram ngram = (Ngram) o;

        List<Token> tokenList = Lists.newArrayList(ngram.tokenList);
        List<Token> tokenList1 = Lists.newArrayList(this.tokenList);
        Collections.sort(tokenList);
        Collections.sort(tokenList1);
        return !(tokenList1 != null ? !tokenList1.equals(tokenList) : tokenList != null);

    }

    @Override
    public int hashCode() {
        return tokenList != null ? tokenList.hashCode() : 0;
    }
}
