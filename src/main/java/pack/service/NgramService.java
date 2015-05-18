package pack.service;

import java.io.FileNotFoundException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface NgramService {
    static final String START_FLAG = "<s>";
    static final String END_FLAG = "</s>";


    String loadFile(String file) throws FileNotFoundException;

    void buildNgram(String text, int ngramSize);

    int getCountOfSubString(String left, String right, String[] words);
}
