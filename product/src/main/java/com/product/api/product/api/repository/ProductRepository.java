package com.product.api.product.api.repository;

import com.product.api.product.api.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import store.app.store.model.Stores;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    @Query("select p from Product p where p.uniqueStoreIdentifier = ?1")
    List<Product> findByUniqueStoreIdentifier(String uniqueStoreIdentifier);


    @Query("select (count(p) > 0) from Product p where upper(p.name) = upper(?1)")
    boolean existsByname(String name);


    @Query(value = "SELECT * FROM Product WHERE UPPER(name) = UPPER(:name) AND UPPER(unique_store_identifier) = UPPER(:unique_store_identifier)", nativeQuery = true)
    List<Product> findByProductNameContainingAndUniqueStoreIdentifier(String name, String unique_store_identifier);

}
