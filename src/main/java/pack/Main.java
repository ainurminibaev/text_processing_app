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
        int ngramSize;
        int guessNum;
        String inputWords;
        String replacerText;
        try {
            ngramSize = Integer.valueOf(args[0].substring(NGRAM_PARAM.length()));
            inputWords = args[1];
            replacerText = args[2];
            guessNum = Integer.valueOf(args[4].substring(GUESS_NUM.length()));
        } catch (Exception e) {
            ngramSize = NGRAM;
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
        String sentence = sentenceBuilder.buildSentence(words, ngramSize);
        System.out.println(sentence);

        Replacer replacer = context.getBean(Replacer.class);
        replacer.replace(replacerText, ngramSize, guessNum);
    }
}
