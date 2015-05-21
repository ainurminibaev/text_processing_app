package pack2.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by giylmi on 21.05.2015.
 */
public class Ngram implements Serializable{

    public int size;
    public double probability;
    public String[] tokens;

    public Ngram(int size) {
        this.size = size;
        this.tokens = new String[size];
    }

    public Ngram(int size, double probability) {
        this.size = size;
        this.probability = probability;
        tokens = new String[size];
    }

    public void fillTokens(String... tokens){
        if (tokens.length > size) {
            System.out.println("WARNING: FILLING NGRAM SIZE OF " + size + " WITH " + tokens.length + " TOKENS");
        } else if (tokens.length < size) {
            throw new RuntimeException("ERROR: TOO FEW TOKENS TO FILL");
        }
        for (int i = 0; i < size; i++){
            this.tokens[i] = tokens[i];
        }
    }

    public Ngram excludeFirstTokenNgram() {
        Ngram newNgram = new Ngram(this.size - 1);
        for (int i = 1; i < this.size; i++){
            newNgram.tokens[i - 1] = this.tokens[i];
        }
        return newNgram;
    }

    public Ngram excludeLastTokenNgram() {
        Ngram newNgram = new Ngram(this.size - 1);
        for (int i = 0; i < this.size - 1; i++){
            newNgram.tokens[i] = this.tokens[i];
        }
        return newNgram;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ngram)) return false;

        Ngram ngram = (Ngram) o;

        if (!Arrays.equals(tokens, ngram.tokens)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tokens != null ? Arrays.hashCode(tokens) : 0;
    }
}
