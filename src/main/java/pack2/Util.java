package pack2;

import pack2.model.Ngram;

import java.io.File;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static pack2.Constants.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class Util {

    // Implementing Fisher–Yates shuffle
    public static void shuffleArray(Object[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }


    /**
     * Выделяет места, куда нужно вставить признак начала/конца
     * Начало - True
     * Конец - False
     * @return
     */
    public static Map<Integer, Boolean> getStartEndMappings(String line, BreakIterator sentenceIterator) {
        boolean isStartFlag = true;
        sentenceIterator.setText(line);
        int flagIndex = sentenceIterator.current();
        Map<Integer, Boolean> insertMap = new HashMap<>();
        while (flagIndex != -1) {
            if (isStartFlag) {
                insertMap.put(flagIndex, true);
                isStartFlag = false;
            } else {
                insertMap.put(flagIndex, false);
                isStartFlag = true;
            }
            flagIndex = sentenceIterator.next();
        }
        return insertMap;
    }


    /**
     * Вставляет признаки начала и конца
     * @return
     */
    public static String getMarkedLine(String line, BreakIterator sentenceIterator) {
        Map<Integer, Boolean> insertMap = getStartEndMappings(line, sentenceIterator);
        StringBuilder str = new StringBuilder(line.length());
        boolean isFirstOccurance = true;
        for (int i = 0; i < line.length(); i++) {
            if (insertMap.containsKey(i)) {
                Boolean isStart = insertMap.get(i);
                if (!isFirstOccurance || (insertMap.size() != 1 && !isFirstOccurance)) {
                    str.append(START_END_TOKEN);
                } else {
                    str.append(' ');
                    str.append(isStart ? START_TOKEN : END_TOKEN);
                    str.append(' ');
                }
                isFirstOccurance = false;
                insertMap.remove(i);
            }
            str.append(line.charAt(i));
        }
        for (Map.Entry<Integer, Boolean> e : insertMap.entrySet()) {
            str.append(e.getValue() ? START_TOKEN : END_TOKEN);
        }
        return str.toString();

    }

    public static String buildFileName(String pathToFolder, int ngramSize){
        return pathToFolder + File.separator + Constants.DATA_FILE_NAME + ngramSize + Constants.DATA_FILE_EXT;
    }

    public static Ngram randomNgram(List<Ngram> ngramList){
        Random randomer = new Random();
        double total = totalPropability(ngramList);
        double probability = randomer.nextDouble() % total;
        double sum = 0;
        for (Ngram ngram : ngramList) {
            if (probability < sum + ngram.probability) {
                return ngram;
            } else {
                sum += ngram.probability;
            }
        }
        return null;
    }

    private static double totalPropability(List<Ngram> nextNgrams) {
        if (nextNgrams == null) return 0;
        double sum = 0;
        for (Ngram ngram : nextNgrams) {
            sum += ngram.probability;
        }
        return sum;
    }
}
