import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeedFileReader {

    public List<String> getSeedList(String fileName) {
        String line = null;
        List<String> seedList = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                seedList.add(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("The file:" + fileName + " does not exist.");
        } catch (IOException ex) {
            System.out.println("Error reading file: " + fileName);
        }
        return seedList;
    }
}
