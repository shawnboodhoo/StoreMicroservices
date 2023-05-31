package employee.app.employee.controller;

import employee.app.employee.dto.EmployeeDto;
import employee.app.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDto employeeDto){
        return employeeService.createEmployee(employeeDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/uniqueStoreIdentifier/{uniqueStoreIdentifier}")
    public ResponseEntity<?> getAllEmployeeByUniqueStoreIdentifier(@PathVariable String uniqueStoreIdentifier){
        return employeeService.getAllEmployeesByUniqueIdentifier(uniqueStoreIdentifier);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id){
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStoreById(@PathVariable Long id){
       return employeeService.deleteEmployeeById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto){
        return employeeService.updateEmployee(id, employeeDto);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchForStoresInALocation(@RequestParam("first_name") String first_name, @RequestParam("last_name") String last_name, @RequestParam("unique_store_identifier") String unique_store_identifier) {
        return employeeService.searchForEmployeeInAStore(first_name, last_name, unique_store_identifier);
    }




    }