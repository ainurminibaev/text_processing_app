import classifier.BayesClassifier;
import classifier.Classifier;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by ainurminibaev on 11.05.15.
 */
public class Classify {

    //    http://www.e-reading.club/txt.php/1008499/Lewis_-_Moneyball.txt
    public static String downloadBook(String uri) throws IOException {
        URL book = new URL(uri);
        return readBook(new InputStreamReader(book.openStream()));
    }

    public static String readBook(Reader in) throws IOException {
        BufferedReader br = new BufferedReader(in);
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static String readFromFile(String file) throws IOException {
        File f = new File(file);
        System.out.println(f.getAbsolutePath());
        return readBook(new FileReader(file));
    }

    public static void main(String[] args) throws IOException {
        // Create a new bayes classifier with string categories and string features.
        Classifier<String, String> bayes = new BayesClassifier<String, String>();

// Two examples to learn from: Moneyball - about baseball, and a.txt - Steve Jobs book
        String[] sportText = downloadBook("http://www.e-reading.club/txt.php/1008499/Lewis_-_Moneyball.txt").split("\\s");
        String[] infoText = readFromFile("a.txt").split("\\s");

        bayes.learn("sports", Arrays.asList(sportText));
        bayes.learn("info", Arrays.asList(infoText));


        String[] words = "goal tutor variance speed drink defence performance field apple computer science steve IBM processor Steve duration".split("\\s");
        for (String word : words) {
            System.out.println(word + " = " + bayes.classify(Arrays.asList(word)).getCategory());
        }

// Get more detailed classification result.
        ((BayesClassifier<String, String>) bayes).classifyDetailed(Arrays.asList(words));

// Change the memory capacity. New learned classifications (using
// the learn method) are stored in a queue with the size given
// here and used to classify unknown sentences.
        bayes.setMemoryCapacity(500);
    }
}
