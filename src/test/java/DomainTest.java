import org.junit.Assert;
import org.junit.Test;
import pack2.model.Ngram;

/**
 * Created by ainurminibaev on 28.05.15.
 */
public class DomainTest {

    @Test
    public void equalityTest() {
        Ngram ngram1 = new Ngram(2);
        ngram1.fillTokens("a", "b");
        Ngram ngram2 = new Ngram(2);
        ngram2.fillTokens("a", "b");
        Assert.assertEquals(ngram1, ngram2);
        Assert.assertEquals(ngram1.hashCode(), ngram2.hashCode());
    }
}
