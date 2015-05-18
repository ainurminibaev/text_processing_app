package pack;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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


    public static String getMarkedLine(String line, BreakIterator sentenceIterator) {
        Map<Integer, Boolean> insertMap = getStartEndMappings(line, sentenceIterator);
        StringBuilder str = new StringBuilder(line.length());
        boolean isFirstOccurance = true;
        for (int i = 0; i < line.length(); i++) {
            if (insertMap.containsKey(i)) {
                Boolean isStart = insertMap.get(i);
                if (!isFirstOccurance || (insertMap.size() != 1 && !isFirstOccurance)) {
                    str.append(Constants.START_END_FLAG);
                } else {
                    str.append(isStart ? Constants.START_FLAG : Constants.END_FLAG);
                }
                isFirstOccurance = false;
                insertMap.remove(i);
            }
            str.append(line.charAt(i));
        }
        for (Map.Entry<Integer, Boolean> e : insertMap.entrySet()) {
            str.append(e.getValue() ? Constants.START_FLAG : Constants.END_FLAG);
        }
        return str.toString();

    }
}
