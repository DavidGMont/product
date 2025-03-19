package me.davidgarmo.soundseeker.product.persistence.dao;

import me.davidgarmo.soundseeker.product.persistence.entity.Product;

import java.util.List;

public interface IProductDao {

    Product save(Product product);

    Product findById(Long id);

    List<Product> findAll();

    Product update(Product product);

    void delete(Long id);
}
