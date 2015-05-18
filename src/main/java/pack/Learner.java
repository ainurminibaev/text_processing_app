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

/**
 * Created by giylmi on 19.05.2015.
 */
public class Learner {

    private static final int NGRAM = 3;

    public static void main(String[] args) throws FileNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, DataSourceConfig.class, PersistenceConfig.class, CachingConfig.class);
        NgramRepository ngramRepository = context.getBean(NgramRepository.class);
        TokenRepository tokenRepository = context.getBean(TokenRepository.class);
        NgramService ngramService = context.getBean(NgramService.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        //clean previous work
        ngramRepository.deleteAll();
        tokenRepository.deleteAll();
        String text = ngramService.loadFile("a.txt");
        ngramService.buildNgram(text, NGRAM);
    }
}
