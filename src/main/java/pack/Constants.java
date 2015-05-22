package pack;

/**
 * Created by ainurminibaev on 19.05.15.
 */
public interface Constants {
    String START_FLAG = "<s>";
    String END_FLAG = "</s>";
    String START_END_FLAG = ' ' + END_FLAG + ' ' + START_FLAG + ' ';

    String NGRAM_PARAM = "--n=";
    String MODEL_DATA = "--file=";
    int NGRAM = 2;
}
