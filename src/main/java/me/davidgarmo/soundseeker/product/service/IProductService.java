package me.davidgarmo.soundseeker.product.service;

import me.davidgarmo.soundseeker.product.persistence.entity.Product;

import java.util.List;

public interface IProductService {
    Product save(Product product);

    Product findById(Long id);

    List<Product> findAll();

    Product update(Product product);

    void delete(Long id);
}
