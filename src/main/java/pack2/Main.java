package pack2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.service.Replacer;
import pack2.service.SentenceBuilder;

import static pack2.Constants.GUESS_NUM_PARAM;

import java.io.FileNotFoundException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        int guessNum;
        String inputWords;
        String replacerText;
        Integer ngramSize;
        try {
            inputWords = args[0];
            replacerText = args[1];
            guessNum = Integer.valueOf(args[2].substring(GUESS_NUM_PARAM.length()));
            ngramSize = Integer.valueOf(args[3]);
        } catch (Exception e) {
            guessNum = 4;
            inputWords = "when you want to see any thing";
            replacerText = "He asked ? to let";
            ngramSize = 3;
        }

        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        String[] words = inputWords.split("\\s");
        Util.shuffleArray(words);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.print(word + " ");
        }
        System.out.println();
        String sentence = sentenceBuilder.buildSentence(words, ngramSize);
        System.out.println(sentence);

        Replacer replacer = context.getBean(Replacer.class);
        replacer.replace(replacerText, guessNum, ngramSize);
    }
}
