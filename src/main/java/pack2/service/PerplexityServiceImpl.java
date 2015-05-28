package pack2.service;

import org.springframework.stereotype.Service;
import pack2.model.Data;
import pack2.model.Ngram;

import java.io.IOException;
import java.util.List;

/**
 * Created by adel on 28.05.15.
 */
@Service
public class PerplexityServiceImpl implements PerplexityService {
    /**
     * Считаем perplexity(PP) для тестовой модели и тренировочной
     *
     * @throws IOException
     */
    @Override
    public double calculatePerplexity(Data trainingData, Data testData) throws IOException {
        List<Ngram> trainingNgrams = trainingData.ngrams;
        List<Ngram> testNgrams = testData.ngrams;
        double perplexity = 0;
        for (Ngram testNgram : testNgrams) {
            for (Ngram trainingNgram : trainingNgrams) {
                if (testNgram.equals(trainingNgram)) {
                    perplexity = Math.log10(trainingNgram.probability) / Math.log10(2);
                }
            }
        }
        // Divide with minus of the test corpus size
        perplexity /= -1 * testNgrams.size();
        // calculate 2 power the previous result
        perplexity = Math.pow(2, perplexity);
        return perplexity;
    }
}
