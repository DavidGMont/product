package me.davidgarmo.soundseeker.product.persistence.impl;

import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.persistence.dao.IProductDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class ProductDaoH2 implements IProductDao {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Product save(Product product) {
        Connection connection = null;
        String query = "INSERT INTO PRODUCT (NAME, DESCRIPTION, BRAND, PRICE, AVAILABLE, THUMBNAIL, CATEGORY_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setString(3, product.getBrand());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setBoolean(5, product.getAvailable());
            preparedStatement.setString(6, product.getThumbnail());
            preparedStatement.setLong(7, product.getCategoryId());

            preparedStatement.executeUpdate();

            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                product.setId(keys.getLong(1));
            }

            connection.commit();
            LOGGER.info("✔ Product saved successfully: {}", product);
        } catch (Exception e) {
            LOGGER.info("✘ Error saving product: {}", e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    LOGGER.error("✘ Error rolling back transaction: {}", ex.getMessage());
                }
            }
        } finally {
            closeConnection(connection);
        }

        return product;
    }

    @Override
    public Product findById(Long id) {
        return null;
    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public Product update(Product product) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("✘ Error closing connection: {}", e.getMessage());
            }
        }
    }
}
