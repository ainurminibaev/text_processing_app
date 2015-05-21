package pack.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Entity
@Table(name = "ngram")
public class Ngram extends BaseObject {

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "ngram_token",
            joinColumns = {@JoinColumn(name = "ngramId")},
            inverseJoinColumns = {@JoinColumn(name = "tokenId")}
    )
    private List<Token> tokenList;
    private Boolean isStart;
    private Double probability;
    private Integer ngramSize;

    public Ngram(Integer ngramSize) {
        this.ngramSize = ngramSize;
    }

    public Ngram() {
    }

    public Boolean getIsStart() {
        return isStart;
    }

    public void setIsStart(Boolean isStart) {
        this.isStart = isStart;
    }

    public static Comparator<Ngram> getByProbabilityDescComparator() {
        return BY_PROBABILITY_DESC_COMPARATOR;
    }

    public static void setByProbabilityDescComparator(Comparator<Ngram> byProbabilityDescComparator) {
        BY_PROBABILITY_DESC_COMPARATOR = byProbabilityDescComparator;
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
        if (tokenList.size() != tokenList1.size()) {
            return false;
        }
        Collections.sort(tokenList);
        Collections.sort(tokenList1);
        for (int i = 0; i < tokenList.size(); i++) {
            if (!tokenList.get(i).equals(tokenList1.get(i))) {
                return false;
            }
        }
        return true;

    }

    @Override
    public int hashCode() {
        List<Token> tokenList1 = Lists.newArrayList(this.tokenList);
        Collections.sort(tokenList1);
        return tokenList != null ? Objects.hashCode(tokenList1) : 0;
    }

    public static Comparator<Ngram> BY_PROBABILITY_DESC_COMPARATOR = new Comparator<Ngram>() {
        @Override
        public int compare(Ngram o1, Ngram o2) {
            return o2.getProbability().compareTo(o1.getProbability());
        }
    };
}
