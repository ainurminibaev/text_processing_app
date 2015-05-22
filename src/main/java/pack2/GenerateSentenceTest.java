package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.repository.DataReader;
import pack2.service.SentenceBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import static pack.Constants.*;
/**
 * Created by giylmi on 18.05.2015.
 */
public class GenerateSentenceTest {


    public static void main(String[] args) throws IOException {
        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        DataReader reader = context.getBean(DataReader.class);
        reader.restoreFromStream(new FileInputStream("dump.bin"));
        System.out.println(sentenceBuilder.buildSentence(NGRAM));
    }
}
