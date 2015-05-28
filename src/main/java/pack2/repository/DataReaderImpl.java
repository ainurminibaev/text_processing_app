package pack2.repository;

import org.springframework.stereotype.Repository;
import pack2.model.Data;

import java.io.*;
import java.util.Iterator;

/**
 * Created by giylmi on 21.05.2015.
 */
@Repository
public class DataReaderImpl implements DataReader {

    private Data data;

    @Override
    public Data restoreFromStream(InputStream inputStream) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
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

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public int getNgramSize() {
        Iterator<Integer> ngramSizesIterator = data.ngramMap.keySet().iterator();
        if (ngramSizesIterator.hasNext()) {
            return ngramSizesIterator.next();
        }
        throw new IllegalStateException("No data available in model");
    }
}
