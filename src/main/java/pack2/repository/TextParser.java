package pack2.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

/**
 * Created by adel on 28.05.15.
 */
public interface TextParser {

    public String loadFolder(String folder) throws FileNotFoundException;

    String load(String file) throws FileNotFoundException;

    String load(File file) throws FileNotFoundException;

    String load(URL url) throws IOException;
}
