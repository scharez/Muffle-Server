package utils;

import java.io.IOException;
import java.util.Properties;

/**
 * Muffle-Server
 *
 * @author: Sebastian Schiefermayr
 * Date: 02.03.2020
 * Time: 11:53
 * =====================================
 * Website: https://bastiarts.com
 * Email: basti@bastiarts.com
 * Github: https://github.com/BastiArts
 * =====================================
 */
public class PropertyUtil {
    private static PropertyUtil instance = null;
    private Properties config_props = new Properties();
    private Properties message_props = new Properties();

    public PropertyUtil() {
        init();
    }

    /**
     * Singleton for easy access
     */
    public static PropertyUtil getInstance() {
        if (instance == null) {
            instance = new PropertyUtil();
        }
        return instance;
    }

    /**
     * Reads the configFile
     */
    public void init() {
        try {
            this.config_props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/config.properties"));
            this.message_props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("properties/message.properties"));
        } catch (
                IOException e) {
            System.err.println("Properties file(s) could not be loaded!");
        }
    }

    /**
     * @return Returns the property object for our config
     */
    public Properties getConfigProps() {
        return this.config_props;
    }

    /**
     * @return Returns the property object for our messages
     */
    public Properties getMessageProps() {
        return this.message_props;
    }
}

