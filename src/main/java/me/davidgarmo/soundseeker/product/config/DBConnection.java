package me.davidgarmo.soundseeker.product.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String URL = "jdbc:h2:~/soundseeker-product;INIT=RUNSCRIPT FROM 'create.sql'";
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
