package pack2.repository;

import java.io.FileNotFoundException;

/**
 * Created by adel on 28.05.15.
 */
public interface TextParser {

    public String loadFolder(String folder) throws FileNotFoundException;
}
