package pack2.service;

import pack2.model.Ngram;

import java.util.List;

/**
 * Created by giylmi on 28.05.2015.
 */
public interface Replacer {

    String replace(String initialSequence, int guessNum);


    List<Ngram> buildAllNGrams(String[] words, int skipIndex, int ngramSize);
}
