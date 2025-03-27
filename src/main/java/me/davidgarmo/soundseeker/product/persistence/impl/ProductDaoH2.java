package me.davidgarmo.soundseeker.product.persistence.impl;

import me.davidgarmo.soundseeker.product.config.DBConnection;
import me.davidgarmo.soundseeker.product.persistence.dao.IDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.service.expection.ProductNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoH2 implements IDao<Product> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SQL_INSERT = "INSERT INTO PRODUCT (NAME, DESCRIPTION, BRAND, PRICE, AVAILABLE, THUMBNAIL, CATEGORY_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM PRODUCT WHERE ID = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM PRODUCT";
    private static final String SQL_UPDATE = "UPDATE PRODUCT SET NAME = ?, DESCRIPTION = ?, BRAND = ?, PRICE = ?, AVAILABLE = ?, THUMBNAIL = ?, CATEGORY_ID = ? WHERE ID = ?";

    @Override
    public Product save(Product product) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            setData(product, preparedStatement);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("✘ Creating product failed, no rows affected.");
            }

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setId(generatedKeys.getLong(1));
                connection.commit();
                LOGGER.debug("✔ Product saved successfully: \n{}", product);
                return product;
            } else {
                throw new SQLException("✘ Creating product failed, no ID obtained.");
            }
        } catch (Exception e) {
            LOGGER.error("✘ Error saving product: {}", e.getMessage());
            rollbackTransaction(connection);
            return null;
        } finally {
            closeResources(generatedKeys, preparedStatement, connection);
        }
    }

    @Override
    public Product findById(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_BY_ID);
            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                LOGGER.debug("✔ Product found successfully: \n{}", product);
                return product;
            }
        } catch (Exception e) {
            LOGGER.error("✘ Error finding product by ID: {}", e.getMessage());
        } finally {
            closeResources(resultSet, preparedStatement, connection);
        }

        throw new ProductNotFoundException("✘ Product not found with ID: " + id);
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL);

            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            LOGGER.debug("✔ Found {} products successfully", products.size());
        } catch (Exception e) {
            LOGGER.error("✘ Error finding all products: {}", e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error("✘ Error closing Statement: {}", e.getMessage());
                }
            }
            closeResources(resultSet, null, connection);
        }

        return products;
    }

    @Override
    public Product update(Product product) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(SQL_UPDATE);
            setData(product, preparedStatement);
            preparedStatement.setLong(8, product.getId());

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows > 0) {
                connection.commit();
                LOGGER.debug("✔ Product updated successfully: \n{}", product);
                return product;
            } else {
                LOGGER.warn("✘ No product found to update with ID: {}", product.getId());
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("✘ Error updating product: {}", e.getMessage());
            rollbackTransaction(connection);
            return null;
        } finally {
            closeResources(null, preparedStatement, connection);
        }
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
                LOGGER.debug("✔ Product deleted successfully: {}", id);
            } else {
                LOGGER.warn("✘ Product not found: {}", id);
            }

            connection.commit();
        } catch (Exception e) {
            LOGGER.error("✘ Error deleting product: {}", e.getMessage());
            rollbackTransaction(connection);
        } finally {
            closeConnection(connection);
        }
    }

    private void setData(Product product, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getDescription());
        preparedStatement.setString(3, product.getBrand());
        preparedStatement.setDouble(4, product.getPrice());
        preparedStatement.setBoolean(5, product.getAvailable());
        preparedStatement.setString(6, product.getThumbnail());
        preparedStatement.setLong(7, product.getCategoryId());
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
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

    private static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                LOGGER.debug("✔ Transaction rolled back successfully.");
            } catch (SQLException ex) {
                LOGGER.error("✘ Error rolling back transaction: {}", ex.getMessage());
            }
        }
    }

    private void closeResources(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.error("✘ Error closing ResultSet: {}", e.getMessage());
            }
        }

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                LOGGER.error("✘ Error closing PreparedStatement: {}", e.getMessage());
            }
        }

        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                connection.close();
            } catch (SQLException e) {
                LOGGER.error("✘ Error closing Connection: {}", e.getMessage());
            }
        }
    }
}
