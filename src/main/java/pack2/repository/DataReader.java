package pack2.repository;

import pack2.model.Data;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface DataReader {

    Data restoreData(int ngramSize, String pathToFolder) throws IOException;

    Data getData();
}
