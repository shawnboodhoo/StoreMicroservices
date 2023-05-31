package customer.app.customer.service;


import com.product.api.product.api.dto.StoreDto;
import com.product.api.product.api.exceptions.ErrorStatusCodeMessage;

import com.product.api.product.api.response.CodeMessage;
import com.product.api.product.api.response.StatusCodeMessageData;
import customer.app.customer.dto.CustomerDto;
import customer.app.customer.model.Customer;
import customer.app.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> createCustomer(CustomerDto customerDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + customerDto.getUnique_store_identifier(), StoreDto.class);

            if (customerRepository.existsByFirst_nameAndLast_nameAndUnique_store_identifier(customerDto.getFirst_name(), customerDto.getLast_name(), customerDto.getUnique_store_identifier())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Customer name already exists in this store");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            }


            Customer customer1 = modelMapper.map(customerDto, Customer.class);

            Customer customer = customerRepository.save(customer1);

            CustomerDto postResponse = modelMapper.map(customer1, CustomerDto.class);


            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Customer created", postResponse);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store UniqueStoreIdentifier not found" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } catch (RestClientException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connection error to customer local host api" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataIntegrityViolationException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customerList = (List<Customer>) customerRepository.findAll();
        if (customerList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "No Customer available");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<CustomerDto> customerDtos = customerList.stream().map(customer -> modelMapper.map(customer, CustomerDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Got all Customers", customerDtos);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllCustomersByUniqueIdentifier(String unique_Store_identifier) {
        List<Customer> customerList = customerRepository.findByUnique_store_identifier(unique_Store_identifier);
        if (customerList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "No Customers found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<CustomerDto> customerDtos = customerList.stream().map(customer -> modelMapper.map(customer, CustomerDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Customers found", customerDtos);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> getCustomerById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Customer ID not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        CustomerDto postResponse = modelMapper.map(customer.get(), CustomerDto.class);
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Customer found", postResponse);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteCustomerById(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "customer id not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        customerRepository.deleteById(customerId);
        CodeMessage codeMessage = new CodeMessage(HttpStatus.OK.value(), "customer deleted");
        return new ResponseEntity<>(codeMessage, HttpStatus.OK);
    }


    public ResponseEntity<?> updateCustomer(Long id, CustomerDto customerDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + customerDto.getUnique_store_identifier(), StoreDto.class);

            Optional<Customer> optionalCustomer = customerRepository.findById(id);

            if (optionalCustomer.isEmpty()) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Customer ID not found");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
            } else if (customerRepository.existsByFirst_nameAndLast_nameAndUnique_store_identifier(customerDto.getFirst_name(), customerDto.getLast_name(), customerDto.getUnique_store_identifier())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "customer name already exists in this Store");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            } else {

                Customer existingcustomer = optionalCustomer.get();


                modelMapper.map(customerDto, existingcustomer);


                existingcustomer.setId(id);


                Customer updatedCustomer = customerRepository.save(existingcustomer);

                CustomerDto updatedCustomerDto = modelMapper.map(updatedCustomer, CustomerDto.class);

                StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "customer updated", updatedCustomerDto);
                return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
            }
        } catch (HttpClientErrorException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store UniqueStoreIdentifier not found" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } catch (RestClientException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connection error to Store local host api" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataIntegrityViolationException e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    }









