package pack2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack2.model.Ngram;
import pack2.model.Data;
import pack2.repository.DataReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by giylmi on 21.05.2015.
 */
@Service
public class SentenceBuilderImpl implements SentenceBuilder {

    @Autowired
    private DataReader dataReader;

    @Override
    public String buildSentence(int ngramSize) {
        Data data = dataReader.getData();
        if (data == null) throw new RuntimeException("NO DATA FOUND");
        Map<Ngram, LinkedList<Ngram>> nextNgramMap = data.nextNgramMapMap.get(ngramSize);
        Ngram ngram = data.getFirstNgram(ngramSize);
        StringBuilder sentence = new StringBuilder();
        appendNgram(sentence, ngram);
        do{
            Ngram newNgram = ngram.excludeFirstTokenNgram();
            List<Ngram> nextNgrams = nextNgramMap.get(newNgram);
            if (nextNgrams == null || nextNgrams.isEmpty()) return sentence.toString();
            Random randomer = new Random();
            double total = totalPropability(nextNgrams);
            double probability = randomer.nextDouble() % total;
            double sum = 0;
            cycle: for (Ngram nextNgram: nextNgrams) {
                if (probability < sum + ngram.probability) {
                    sentence.append(nextNgram.tokens[nextNgram.size - 1]).append(" ");
                    ngram = nextNgram;
                    break cycle;
                }
                else {
                    sum += ngram.probability;
                }
            }
        } while(!ngram.tokens[ngramSize - 1].equals("</s>"));
        return sentence.toString();
    }

    private double totalPropability(List<Ngram> nextNgrams) {
        if (nextNgrams == null) return 0;
        double sum = 0;
        for (Ngram ngram: nextNgrams) {
            sum += ngram.probability;
        }
        return sum;
    }

    private void appendNgram(StringBuilder sentence, Ngram ngram) {
        for (String token: ngram.tokens)
            sentence.append(token).append(" ");
    }
}
