package pack2.service;

import pack2.model.Data;

import java.io.FileNotFoundException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public interface NgramService {

    Data buildNgram(String inputFolder, int ngramSize, String outputFolder, double notUsedWordsProbability) throws FileNotFoundException;

    int getCountOfSubString(String left, String right, String[] words);

}
