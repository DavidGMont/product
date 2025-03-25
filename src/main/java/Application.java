import me.davidgarmo.soundseeker.product.config.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

public class Application {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection(";INIT=RUNSCRIPT FROM 'create.sql'");
            LOGGER.info("✔ Connection established successfully.");
        } catch (Exception e) {
            LOGGER.error("✘ Error establishing connection: {}", e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    LOGGER.info("✔ Connection closed successfully.");
                }
            } catch (Exception e) {
                LOGGER.error("✘ Error closing connection: {}", e.getMessage());
            }
        }
    }
}
