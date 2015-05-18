package pack.service;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface SentenceBuilder {

    String buildSentence(String[] words, int ngram);

    String buildRandomSentence(int ngram);
}
