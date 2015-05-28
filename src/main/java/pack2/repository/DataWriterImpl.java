package pack2.repository;

import org.springframework.stereotype.Repository;
import pack2.model.Data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by giylmi on 22.05.2015.
 */
@Repository
public class DataWriterImpl implements DataWriter {
    @Override
    public boolean writeData(Data data, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}