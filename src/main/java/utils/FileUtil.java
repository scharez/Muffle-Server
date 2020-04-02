package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

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


    public String readFromFile(String absolutePath, Map<String, String> flags) {
        String fileContent = readFile(absolutePath);
        try {
            Set<Map.Entry<String, String>> entries = flags.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                fileContent = fileContent.replace(entry.getKey().trim(), entry.getValue().trim());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return fileContent;
    }

    public String readFile(String absolutePath) {
        StringBuffer contents = new StringBuffer();
        absolutePath = new File(absolutePath).getAbsolutePath();
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
