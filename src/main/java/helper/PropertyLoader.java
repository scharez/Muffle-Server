package helper;

import java.io.*;
import java.util.Properties;

public class PropertyLoader {

    public Properties prop;

    public PropertyLoader() {

        this.prop=  new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/properties/muffle.properties")) {

            prop.load(input);

        } catch (IOException io) {
            System.err.println("File could not be loaded!");
        }

    }


}
