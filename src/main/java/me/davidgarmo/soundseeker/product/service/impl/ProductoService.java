package me.davidgarmo.soundseeker.product.service.impl;

import me.davidgarmo.soundseeker.product.persistence.dao.IDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;
import me.davidgarmo.soundseeker.product.service.IProductService;

import java.util.List;

public class ProductoService implements IProductService {
    private final IDao<Product> productIDao;

    public ProductoService(IDao<Product> productIDao) {
        this.productIDao = productIDao;
    }

    @Override
    public Product save(Product product) {
        return productIDao.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productIDao.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productIDao.findAll();
    }

    @Override
    public Product update(Product product) {
        return productIDao.update(product);
    }

    @Override
    public void delete(Long id) {
        productIDao.delete(id);
    }
}
