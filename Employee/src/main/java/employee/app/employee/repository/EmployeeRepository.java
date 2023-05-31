package employee.app.employee.repository;

import employee.app.employee.model.Employee;
import employee.app.employee.model.enums.EmployeePosition;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    @Query("select (count(e) > 0) from Employee e where e.unique_store_identifier = ?1")
    boolean existsByUnique_store_identifier(String unique_store_identifier);


    @Query("select (count(e) > 0) from Employee e where e.position = ?1")
    boolean existsByPosition(EmployeePosition position);

    @Query("select e from Employee e where e.unique_store_identifier = ?1")
    List<Employee> findByUnique_store_identifier(String unique_store_identifier);

    @Query("select (count(e) > 0) from Employee e " +
            "where upper(e.first_name) = upper(?1) and upper(e.last_name) = upper(?2) and e.unique_store_identifier = ?3")
    boolean existsByFirst_nameAndLast_nameAndUnique_store_identifier(String first_name, String last_name, String unique_store_identifier);

    @Query("select (count(e) > 0) from Employee e where e.position = ?1 and e.unique_store_identifier = ?2")
    boolean existsByPositionAndUnique_store_identifier(EmployeePosition position, String unique_store_identifier);

    @Query(value = "SELECT * FROM Employee WHERE UPPER(first_name) = UPPER(:first_name) AND UPPER(last_name) = UPPER(:last_name) AND UPPER(unique_store_identifier) = UPPER(:unique_store_identifier)", nativeQuery = true)
    List<Employee> findByEmployeefirst_nameAndlast_nameContainingAndUniqueStoreIdentifier(String first_name, String last_name, String unique_store_identifier);
}
