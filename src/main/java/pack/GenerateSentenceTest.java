package pack;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack.config.*;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.NgramService;
import pack.service.SentenceBuilder;

import java.io.FileNotFoundException;

/**
 * Created by giylmi on 18.05.2015.
 */
public class GenerateSentenceTest {

    public static void main(String[] args) throws FileNotFoundException {
        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, DataSourceConfig.class, PersistenceConfig.class, CachingConfig.class);
        NgramRepository ngramRepository = context.getBean(NgramRepository.class);
        TokenRepository tokenRepository = context.getBean(TokenRepository.class);
        NgramService ngramService = context.getBean(NgramService.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        System.out.println(sentenceBuilder.buildRandomSentence());
    }
}
