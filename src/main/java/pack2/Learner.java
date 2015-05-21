package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.service.NgramService;

import java.io.FileNotFoundException;

/**
 * Created by giylmi on 19.05.2015.
 */
public class Learner {

    private static final int NGRAM = 3;

    public static void main(String[] args) throws FileNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);

        NgramService ngramService = context.getBean(NgramService.class);
        ngramService.buildNgram(ngramService.loadFile("lotr.txt"), NGRAM, "dump.bin");
    }
}
