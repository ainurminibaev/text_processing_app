package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.service.NgramService;

import java.io.FileNotFoundException;

import static pack.Constants.*;

/**
 * Created by giylmi on 19.05.2015.
 */
public class Learner {


    public static void main(String[] args) throws FileNotFoundException {
        int ngramSize;
        String modelData;
        String outputFile;
        try {
            ngramSize = Integer.valueOf(args[0].substring(NGRAM_PARAM.length()));
            modelData = args[1].substring(MODEL_DATA.length());
            outputFile = args[2].substring(MODEL_DATA_OUT.length());
        } catch (Exception e) {
            ngramSize = NGRAM;
            modelData = "lotr.txt";
            outputFile = "dump.bin";
        }
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);

        NgramService ngramService = context.getBean(NgramService.class);
        ngramService.buildNgram(ngramService.loadFile(modelData), ngramSize, outputFile);
    }
}
