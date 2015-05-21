package pack2.repository;

import pack2.model.Data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface DataWriter {

    boolean writeData(Data data, File file);


}
