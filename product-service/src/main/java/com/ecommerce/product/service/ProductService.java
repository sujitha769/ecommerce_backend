package com.ecommerce.product.service;

import com.ecommerce.product.client.InventoryClient;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.RestPage;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageService productImageService;
    private final InventoryClient inventoryClient;

    public ProductService(
            ProductRepository productRepository,
            ProductImageService productImageService,
            InventoryClient inventoryClient) {

        this.productRepository = productRepository;
        this.productImageService = productImageService;
        this.inventoryClient = inventoryClient;
    }

    // Create product and invalidate paginated caches before execution.
    @Transactional
    @CacheEvict(
            cacheNames = {
                    "productsPage",
                    "productsByCategoryPage",
                    "productsSearchPage"
            },
            allEntries = true,
            beforeInvocation = true
    )
    public ProductResponse createProduct(
            ProductRequest request,
            MultipartFile file) throws IOException {

        if (request.getStockQuantity() == null || request.getStockQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "stockQuantity is required and must be zero or greater");
        }

        Product product = new Product();
        copyRequestToProduct(request, product);

        // 1) Cloudinary upload timing
        long start = System.currentTimeMillis();

        String imageUrl = productImageService.uploadImage(file);

        System.out.println("Cloudinary upload: "
                + (System.currentTimeMillis() - start) + " ms");

        product.setImageUrl(imageUrl);

        // 2) Database save timing
        start = System.currentTimeMillis();

        Product savedProduct = productRepository.saveAndFlush(product);

        System.out.println("Database save: "
                + (System.currentTimeMillis() - start) + " ms");

        // 3) Inventory API timing
        try {
            start = System.currentTimeMillis();

            inventoryClient.createInventory(
                    savedProduct.getId(),
                    request.getStockQuantity()
            );

            System.out.println("Inventory API: "
                    + (System.currentTimeMillis() - start) + " ms");
        } catch (Exception exception) {
            System.err.println("Inventory API failed after "
                    + (System.currentTimeMillis() - start) + " ms");
            System.err.println("Inventory initialization failed for product ID: "
                    + savedProduct.getId());
            System.err.println("Error: " + exception.getMessage());
            exception.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Inventory initialization failed; product transaction rolled back for product ID: "
                            + savedProduct.getId(),
                    exception
            );
        }

        return toResponse(savedProduct);
    }

    // Existing endpoint: all products
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Existing endpoint: product by ID
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return toResponse(findProduct(id));
    }

    // Existing endpoint: category filter
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Existing endpoint: product search
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword,
                        keyword
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Cached paginated product listing
    @Cacheable(
            cacheNames = "productsPage",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsPage(Pageable pageable) {
        return new RestPage<>(
                productRepository.findAll(pageable)
                        .map(this::toResponse)
        );
    }

    // Cached paginated category filter
    @Cacheable(
            cacheNames = "productsByCategoryPage",
            key = "#category.toLowerCase() + '-' + "
                    + "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategoryPage(
            String category,
            Pageable pageable) {

        return new RestPage<>(
                productRepository
                        .findByCategoryIgnoreCase(category, pageable)
                        .map(this::toResponse)
        );
    }

    // Cached paginated search
    @Cacheable(
            cacheNames = "productsSearchPage",
            key = "#keyword.toLowerCase() + '-' + "
                    + "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProductsPage(
            String keyword,
            Pageable pageable) {

        return new RestPage<>(
                productRepository
                        .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                keyword,
                                keyword,
                                pageable
                        )
                        .map(this::toResponse)
        );
    }

    // Update product and invalidate caches before execution.
    @Transactional
    @CacheEvict(
            cacheNames = {
                    "productsPage",
                    "productsByCategoryPage",
                    "productsSearchPage"
            },
            allEntries = true,
            beforeInvocation = true
    )
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = findProduct(id);
        copyRequestToProduct(request, product);

        // Inventory changes are handled by Inventory Service.
        return toResponse(productRepository.save(product));
    }

    // Update image and invalidate caches before execution.
    @Transactional
    @CacheEvict(
            cacheNames = {
                    "productsPage",
                    "productsByCategoryPage",
                    "productsSearchPage"
            },
            allEntries = true,
            beforeInvocation = true
    )
    public ProductResponse updateProductImage(
            Long id,
            String imageUrl) {

        Product product = findProduct(id);
        product.setImageUrl(imageUrl);

        return toResponse(productRepository.save(product));
    }

    // Delete product and invalidate caches before execution.
    @Transactional
    @CacheEvict(
            cacheNames = {
                    "productsPage",
                    "productsByCategoryPage",
                    "productsSearchPage"
            },
            allEntries = true,
            beforeInvocation = true
    )
    public void deleteProduct(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found with ID: " + id
                ));
    }

    private void copyRequestToProduct(
            ProductRequest request,
            Product product) {

        product.setName(request.getName().trim());
        product.setCategory(request.getCategory().trim());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setDescription(request.getDescription().trim());
        product.setEmoji(request.getEmoji());
        product.setBg(request.getBg());
        product.setSizes(request.getSizes());
        product.setRating(request.getRating());
        product.setReviews(request.getReviews());
    }



    /**
     * Safely retries inventory initialization for an existing product.
     */
    public String retryInventoryInitialization(
            Long productId,
            Integer initialQuantity) {

        // Confirm that the product exists.
        findProduct(productId);

        try {
            boolean created = inventoryClient.ensureInventoryExists(
                    productId,
                    initialQuantity
            );

            if (created) {
                return "Inventory initialized successfully for product ID: "
                        + productId;
            }

            return "Inventory already exists for product ID: " + productId;

        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not initialize inventory for product ID: "
                            + productId + ". Reason: " + exception.getMessage(),
                    exception
            );
        }
    }


    private <T> List<T> plainList(List<T> source) {
        return source == null
                ? new ArrayList<>()
                : new ArrayList<>(source);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getMrp(),
                product.getRating(),
                product.getReviews(),
                product.getEmoji(),
                plainList(product.getBg()),
                product.getDescription(),
                plainList(product.getSizes()),
                product.getImageUrl()
        );
    }
}