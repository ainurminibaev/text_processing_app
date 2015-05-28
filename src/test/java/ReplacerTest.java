import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pack2.model.Ngram;
import pack2.service.Replacer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ainurminibaev on 28.05.15.
 */
public class ReplacerTest extends BaseTest {
    @Autowired
    Replacer replacer;

    @Test
    public void testNgramCounter() {
        String[] words = "Data with ? element".split(" ");
        int skipIndex = Iterables.indexOf(Arrays.asList(words), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.equals("?");
            }
        });
        int ngramSize = 2;
        List<Ngram> ngrams = replacer.buildAllNGrams(words, skipIndex, ngramSize);
        Assert.assertEquals(2, ngrams.size());
    }
}
