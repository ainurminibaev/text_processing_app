import org.junit.Assert;
import org.junit.Test;
import pack.Util;

import java.text.BreakIterator;
import java.util.Locale;
import java.util.Map;


/**
 * Created by ainurminibaev on 19.05.15.
 */
public class StartEndMappingTest {

    @Test
    public void test() {
        String sentence = "My name Ainur. I'a a student.            A stusdfsdfsdfdent";
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        Map<Integer, Boolean> startEndMappings = Util.getStartEndMappings(sentence, sentenceIterator);
        System.out.println(Util.getMarkedLine(sentence, sentenceIterator));
        Assert.assertEquals(4, startEndMappings.size());
    }
}
