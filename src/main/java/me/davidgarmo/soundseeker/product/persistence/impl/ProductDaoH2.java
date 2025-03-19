package me.davidgarmo.soundseeker.product.persistence.impl;

import me.davidgarmo.soundseeker.product.persistence.dao.IProductDao;
import me.davidgarmo.soundseeker.product.persistence.entity.Product;

import java.util.List;

public class ProductDaoH2 implements IProductDao {
    @Override
    public Product save(Product product) {
        return null;
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
}
