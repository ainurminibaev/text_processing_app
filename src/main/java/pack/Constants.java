package pack;

/**
 * Created by ainurminibaev on 19.05.15.
 */
public interface Constants {
    String START_FLAG = "<s>";
    String END_FLAG = "</s>";
    String START_END_FLAG = ' ' + END_FLAG + ' ' + START_FLAG + ' ';

    String NGRAM_PARAM = "--n=";
    String MODEL_DATA_PARAM = "--file=";
    String MODEL_DATA_OUT_PARAM = "--output=";
    String MODEL_DATA_IN_PARAM = "--input=";
    String GUESS_NUM_PARAM = "--guess-num=";
    String UNKNOWN_WORD_PARAM = "--unknown-word-freq";
    String UNKNOWN_WORD_MARKER = "UNKW";

    int NGRAM = 2;
}
