package store.app.store.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.app.store.model.Stores;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<Stores, Long> {
    @Query("select (count(s) > 0) from Stores s where upper(s.sameStoreStoreIdentifier) = upper(?1)")
    boolean existsBystoreIdentifier(String sameStoreStoreIdentifier);

    @Query("select (count(s) > 0) from Stores s where upper(s.storeName) = upper(?1)")
    boolean existsByStoreName(String storeName);

    @Query("select (count(s) > 0) from Stores s where upper(s.uniqueStoreIdentifier) = upper(?1)")
    boolean uniqueStoreIdentifierExists(String uniqueStoreIdentifier);

    List<Stores> getAllStoresBysameStoreStoreIdentifier(String sameStoreStoreIdentifier);

    Optional<Stores> getStoreByuniqueStoreIdentifier(String uniqueStoreIdentifier);

    @Query(value = "SELECT * FROM Stores WHERE UPPER(store_name) = UPPER(:store_name) AND UPPER(location) = UPPER(:location)", nativeQuery = true)
    List<Stores> findByStoreNameContainingAndLocation(String store_name, String location);


//    @Query(value = "SELECT * FROM Stores WHERE storeName LIKE CONCAT('%', :storeNameQuery, '%') AND location = :location", nativeQuery = true)
//    List<Stores> findByStoreNameContainingAndLocation(@Param("storeNameQuery") String storeNameQuery, @Param("location") String location);

    Stores findStoresByuniqueStoreIdentifier(String uniqueStoreIdentifier);


}
