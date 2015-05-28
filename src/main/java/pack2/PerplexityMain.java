package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.repository.DataReader;
import pack2.service.NgramService;

import java.io.FileInputStream;
import java.io.IOException;

import static pack2.Constants.*;

/**
 * Created by ainurminibaev on 28.05.15.
 */
public class PerplexityMain {
    public static void main(String[] args) throws IOException {
        int ngramSize;
        String inputFile;
        String testTextFile;
        try {
            ngramSize = Integer.valueOf(args[0].substring(NGRAM_PARAM.length()));
            inputFile = args[1].substring(MODEL_DATA_IN_PARAM.length());
            testTextFile = args[2].substring(TEST_MODEL_TEXT_PARAM.length());
        } catch (Exception e) {
            ngramSize = DEFAULT_NGRAM_SIZE;
            inputFile = "dump.bin";
            testTextFile = "a.txt";
        }
        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
        NgramService ngramService = context.getBean(NgramService.class);
        DataReader reader = context.getBean(DataReader.class);
        reader.restoreFromStream(new FileInputStream(inputFile));
        System.out.println(ngramService.calculatePerplexity(testTextFile));

    }
}
