package pack2;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;
import pack2.repository.DataReader;
import pack2.service.NgramService;
import pack2.service.SentenceBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adel on 28.05.15.
 */
public class Run {

    static Logger logger = LoggerFactory.getLogger(Run.class);

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            Arguments arguments = Arguments.resolveArguments(args);
            logger.info("Resolved arguments: " + arguments);
            switch (arguments.command){
                case LEARN: learn(arguments);
            }
        } else {
            System.out.println("No arguments provided - do nothing!");
        }
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

    private static class Arguments{

        Command command;
        private Map<String, String> argsMap = new HashMap<>();

        public String getArgument(String key){
            return argsMap.get(key);
        }

        public String getArgument(String key, String defaultValue){
            String s = argsMap.get(key);
            if (s == null) {
                logger.info("Reverting to default value for key=" + key + ", default=" + defaultValue);
                return defaultValue;
            }
            return s;
        }
        public <T> T getArgument(String key, T defaultValue, Function<String, T> transformFunction){
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

        private Arguments(){}
        public static Arguments resolveArguments(String[] args){
            Arguments arguments = new Arguments();
            String name = args[0];
            try {
                arguments.command = Command.valueOf(name.toUpperCase());
            } catch (Exception e) {
                System.out.println("Couldn't resolve command argument: \"" + name + "\"\nAvailable arguments: " + Joiner.on(",").join(Command.valuesList()));
            }
            for (int i = 1; i < args.length; i++) {
                int j = -1;
                while (args[i].charAt(j + 1) == '-'){
                    j++;
                }
                if (j > - 1) {
                    String argument = args[i].substring(j);
                    String[] keyValuePair = argument.split("=");
                    String key = keyValuePair[0];
                    StringBuilder valueBuilder = new StringBuilder();
                    for (int k = 1; k < keyValuePair.length; k++){
                        valueBuilder.append(keyValuePair[k]);
                    }
                    arguments.argsMap.put(key, valueBuilder.toString());
                }
                /*else ignore argument*/
            }

            return arguments;
        }

        enum Command{
            LEARN("learn");

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
        }
    }
}
