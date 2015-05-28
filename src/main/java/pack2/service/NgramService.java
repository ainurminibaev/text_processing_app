package pack2.service;

import pack2.model.Data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface NgramService {

    Data buildNgram(String inputFolder, int ngramSize, String outputFolder, double notUsedWordsProbability) throws FileNotFoundException;

    String load(String file) throws FileNotFoundException;

    String load(URL url) throws IOException;

    Data buildNgram(String text, int ngramSize, String filename);

    int getCountOfSubString(String left, String right, String[] words);

}
