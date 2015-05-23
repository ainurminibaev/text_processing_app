package pack;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack.config.CachingConfig;
import pack.config.CoreConfig;
import pack.config.DataSourceConfig;
import pack.config.PersistenceConfig;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.NgramService;
import pack.service.SentenceBuilder;

import java.io.FileNotFoundException;

import static pack.Constants.*;

/**
 * Created by giylmi on 19.05.2015.
 */
public class Learner {


    public static void main(String[] args) throws FileNotFoundException {
        int ngramSize;
        String modelData;
        double unkWordProbability;
        try {
            ngramSize = Integer.valueOf(args[0].substring(NGRAM_PARAM.length()));
            modelData = args[1].substring(MODEL_DATA_PARAM.length());
            unkWordProbability = Double.valueOf(args[2].substring(UNKNOWN_WORD_PARAM.length()));
        } catch (Exception e) {
            ngramSize = NGRAM;
            modelData = "a.txt";
            unkWordProbability = 0.000364498;
        }
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, DataSourceConfig.class, PersistenceConfig.class, CachingConfig.class);
        NgramRepository ngramRepository = context.getBean(NgramRepository.class);
        TokenRepository tokenRepository = context.getBean(TokenRepository.class);
        NgramService ngramService = context.getBean(NgramService.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        //clean previous work
        ngramRepository.deleteAll();
        String text = ngramService.loadFile(modelData);
        ngramService.buildNgram(text, ngramSize, unkWordProbability);
    }
}
