package me.davidgarmo.soundseeker.product.service.impl;

import me.davidgarmo.soundseeker.product.persistence.dao.IDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.service.IProductService;

import java.util.List;

public class ProductService implements IProductService {
    private final IDao<Product> productIDao;

    public ProductService(IDao<Product> productIDao) {
        this.productIDao = productIDao;
    }

    private static void validateProductId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID must be informed.");
        }
    }

    private static void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty() || product.getName().length() > 60) {
            throw new IllegalArgumentException("Product name cannot be empty or exceed 60 characters.");
        }
        if (product.getDescription() == null || product.getDescription().isEmpty() || product.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Product description cannot be empty or exceed 1000 characters.");
        }
        if (product.getBrand() == null || product.getBrand().isEmpty() || product.getBrand().length() > 60) {
            throw new IllegalArgumentException("Product brand cannot be empty or exceed 60 characters.");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0.");
        }
        if (product.getAvailable() == null) {
            throw new IllegalArgumentException("Product availability must be informed.");
        }
        if (product.getThumbnail().length() > 255) {
            throw new IllegalArgumentException("Product thumbnail cannot exceed 255 characters.");
        }
        if (product.getCategoryId() == null) {
            throw new IllegalArgumentException("Product category must be informed.");
        }
    }

    @Override
    public Product save(Product product) {
        validateProduct(product);
        return productIDao.save(product);
    }

    @Override
    public Product findById(Long id) {
        validateProductId(id);
        return productIDao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productIDao.findAll();
    }

    @Override
    public Product update(Product product) {
        validateProductId(product.getId());
        validateProduct(product);
        return productIDao.update(product);
    }

    @Override
    public void delete(Long id) {
        validateProductId(id);
        productIDao.delete(id);
    }
}
