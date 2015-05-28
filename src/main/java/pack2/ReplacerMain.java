package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.repository.DataReader;
import pack2.service.Replacer;

import java.io.FileInputStream;
import java.io.IOException;

import static pack2.Constants.GUESS_NUM_PARAM;

/**
 * Created by ainurminibaev on 28.05.15.
 */
public class ReplacerMain {
    public static void main(String[] args) throws IOException {
        int guessNum;
        String replacerText;
        String inputFile;
        try {
            replacerText = args[1];
            guessNum = Integer.valueOf(args[2].substring(GUESS_NUM_PARAM.length()));
            inputFile = args[3];
        } catch (Exception e) {
            guessNum = 4;
            replacerText = "He asked ? to let";
            inputFile = "dump.bin";
        }

        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
        DataReader dataReader = context.getBean(DataReader.class);

        dataReader.restoreFromStream(new FileInputStream(inputFile));

        Replacer replacer = context.getBean(Replacer.class);
        replacer.replace(replacerText, guessNum);
    }
}
