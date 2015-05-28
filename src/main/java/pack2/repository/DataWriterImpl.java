package pack2.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pack2.Constants;
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

    Logger logger = LoggerFactory.getLogger(DataWriterImpl.class);

    @Override
    public boolean writeData(Data data, String pathToFolder) {
        logger.info("Writing Data to folder=" + pathToFolder);
        if (new File(pathToFolder).mkdirs()) {
            String name = pathToFolder + File.separator + Constants.DATA_FILE_NAME + data.ngramSize + Constants.DATA_FILE_EXT;
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(name))) {
                oos.writeObject(data);
                logger.info("Data successfully saved to " + name);
                return true;
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Data couldn't be saved due to exception: ", e);
                } else {
                    logger.error("Data couldn't be saved due to exception: " + e.getMessage());
                }
                return false;
            }
        }
        logger.error("Data couldn't be saved because the application cannot create outputDir");
        return false;
    }
}