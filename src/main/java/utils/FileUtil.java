package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Muffle-Server
 *
 * @author: Sebastian Schiefermayr
 * Date: 09.03.2020
 * Time: 10:04
 * =====================================
 * Website: https://bastiarts.com
 * Email: basti@bastiarts.com
 * Github: https://github.com/BastiArts
 * =====================================
 */
public class FileUtil {
    private static FileUtil instance = null;

    private FileUtil() {

    }

    public static FileUtil getInstance() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }


    public String readFromFile(String absolutePath) {
        StringBuffer contents = new StringBuffer();

        try {
            //use buffering, reading one line at a time
            BufferedReader reader = new BufferedReader(new FileReader(absolutePath));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            System.err.println("Error reading the file with Path: " + absolutePath);
        }
        return contents.toString();
    }
}
