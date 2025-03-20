package me.davidgarmo.soundseeker.product.persistence.dao;

import java.util.List;

public interface IDao<T> {

    T save(T t);

    T findById(Long id);

    List<T> findAll();

    T update(T t);

    void delete(Long id);
}
