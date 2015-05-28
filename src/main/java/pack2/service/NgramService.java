package pack2.service;

import pack2.model.Data;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface NgramService {


    String loadFile(String file) throws FileNotFoundException;

    Data buildNgram(String text, int ngramSize, String filename);

    int getCountOfSubString(String left, String right, String[] words);

    double calculatePerplexity(String trainingModelFile, String testTextFile) throws IOException;
}
