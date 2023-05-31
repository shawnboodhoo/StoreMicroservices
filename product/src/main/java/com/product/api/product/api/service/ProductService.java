package com.product.api.product.api.service;


import com.product.api.product.api.dto.ProductDto;
import com.product.api.product.api.dto.ResponseDto;
import com.product.api.product.api.dto.StoreDto;
import com.product.api.product.api.exceptions.ErrorStatusCodeMessage;
import com.product.api.product.api.model.Product;
import com.product.api.product.api.repository.ProductRepository;
import com.product.api.product.api.response.CodeMessage;
import com.product.api.product.api.response.StatusCodeMessageData;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import store.app.store.model.Stores;

import javax.transaction.Transactional;
import java.net.ConnectException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<?> createProduct(ProductDto productDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + productDto.getUniqueStoreIdentifier(), StoreDto.class);

            if (productRepository.existsByname(productDto.getName())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Product name already exists");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            } else if (productDto.getQuantity() <= 0) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Product quantity has to be greater than zero");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
            } else if (productDto.getPrice() <= 0) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Product price has to be greater than zero");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
            }

            Product product = modelMapper.map(productDto, Product.class);
            Product product1 = productRepository.save(product);

            ProductDto postResponse = modelMapper.map(product1, ProductDto.class);
            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product created", postResponse);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);

        } catch (HttpClientErrorException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store UniqueStoreIdentifier not found" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } catch (RestClientException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connection error to Store local host api" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataIntegrityViolationException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



        public ResponseEntity<?> getAllProducts(){
        List<Product> productList = (List<Product>) productRepository.findAll();
        if (productList.isEmpty()){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Error getting product");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<ProductDto> productDtos = productList.stream().map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product created", productDtos);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);

    }

    public ResponseEntity<?> getAllProductsByUniqueStoreIdentifier(String UniqueStoreIdentifier){
    try {

        List<Product> productList = productRepository.findByUniqueStoreIdentifier(UniqueStoreIdentifier);

        ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + UniqueStoreIdentifier, StoreDto.class );


        StoreDto storeResponse = responseEntity.getBody();


        List<ProductDto> productResponse = productList.stream().map(x -> modelMapper.map(x, ProductDto.class))
                .collect(Collectors.toList());

        ResponseDto responseDto = new ResponseDto();

        responseDto.setStore(storeResponse);
        responseDto.setProduct(productResponse);

        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product found", responseDto);

        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    } catch (HttpClientErrorException e) {
        store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store UniqueStoreIdentifier not found" + " " + e.getMessage());
        return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
    } catch (RestClientException e) {
        store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connection error to Store local host api" + " " + e.getMessage());
        return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (DataIntegrityViolationException e) {
        store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
        return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    }



    public ResponseEntity<?> deleteProductById(Long id) {
        Optional<Product> productDtoOptional = productRepository.findById(id);
        if (productDtoOptional.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Product id not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        CodeMessage codeMessage = new CodeMessage(HttpStatus.OK.value(), "product deleted");
        return new ResponseEntity<>(codeMessage, HttpStatus.OK);

    }


    public ResponseEntity<?> updateProduct(Long id, ProductDto productDto) {
        try {
            ResponseEntity<StoreDto> responseEntity = restTemplate.getForEntity("http://localhost:8080/store/uniqueStoreIdentifier/" + productDto.getUniqueStoreIdentifier(), StoreDto.class);
            Optional<Product> product = productRepository.findById(id);
            if (product.isEmpty()) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Product id not found");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
            } else if (productRepository.existsByname(productDto.getName())) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.CONFLICT.value(), "Product already name exists");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.CONFLICT);
            } else if (productDto.getQuantity() <= 0) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Product quantity has to be greater than zero");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
            } else if (productDto.getPrice() <= 0) {
                ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Product price has to be greater than zero");
                return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
            }
            Product product1 = product.get();

            modelMapper.map(productDto, product1);

            product1.setId(id);

            productRepository.save(product1);

            StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product updated", product);
            return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Store UniqueStoreIdentifier not found" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } catch (RestClientException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Connection error to Store local host api" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataIntegrityViolationException e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Can't post null values:" + " " + e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            store.app.store.exceptions.ErrorStatusCodeMessage errorStatusCodeMessage = new store.app.store.exceptions.ErrorStatusCodeMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> searchForProductsInAStore(String name, String unique_store_identifier) {
        List<Product> productList = productRepository.findByProductNameContainingAndUniqueStoreIdentifier(name, unique_store_identifier);
        if (productList.isEmpty()) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Product not found in this store");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        }
        List<ProductDto> productDtoList = productList.stream().map(product -> modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product found in this store", productDtoList);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> buyAProduct(Long productId, int quantity){
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Product id not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } else if (product.getQuantity() < quantity| product.getQuantity() <=0){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Have to buy more than zero products or purchase quantity is more than what's available");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        }

            int remainingQuantity = product.getQuantity() -quantity;

        product.setQuantity(remainingQuantity);

        productRepository.save(product);

        ProductDto productDTO = modelMapper.map(product, ProductDto.class);

        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product Brought", productDTO);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);
    }


    public ResponseEntity<?> restockAProduct(Long productId, int quantity){
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.NOT_FOUND.value(), "Product id not found");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.NOT_FOUND);
        } else if (product.getQuantity() <=0){
            ErrorStatusCodeMessage errorStatusCodeMessage = new ErrorStatusCodeMessage(HttpStatus.BAD_REQUEST.value(), "Have to restock more than zero products");
            return new ResponseEntity<>(errorStatusCodeMessage, HttpStatus.BAD_REQUEST);
        }

        int newQuantity = product.getQuantity() + quantity;

        product.setQuantity(newQuantity);

        productRepository.save(product);

        ProductDto productDTO = modelMapper.map(product, ProductDto.class);

        StatusCodeMessageData statusCodeMessageData = new StatusCodeMessageData(HttpStatus.OK.value(), "Product Restocked", productDTO);
        return new ResponseEntity<>(statusCodeMessageData, HttpStatus.OK);

    }
}







