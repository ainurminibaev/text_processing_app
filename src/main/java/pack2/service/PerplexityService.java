package pack2.service;

import pack2.model.Data;
import pack2.model.Ngram;

import java.io.IOException;
import java.util.List;

/**
 * Created by adel on 28.05.15.
 */
public interface PerplexityService {

    /**
     * Считаем perplexity(PP) для тестовой модели и тренировочной
     *
     * @throws java.io.IOException
     */
    public double calculatePerplexity(Data trainingData, Data testData) throws IOException;
}
