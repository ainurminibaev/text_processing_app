package pack.service;

import java.io.FileNotFoundException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface NgramService {


    String loadFile(String file) throws FileNotFoundException;

    void buildNgram(String text, int ngramSize, double uselessWordsProbability);

    int getCountOfSubString(String left, String right, String[] words);
}
