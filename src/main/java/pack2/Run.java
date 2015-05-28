package pack2;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.model.Data;
import pack2.repository.DataReader;
import pack2.service.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by adel on 28.05.15.
 */
public class Run {

    static Logger logger = LoggerFactory.getLogger(Run.class);

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            Arguments arguments = Arguments.resolveArguments(args);
            logger.info("Resolved arguments: " + arguments);
            switch (arguments.command) {
                case LEARN:
                    learn(arguments);
                    break;
                case RESTORE_SENTENCE_SHUFFLE:
                    restoreSentence(arguments);
                    break;
                case RESTORE_SENTENCE_RANDOM:
                    buildSentence(arguments);
                    break;
                case REPLACE:
                    replace(arguments);
                    break;
                case PERPLEXITY:
                    perplexity(arguments);
                    break;
            }
        } else {
            System.out.println("No arguments provided - do nothing!");
        }
    }

    private static void perplexity(Arguments arguments) {
        AnnotationConfigApplicationContext context = getAnnotationConfigApplicationContext();
        DataReader dataReader = context.getBean(DataReader.class);
        PerplexityService perplexityService = context.getBean(PerplexityService.class);
        NgramService ngramService = context.getBean(NgramService.class);
        try {
            restoreData(arguments, dataReader);
            String input = arguments.getArgument("test-dir", Defaults.testFolder);
            Integer ngramSize = arguments.getArgument("n", Defaults.ngramSize, new Function<String, Integer>() {
                @Override
                public Integer apply(String s) {
                    return Integer.valueOf(s);
                }
            });
            Double notUsedWordsProbability = arguments.getArgument("unknown-word-freq", Defaults.unknownWordFreq, new Function<String, Double>() {
                @Override
                public Double apply(String s) {
                    return Double.valueOf(s);
                }
            });
            logger.info("Learning test data");
            Data testData = ngramService.buildNgram(input, ngramSize, null, notUsedWordsProbability);
            logger.info("perplexity: " + perplexityService.calculatePerplexity(dataReader.getData(), testData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void replace(Arguments arguments) {
        AnnotationConfigApplicationContext context = getAnnotationConfigApplicationContext();
        DataReader dataReader = context.getBean(DataReader.class);
        Replacer replacer = context.getBean(Replacer.class);
        try {
            restoreData(arguments, dataReader);
            String sentence = arguments.getArgument("sentence", Defaults.REPLACER_ARG);
            if (sentence == null || sentence.isEmpty()) {
                logger.error("No sentence provided");
            } else {
                Integer guessNum = arguments.getArgument("guess-num", Defaults.guessNum, new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) {
                        return Integer.valueOf(s);
                    }
                });
                logger.info(replacer.replace(sentence, guessNum));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildSentence(Arguments arguments) {
        AnnotationConfigApplicationContext context = getAnnotationConfigApplicationContext();
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);
        DataReader dataReader = context.getBean(DataReader.class);
        try {
            restoreData(arguments, dataReader);
            logger.info("Building random sentence");
            logger.info(sentenceBuilder.buildSentence());
            logger.info("Sentence built");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void restoreSentence(Arguments arguments) {
        AnnotationConfigApplicationContext context = getAnnotationConfigApplicationContext();
        SentenceBuilder sentenceBuilder = context.getBean(SentenceBuilder.class);
        DataReader dataReader = context.getBean(DataReader.class);

        String[] words = arguments.getArgument("words", Defaults.SHUFFLE_TEXT, new Function<String, String[]>() {
            @Override
            public String[] apply(String s) {
                return s.split(",");
            }
        });
        if (words == null) {
            logger.error("No words provided");
        } else {
            try {
                restoreData(arguments, dataReader);
                logger.info("Building sentence from words=" + Arrays.toString(words));
                logger.info(sentenceBuilder.buildSentence(words));
                logger.info("Sentence built end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void restoreData(Arguments arguments, DataReader dataReader) throws IOException {
        Integer ngramSize = arguments.getArgument("n", Defaults.ngramSize, new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return Integer.valueOf(s);
            }
        });
        String model = arguments.getArgument("lm", Defaults.outputFolder);
        dataReader.restoreData(ngramSize, model);
    }

    private static void learn(Arguments arguments) {
        AnnotationConfigApplicationContext context = getAnnotationConfigApplicationContext();
        NgramService ngramService = context.getBean(NgramService.class);

        try {
            logger.info("Learning has begun");
            String inputFolder = arguments.getArgument("src-texts", Defaults.inputFolder);
            Integer n = arguments.getArgument("n", Defaults.ngramSize, new Function<String, Integer>() {
                @Override
                public Integer apply(String s) {
                    return Integer.valueOf(s);
                }
            });
            String outputFolder = arguments.getArgument("o", Defaults.outputFolder);
            Double notUsedWordsProbability = arguments.getArgument("unknown-word-freq", Defaults.unknownWordFreq, new Function<String, Double>() {
                @Override
                public Double apply(String s) {
                    return Double.valueOf(s);
                }
            });
            ngramService.buildNgram(inputFolder, n, outputFolder, notUsedWordsProbability);
            logger.info("Learning has been ended");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static AnnotationConfigApplicationContext getAnnotationConfigApplicationContext() {
        return new AnnotationConfigApplicationContext(CoreConfig.class, CachingConfig.class);
    }

    private static class Arguments {

        Command command;
        private Map<String, String> argsMap = new HashMap<>();

        public String getArgument(String key) {
            return argsMap.get(key);
        }

        public String getArgument(String key, String defaultValue) {
            String s = argsMap.get(key);
            if (s == null) {
                logger.info("Reverting to default value for key=" + key + ", default=" + defaultValue);
                return defaultValue;
            }
            return s;
        }

        public <T> T getArgument(String key, T defaultValue, Function<String, T> transformFunction) {
            String s = argsMap.get(key);
            if (s == null) {
                logger.info("Reverting to default value for key=" + key + ", default=" + defaultValue);
                return defaultValue;
            }
            return transformFunction.apply(s);
        }

        @Override
        public String toString() {
            return "Arguments{" +
                    "command=" + command +
                    ", argsMap=" + argsMap +
                    '}';
        }

        private Arguments() {
        }

        public static Arguments resolveArguments(String[] args) {
            Arguments arguments = new Arguments();
            String name = args[0];
            try {
                arguments.command = Command.byName(name);
            } catch (Exception e) {
                logger.error("Couldn't resolve command argument: \"" + name + "\"\nAvailable arguments: " + Joiner.on(",").join(Command.valuesList()));
            }
            for (int i = 1; i < args.length; i++) {
                int j = -1;
                while (args[i].charAt(j + 1) == '-') {
                    j++;
                }
                if (j > -1) {
                    String argument = args[i].substring(j + 1);
                    String[] keyValuePair = argument.split("=");
                    String key = keyValuePair[0];
                    StringBuilder valueBuilder = new StringBuilder();
                    for (int k = 1; k < keyValuePair.length; k++) {
                        valueBuilder.append(keyValuePair[k]);
                    }
                    arguments.argsMap.put(key, valueBuilder.toString());
                }
                /*else ignore argument*/
            }

            return arguments;
        }

        enum Command {
            LEARN("learn"),
            RESTORE_SENTENCE_RANDOM("restore-sentence-random"),
            RESTORE_SENTENCE_SHUFFLE("restore-sentence-shuffle"),
            REPLACE("replace"),
            PERPLEXITY("perplexity");

            private String name;

            Command(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public static List valuesList() {
                return Lists.transform(Arrays.asList(values()), new Function<Command, String>() {
                    @Override
                    public String apply(Command command) {
                        return command.name;
                    }
                });
            }

            public static Command byName(String s) {
                for (Command c : Command.values())
                    if (c.name.equals(s.toLowerCase()))
                        return c;
                throw new RuntimeException();
            }
        }
    }
}
