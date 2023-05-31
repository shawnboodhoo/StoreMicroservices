package customer.app.customer.controller;
import customer.app.customer.dto.CustomerDto;
import customer.app.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")

public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.createCustomer(customerDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/customer/{UniqueStoreIdentifier}")
    public ResponseEntity<?> getAllCustomersByUniqueStoreIdentifier(@PathVariable String UniqueStoreIdentifier) {
        return customerService.getAllCustomersByUniqueIdentifier(UniqueStoreIdentifier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomerById(@PathVariable Long id) {
        return customerService.deleteCustomerById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto product) {
        return customerService.updateCustomer(id, product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id){
        return customerService.getCustomerById(id);
    }



}
