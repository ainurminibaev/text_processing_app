package pack2;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface Constants {

    static String START_TOKEN = "<s>";
    static String END_TOKEN = "</s>";
    static String START_END_TOKEN = " " + END_TOKEN + " " + START_TOKEN + " ";
    String UNKNOWN_WORD_MARKER = "UNKW";

    String DATA_FILE_NAME = "data";
    String DATA_FILE_EXT = ".bin";
}
