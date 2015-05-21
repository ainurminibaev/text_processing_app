package pack2.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack2.Constants;
import pack2.Util;
import pack2.model.Data;
import pack2.model.Ngram;
import pack2.repository.DataWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by giylmi on 22.05.2015.
 */
@Service
public class NgramServiceImpl implements NgramService {
    @Autowired
    private DataWriter dataWriter;

    @Override
    public String loadFile(String file) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            StringBuilder textBuilder = new StringBuilder();
            BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                textBuilder.append(Util.getMarkedLine(line, sentenceIterator));
                textBuilder.append('\n');
            }
            return textBuilder.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    @Override
    public void buildNgram(String text, int ngramSize, String filename) {
//TODO  [\\s,;\\n\\t]+
        Data data = new Data();
        ArrayList<String> wordsList = Lists.newArrayList(text.split("[\\s\\n\\t]+"));
        cleanWordSetFromTrunk(wordsList);
        String[] words = new String[wordsList.size()];
        wordsList.toArray(words);
        HashSet<String> wordsSet = Sets.newHashSet(words);
        if (words.length < ngramSize) {
            return;
        }
        int p = 0;
        for (int i = 0; i < words.length - ngramSize + 1; i++) {
            Ngram ngram = new Ngram(ngramSize);
            String[] tokens = new String[ngramSize];
            boolean canBeNgram = true;
            for (int j = 0; j < ngramSize; j++) {
                String word = words[i + j];
                if ((word.equals(Constants.START_TOKEN) && j != 0) || (word.equals(Constants.END_TOKEN) && j != ngramSize - 1)) {
                    canBeNgram = false;
                    break;
                }
                tokens[j] = word;
            }
            if (canBeNgram) {
                ngram.fillTokens(tokens);
                ngram.probability = calculateNgramProvability(ngram, words, wordsSet.size());
                data.addNgram(ngram);
            }

            if (i * 100.0 / words.length - p > 10) {
                System.out.println(i * 100.0 / words.length + " words have been parsed");
                p = (int) (i * 100.0 / words.length);
            }
        }
        System.out.println("All words have been parsed!");
        dataWriter.writeData(data, new File(filename));
    }

    @Override
    public int getCountOfSubString(String left, String right, String[] words) {
        return 0;
    }

    /**
     * Расчет вероятность для Ngram
     * Pn = P(w1,w2)*P(w2,w3) ....
     *
     * @return
     */
    private Double calculateNgramProvability(Ngram ngram, String[] words, int wordSetSize) {
        double totalProbab = 1;
        for (int i = 0; i < ngram.tokens.length - 1; i++) {
            String[] tokens = ngram.tokens;
            totalProbab *= getProbabilityForPair(tokens[i], tokens[i + 1], words, wordSetSize);
        }
        return totalProbab;
    }

    private Double getProbabilityForPair(String left, String right, String[] words, int wordSetSize) {
        return (getCountOfSubString(left, right, words) + 1) / ((double) getCountOfSubString(left, null, words) + wordSetSize);
    }


    /**
     * Избавляемся от пустышек, символов
     *
     * @param wordsList
     */
    private void cleanWordSetFromTrunk(ArrayList<String> wordsList) {
        for (int i = 0; i < wordsList.size(); i++) {
            String token = wordsList.get(i);
            //пропускаем признаки старта и конца
            if (token.equals(Constants.END_TOKEN) || token.equals(Constants.START_TOKEN)) {
                continue;
            }
            //удаляем старое слово, чтобы слова вставить
            wordsList.remove(i);
            token = token.replaceAll("[^\\w,]", "");
            if (token.trim().length() == 0) {
                //пропускаем слова-пустышки
                i--;
                continue;
            }
            //избавляемся от всего, кроме букв и запятых
            wordsList.add(i, token);
        }
    }
}
