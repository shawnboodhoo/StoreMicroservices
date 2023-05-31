package customer.app.customer.repository;

import customer.app.customer.model.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @Query("select c from Customer c where c.unique_store_identifier = ?1")
    List<Customer> findByUnique_store_identifier(String unique_store_identifier);

    @Query("select (count(c) > 0) from Customer c " +
            "where upper(c.first_name) = upper(?1) and upper(c.last_name) = upper(?2) and c.unique_store_identifier = ?3")
    boolean existsByFirst_nameAndLast_nameAndUnique_store_identifier(String first_name, String last_name, String unique_store_identifier);




}
