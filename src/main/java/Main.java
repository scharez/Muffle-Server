import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.PropertyUtil;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static String BASE_URI = "";
    private static org.glassfish.grizzly.http.server.HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig().packages("service", "filter");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        // Init our Properties
        Properties props = PropertyUtil.getInstance().getConfigProps();
        BASE_URI = props.getProperty("server.host") + ":"
                + props.getProperty("server.port")
                + props.getProperty("server.route");

        Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
        l.setLevel(Level.FINE);
        l.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        l.addHandler(ch);


        final org.glassfish.grizzly.http.server.HttpServer server = startServer();
        server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("public"), "/");

        System.out.println(PropertyUtil.getInstance().getMessageProps().getProperty("general.started"));
        System.in.read();
        server.shutdown();
    }
}
