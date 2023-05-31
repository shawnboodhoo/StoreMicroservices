package com.product.api.product.api.controller;

import com.product.api.product.api.dto.ProductDto;
import com.product.api.product.api.dto.ResponseDto;
import com.product.api.product.api.dto.StoreDto;
import com.product.api.product.api.model.Product;
import com.product.api.product.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")

public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductDto product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{UniqueStoreIdentifier}")
    public ResponseEntity<?> getAllProductsByUniqueStoreIdentifier(@PathVariable String UniqueStoreIdentifier) {
        return productService.getAllProductsByUniqueStoreIdentifier(UniqueStoreIdentifier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDto product) {
        return productService.updateProduct(id, product);
    }

    @GetMapping("/searchProducts")
    public ResponseEntity<?> searchForProductsInAStore(@RequestParam("name") String name, @RequestParam("unique_store_identifier") String unique_store_identifier) {
        return productService.searchForProductsInAStore(name, unique_store_identifier);
    }
    @PostMapping("/{productId}/buy")
    public ResponseEntity<?> buyProduct(@PathVariable Long productId, @RequestParam("quantity") int quantity){
        return productService.buyAProduct(productId,quantity);

    }
    @PostMapping("/{productId}/restock")
    public ResponseEntity<?> restockProduct(@PathVariable Long productId, @RequestParam("quantity") int quantity){
        productService.restockAProduct(productId, quantity);
        return productService.restockAProduct(productId, quantity);

    }

}
