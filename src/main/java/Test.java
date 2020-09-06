import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by patc on 11/14/16.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        try (BufferedReader rd = new BufferedReader(new FileReader("in"))) {
            Scanner scanner = new Scanner(rd).useDelimiter("\\Z");
            String s = scanner.hasNext() ? scanner.next() : "";
            System.out.print(s);
        }
    }
}
