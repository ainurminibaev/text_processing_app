import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import pack2.Constants;
import pack2.Defaults;
import pack2.model.Data;
import pack2.repository.TextParser;
import pack2.service.NgramService;

import java.io.*;

/**
 * Created by ainurminibaev on 28.05.15.
 */
public class NgramServiceTest extends BaseTest {

    @Autowired
    NgramService ngramService;

    @Autowired
    TextParser textParser;

    @Test
    public void test() throws FileNotFoundException {
        String fname = "filename.txt";
        try (PrintStream out = new PrintStream(new FileOutputStream(fname))) {
            out.print(FILE_DATA);
        }
        String result = textParser.load(fname);
        Assert.assertNotSame(FILE_DATA, result);
        Assert.assertTrue(result.contains(Constants.END_TOKEN));
        Assert.assertTrue(result.contains(Constants.START_TOKEN));
        new File(fname).delete();
    }

    @Test
    public void testSubStr() {
        String[] wordsList = FILE_DATA.split(" ");
        int countOfSubString = ngramService.getCountOfSubString(FILE_DATA_ELEMENT, null, wordsList);
        Assert.assertEquals(1, countOfSubString);

        countOfSubString = ngramService.getCountOfSubString("123", null, wordsList);
        Assert.assertEquals(0, countOfSubString);
    }

    @Test
    public void testBuildData() throws FileNotFoundException {
        int ngramSize = 2;
        String outputFileName = "test-out.bin";
        Data data = ngramService.buildNgram("input", ngramSize, "output", Defaults.unknownWordFreq);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.ngrams);
        FileSystemUtils.deleteRecursively(new File(outputFileName));
    }
}
