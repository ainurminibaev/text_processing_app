import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pack2.config.CachingConfig;
import pack2.config.CoreConfig;

/**
 * Created by ainurminibaev on 28.05.15.
 */
@ContextConfiguration(classes = {
        CoreConfig.class, CachingConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseTest {
    protected static final String FILE_DATA = "my data from universe";
    protected static final String FILE_DATA_ELEMENT = "data";
}
