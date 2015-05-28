package pack2;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface Constants {

    static String START_TOKEN = "<s>";
    static String END_TOKEN = "</s>";
    static String START_END_TOKEN = " " + END_TOKEN + " " + START_TOKEN + " ";
    String GUESS_NUM_PARAM = "--guess-num=";
    String NGRAM_PARAM = "--n=";
    String MODEL_DATA_PARAM = "--file=";
    String MODEL_DATA_IN_PARAM = "--input=";
    String MODEL_DATA_OUT_PARAM = "--output=";
    String TEST_MODEL_TEXT_PARAM = "--test=";
    String UNKNOWN_WORD_PARAM = "--unknown-word-freq=";
    String UNKNOWN_WORD_MARKER = "UNKW";

    Integer DEFAULT_NGRAM_SIZE = 3;

}
