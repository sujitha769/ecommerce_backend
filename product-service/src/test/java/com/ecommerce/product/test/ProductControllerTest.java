package com.ecommerce.product.test;

import com.ecommerce.product.controller.ProductController;
import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    private ProductRequest createRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming laptop");
        request.setPrice(new BigDecimal("55000.00"));
        request.setMrp(new BigDecimal("65000.00"));
        request.setCategory("Electronics");
        request.setEmoji("💻");
        request.setBg(List.of("#dbeafe", "#93c5fd"));
        request.setSizes(List.of("Standard"));
        return request;
    }

    private ProductResponse createResponse() {
        return new ProductResponse(
                1L,
                "Laptop",
                "Electronics",
                new BigDecimal("55000.00"),
                new BigDecimal("65000.00"),
                0.0,
                0,
                "💻",
                List.of("#dbeafe", "#93c5fd"),
                "Gaming laptop",
                List.of("Standard"),
                "https://res.cloudinary.com/demo/image/upload/laptop.jpg"
        );
    }

    @Test
    void createProductWithImageTest() throws Exception {
        when(productService.createProduct(
                any(ProductRequest.class),
                any(org.springframework.web.multipart.MultipartFile.class)))
                .thenReturn(createResponse());

        MockMultipartFile productPart = new MockMultipartFile(
                "product",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest())
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "file",
                "laptop.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(55000.00))
                .andExpect(jsonPath("$.mrp").value(65000.00))
                .andExpect(jsonPath("$.imageUrl")
                        .value("https://res.cloudinary.com/demo/image/upload/laptop.jpg"));
    }

    @Test
    void getAllProductsTest() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(createResponse()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].price").value(55000.00))
                .andExpect(jsonPath("$[0].mrp").value(65000.00))
                .andExpect(jsonPath("$[0].imageUrl").exists());
    }

    @Test
    void getProductByIdTest() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(createResponse());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.sizes[0]").value("Standard"));
    }

    @Test
    void updateProductTest() throws Exception {
        when(productService.updateProduct(
                eq(1L),
                any(ProductRequest.class)))
                .thenReturn(createResponse());

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.mrp").value(65000.00));
    }

    @Test
    void deleteProductTest() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void createProductWithInvalidNameTest() throws Exception {
        ProductRequest request = createRequest();
        request.setName("");

        MockMultipartFile productPart = new MockMultipartFile(
                "product",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile imagePart = new MockMultipartFile(
                "file",
                "laptop.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/api/products")
                        .file(productPart)
                        .file(imagePart))
                .andExpect(status().isBadRequest());
    }
}