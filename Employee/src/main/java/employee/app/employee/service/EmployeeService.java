package employee.app.employee.service;

import employee.app.employee.dto.EmployeeDto;
import employee.app.employee.dto.StoreDto;
import employee.app.employee.model.Employee;
import employee.app.employee.model.enums.EmployeePosition;
import employee.app.employee.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import store.app.store.exceptions.ErrorStatusCodeMessage;
import store.app.store.response.CodeMessage;
import store.app.store.response.StatusCodeMessageData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;


    public ResponseEntity<?> createEmployee(EmployeeDto employeeDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + employeeDto.getUnique_store_identifier(), StoreDto.class);

            if (employeeRepository.existsByFirst_nameAndLast_nameAndUnique_store_identifier(employeeDto.getFirst_name(), employeeDto.getLast_name(), employeeDto.getUnique_store_identifier())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Employee name already exists in this store");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            }


            Employee employee = modelMapper.map(employeeDto, Employee.class);

            Employee employee1 = employeeRepository.save(employee);

            EmployeeDto postResponse = modelMapper.map(employee, EmployeeDto.class);


            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Employee created", postResponse);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);

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


    public ResponseEntity<?> getAllEmployees() {
        List<Employee> employeeList = (List<Employee>) employeeRepository.findAll();
        if (employeeList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "No Employees available");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<EmployeeDto> employeeDtos = employeeList.stream().map(employee -> modelMapper.map(employee, EmployeeDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Got all Employees", employeeDtos);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllEmployeesByUniqueIdentifier(String unique_Store_identifier) {
        List<Employee> employeeList = employeeRepository.findByUnique_store_identifier(unique_Store_identifier);
        if (employeeList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "No employees found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<EmployeeDto> employeeDtos = employeeList.stream().map(employee -> modelMapper.map(employee, EmployeeDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Employees found", employeeDtos);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Employee ID not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        EmployeeDto postResponse = modelMapper.map(employee.get(), EmployeeDto.class);
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Employee found", postResponse);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteEmployeeById(Long employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
        if (employeeOptional.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Employee id not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        employeeRepository.deleteById(employeeId);
        CodeMessage codeMessage = new CodeMessage(HttpStatus.OK.value(), "Employee deleted");
        return new ResponseEntity<>(codeMessage, HttpStatus.OK);
    }


    public ResponseEntity<?> updateEmployee(Long id, EmployeeDto employeeDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + employeeDto.getUnique_store_identifier(), StoreDto.class);

            Optional<Employee> optionalEmployee = employeeRepository.findById(id);

            if (optionalEmployee.isEmpty()) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Employee ID not found");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
            } else if (employeeRepository.existsByFirst_nameAndLast_nameAndUnique_store_identifier(employeeDto.getFirst_name(), employeeDto.getLast_name(), employeeDto.getUnique_store_identifier())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Employee name already exists in this Store");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            } else {

                Employee existingEmployee = optionalEmployee.get();


               modelMapper.map(employeeDto, existingEmployee);


                existingEmployee.setId(id);


                Employee updatedEmployee = employeeRepository.save(existingEmployee);

                EmployeeDto updatedEmployeeDto = modelMapper.map(updatedEmployee, EmployeeDto.class);

                StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Employee updated", updatedEmployeeDto);
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

    public ResponseEntity<?> searchForEmployeeInAStore(String first_name, String last_name, String unique_Store_identifier) {
        List<Employee> employeeList = employeeRepository.findByEmployeefirst_nameAndlast_nameContainingAndUniqueStoreIdentifier(first_name, last_name, unique_Store_identifier);
        if (employeeList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Employee not found in this Store");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<EmployeeDto> employeeDtoList = employeeList.stream().map(employee -> modelMapper.map(employee, EmployeeDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Employee found in this store", employeeDtoList);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }
}