package pack2.service;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface SentenceBuilder {

    String buildSentence(int ngramSize);

    String buildSentence(String[] words, int ngramSize);
}
