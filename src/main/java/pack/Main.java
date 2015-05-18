package pack;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack.config.*;
import pack.repository.NgramRepository;
import pack.repository.TokenRepository;
import pack.service.NgramService;
import pack.service.Replacer;
import pack.service.SentenceBuilder;

import java.io.FileNotFoundException;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class Main {

    public static final int NGRAM = 3;
    public static final boolean CLEAN_PREVIOUS = false;

    public static void main(String[] args) throws FileNotFoundException {
        //define context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CoreConfig.class, DataSourceConfig.class, PersistenceConfig.class, CachingConfig.class);
        NgramRepository ngramRepository = context.getBean(NgramRepository.class);
        TokenRepository tokenRepository = context.getBean(TokenRepository.class);
        NgramService ngramService = context.getBean(NgramService.class);
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);

        //clean previous work
        if (CLEAN_PREVIOUS) {
            tokenRepository.deleteAll();
            ngramRepository.deleteAll();
            String text = ngramService.loadFile("a.txt");
            ngramService.buildNgram(text, NGRAM);
        }
        String[] words = "my best video pile".split("\\s");
        Util.shuffleArray(words);
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            System.out.print(word + " ");
        }
        System.out.println();
        String sentence = sentenceBuilder.buildSentence(words, NGRAM);
        System.out.println(sentence);

        Replacer replacer = context.getBean(Replacer.class);
        replacer.replace("broke up into ? masses", NGRAM);
    }
}
