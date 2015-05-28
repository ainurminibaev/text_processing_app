package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.repository.DataReader;
import pack2.service.SentenceBuilder;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class SentenceBuilderShuffle {


    public static void main(String[] args) throws IOException {
        String inputWords;
        String inputFile;
        try {
            inputWords = args[0];
            inputFile = args[3];
        } catch (Exception e) {
            inputWords = "when you want to see any thing";
            inputFile = "dump.bin";
        }

        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
        DataReader dataReader = context.getBean(DataReader.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        dataReader.restoreFromStream(new FileInputStream(inputFile));

        String[] words = inputWords.split("\\s");
        Util.shuffleArray(words);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.print(word + " ");
        }
        System.out.println();
        String sentence = sentenceBuilder.buildSentence(words);
        System.out.println(sentence);
    }
}
