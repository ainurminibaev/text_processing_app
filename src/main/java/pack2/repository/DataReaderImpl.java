package pack2.repository;

import org.springframework.stereotype.Repository;
import pack2.Constants;
import pack2.model.Data;

import java.io.*;

/**
 * Created by giylmi on 21.05.2015.
 */
@Repository
public class DataReaderImpl implements DataReader {

    private Data data;

    @Override
    public Data restoreData(int ngramSize, String pathToFolder) throws IOException {
        return restoreFromStream(new FileInputStream(pathToFolder + File.separator + Constants.DATA_FILE_NAME + ngramSize + Constants.DATA_FILE_EXT));
    }

    @Override
    public Data getData() {
        return data;
    }

    private Data restoreFromStream(InputStream inputStream) throws IOException {
        try(ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            data = (Data) ois.readObject();
            return data;
        } catch (InvalidClassException e) {
            System.out.println("dump is not valid for current version, cannot restore: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("dump is not valid, cannot restore: " + e.getMessage());
        } catch (EOFException e) {
            System.out.println("looks like dump is corrupted: unexpected EOF was found, cannot restore: " + e.getMessage());
        }
        return null;
    }
}
