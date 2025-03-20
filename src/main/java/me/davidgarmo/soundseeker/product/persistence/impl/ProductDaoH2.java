package me.davidgarmo.soundseeker.product.persistence.impl;

import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.persistence.dao.IDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoH2 implements IDao<Product> {

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
        Connection connection = null;
        Product product = null;
        String query = "SELECT * FROM PRODUCT WHERE ID = ?";

        try {
            connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                product = mapProduct(resultSet);
                LOGGER.info("✔ Product found successfully: {}", product);
                return product;
            }
        } catch (Exception e) {
            LOGGER.error("✘ Error establishing connection: {}", e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return product;
    }

    @Override
    public List<Product> findAll() {
        Connection connection = null;
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM PRODUCT";

        try {
            connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }

            LOGGER.info("✔ Products found successfully: {}", products);
            return products;
        } catch (Exception e) {
            LOGGER.error("✘ Error establishing connection: {}", e.getMessage());
        } finally {
            closeConnection(connection);
        }

        return products;
    }

    @Override
    public Product update(Product product) {
        Connection connection = null;
        String query = "UPDATE PRODUCT SET NAME = ?, DESCRIPTION = ?, BRAND = ?, PRICE = ?, AVAILABLE = ?, THUMBNAIL = ?, CATEGORY_ID = ? WHERE ID = ?";

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setString(3, product.getBrand());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setBoolean(5, product.getAvailable());
            preparedStatement.setString(6, product.getThumbnail());
            preparedStatement.setLong(7, product.getCategoryId());
            preparedStatement.setLong(8, product.getId());

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows > 0) {
                LOGGER.info("✔ Product updated successfully: {}", product);
            } else {
                LOGGER.warn("✘ Product not found: {}", product.getId());
            }

            connection.commit();
        } catch (Exception e) {
            LOGGER.error("✘ Error updating product: {}", e.getMessage());
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
    public void delete(Long id) {
        Connection connection = null;
        String query = "DELETE FROM PRODUCT WHERE ID = ?";

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);

            int deletedRows = preparedStatement.executeUpdate();
            if (deletedRows > 0) {
                LOGGER.info("✔ Product deleted successfully: {}", id);
            } else {
                LOGGER.warn("✘ Product not found: {}", id);
            }

            connection.commit();
        } catch (Exception e) {
            LOGGER.error("✘ Error deleting product: {}", e.getMessage());
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
    }

    private Product mapProduct(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getLong("ID"),
                resultSet.getString("NAME"),
                resultSet.getString("DESCRIPTION"),
                resultSet.getString("BRAND"),
                resultSet.getDouble("PRICE"),
                resultSet.getBoolean("AVAILABLE"),
                resultSet.getString("THUMBNAIL"),
                resultSet.getLong("CATEGORY_ID")
        );
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
