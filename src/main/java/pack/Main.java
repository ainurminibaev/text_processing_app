package pack;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack.config.*;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.NgramService;
import pack.service.Replacer;
import pack.service.SentenceBuilder;

import java.io.FileNotFoundException;

import static pack.Constants.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class Main {


    public static void main(String[] args) throws FileNotFoundException {
        int guessNum;
        String inputWords;
        String replacerText;
        try {
            inputWords = args[0];
            replacerText = args[1];
            guessNum = Integer.valueOf(args[2].substring(GUESS_NUM_PARAM.length()));
        } catch (Exception e) {
            guessNum = 4;
            inputWords = "when you want to see any thing";
            replacerText = "He asked ? to let";
        }

        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, DataSourceConfig.class, PersistenceConfig.class, CachingConfig.class);
        NgramRepository ngramRepository = context.getBean(NgramRepository.class);
        TokenRepository tokenRepository = context.getBean(TokenRepository.class);
        NgramService ngramService = context.getBean(NgramService.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);


        String[] words = inputWords.split("\\s");
        Util.shuffleArray(words);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.print(word + " ");
        }
        System.out.println();
        String sentence = sentenceBuilder.buildSentence(words);
        System.out.println(sentence);

        Replacer replacer = context.getBean(Replacer.class);
        replacer.replace(replacerText, guessNum);
    }
}
