package pack;

/**
 * Created by ainurminibaev on 12.05.15.
 */
public class SplitTest {
    public static void main(String[] args) {
        String s = "a. b";
        String[] r = s.split("[\\s\\.]+");
        for (int i = 0; i < r.length; i++) {
            String s1 = r[i];
            System.out.println(s1);
        }
    }
}
