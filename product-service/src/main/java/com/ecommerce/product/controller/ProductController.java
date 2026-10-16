package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public ProductController(ProductService productService, ObjectMapper objectMapper, Validator validator) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") MultipartFile productPart,
            @RequestPart("file") MultipartFile file) throws Exception {
        ProductRequest request = objectMapper.readValue(productPart.getBytes(), ProductRequest.class);
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Invalid product data", violations);
        }
        ProductResponse response = productService.createProduct(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank())
            return ResponseEntity.ok(productService.searchProducts(search));
        if (category != null && !category.isBlank())
            return ResponseEntity.ok(productService.getProductsByCategory(category));
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProductResponse>> getProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        if (page < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number cannot be negative");
        if (size < 1 || size > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 100");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<ProductResponse> products;
        if (search != null && !search.isBlank())
            products = productService.searchProductsPage(search.trim(), pageable);
        else if (category != null && !category.isBlank())
            products = productService.getProductsByCategoryPage(category.trim(), pageable);
        else
            products = productService.getProductsPage(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/inventory/retry")
    public ResponseEntity<String> retryInventoryInitialization(
            @PathVariable Long id, @RequestParam @PositiveOrZero Integer initialQuantity) {
        return ResponseEntity.ok(productService.retryInventoryInitialization(id, initialQuantity));
    }
}
