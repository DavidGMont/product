package me.davidgarmo.soundseeker.product.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "jdbc:h2:~/soundseeker-product";
    private static final String USER = "sa";
    private static final String PASSWORD = "sa";

    private static HikariDataSource dataSource;

    static {
        try {
            initializePool();
        } catch (Exception e) {
            LOGGER.error("✘ Error initializing connection pool: {}", e.getMessage());
        }
    }

    private static void initializePool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        dataSource = new HikariDataSource(config);
        LOGGER.info("✔ Connection pool initialized successfully.");
        initializeDatabaseIfNeeded();
    }

    private static void initializeDatabaseIfNeeded() {
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.createStatement().execute("SELECT 1 FROM PRODUCT LIMIT 1");
                LOGGER.info("✔ Database tables already exist, skipping initialization.");
            } catch (SQLException e) {
                LOGGER.info("✔ Initializing database tables...");
                connection.createStatement().execute("RUNSCRIPT FROM 'create.sql'");
                LOGGER.info("✔ Database tables initialized successfully.");
            }
        } catch (SQLException e) {
            LOGGER.error("✘ Error checking or initializing database: {}", e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("✘ DataSource is not initialized.");
        }
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("✔ Connection pool closed successfully.");
        }
    }
}
