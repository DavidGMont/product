import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.web.servlet.ProductServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Application {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            initializeServer();
            Runtime.getRuntime().addShutdownHook(new Thread(DBConnection::closePool));
        } catch (Exception e) {
            LOGGER.error("✘ Error initializing database: {}", e.getMessage());
        }
    }

    private static void initializeServer() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        try {
            String baseDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
            tomcat.setBaseDir(baseDir);

            File docBase = new File("src/main/webapp/");
            if (!docBase.exists()) {
                docBase = new File(".");
            }
            Context context = tomcat.addContext("", docBase.getAbsolutePath());

            Tomcat.addServlet(context, "productServlet", new ProductServlet());
            context.addServletMappingDecoded("/api/v1/products/*", "productServlet");

            tomcat.start();
            LOGGER.info("🚀 Tomcat server started on port 8080.");

            tomcat.getServer().await();
        } catch (Exception e) {
            LOGGER.error("✘ Error starting Tomcat server: {}", e.getMessage());
        }
    }
}
