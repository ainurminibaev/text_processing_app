package pack.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@Entity
@Table(name = "token")
public class Token extends BaseObject implements Comparable<Token> {

    private String token;

    @ManyToMany(mappedBy = "tokenList")
    private Set<Ngram> ngramSet;

    public Token(String word) {
        this.token = word;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Set<Ngram> getNgramSet() {
        return ngramSet;
    }

    public void setNgramSet(Set<Ngram> ngramSet) {
        this.ngramSet = ngramSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token1 = (Token) o;

        return !(token != null ? !token.equals(token1.token) : token1.token != null);

    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public String toString() {
        return token + " ";
    }

    @Override
    public int compareTo(Token o) {
        return this.getToken().compareTo(o.getToken());
    }
}
