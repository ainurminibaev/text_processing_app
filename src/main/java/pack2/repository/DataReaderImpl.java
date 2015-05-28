package pack2.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pack2.Constants;
import pack2.model.Data;

import java.io.*;

/**
 * Created by giylmi on 21.05.2015.
 */
@Repository
public class DataReaderImpl implements DataReader {

    Logger logger = LoggerFactory.getLogger(DataReaderImpl.class);

    private Data data;

    @Override
    public Data restoreData(int ngramSize, String pathToFolder){
        String name = pathToFolder + File.separator + Constants.DATA_FILE_NAME + ngramSize + Constants.DATA_FILE_EXT;
        try {
            return restoreFromStream(new FileInputStream(name));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No dump with path=" + name + " found. Learn first!");
        }
    }

    @Override
    public Data getData() {
        return data;
    }

    private Data restoreFromStream(InputStream inputStream){
        try(ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            data = (Data) ois.readObject();
            return data;
        } catch (InvalidClassException e) {
            logger.error("dump is not valid for current version, cannot restore: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("dump is not valid, cannot restore: " + e.getMessage());
        } catch (EOFException e) {
            logger.error("looks like dump is corrupted: unexpected EOF was found, cannot restore: " + e.getMessage());
        } catch (IOException e) {
            logger.error("something went wrong with your stream");
        }
        return null;
    }
}
