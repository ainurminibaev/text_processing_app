package pack2.repository;

import pack2.model.Data;

import java.io.File;

/**
 * Created by giylmi on 21.05.2015.
 */
public interface DataWriter {

    boolean writeData(Data data, File file);


}
