package pack2.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pack2.Util;

import java.io.*;
import java.net.URL;
import java.text.BreakIterator;
import java.util.Locale;

/**
 * Created by adel on 28.05.15.
 */
@Repository
public class TextParserImpl implements TextParser {

    Logger logger = LoggerFactory.getLogger(TextParserImpl.class);

    @Override
    public String loadFolder(String folder) throws FileNotFoundException {
        logger.info("Reading text from folder=" + folder);
        File folderFile = new File(folder);
        StringBuilder text = new StringBuilder();
        File[] files = folderFile.listFiles();
        if (files == null) {
            throw new RuntimeException("No files in folder!");
        }
        for (File file : files) {
            logger.info("Reading text from file=" + file.getName());
            text.append(load(file));
        }
        logger.info("Text has been read");
        return text.toString();
    }

    private String loadStream(Reader in) throws FileNotFoundException {
        try (BufferedReader reader = new BufferedReader(in)) {
            String line = null;
            StringBuilder textBuilder = new StringBuilder();
            BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                textBuilder.append(Util.getMarkedLine(line, sentenceIterator));
                textBuilder.append('\n');
            }
            return textBuilder.toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return "";
    }

    @Override
    public String load(String file) throws FileNotFoundException {
        return loadStream(new FileReader(file));
    }

    @Override
    public String load(File file) throws FileNotFoundException {
        return loadStream(new FileReader(file));
    }

    @Override
    public String load(URL url) throws IOException {
        return loadStream(new InputStreamReader(url.openStream()));
    }


}
