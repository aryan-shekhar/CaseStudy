package com.sonata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonata.exception.ProductException;
import com.sonata.modal.Product;
import com.sonata.request.CreateProductRequest;
import com.sonata.response.ApiResponse;
import com.sonata.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(AdminProductController.class)
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateProductHandler() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        CreateProductRequest createProductRequest = new CreateProductRequest();
        // Set fields for createProductRequest if needed

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title").value("Test Product"));

        verify(productService, times(1)).createProduct(any(CreateProductRequest.class));
    }


    @Test
    public void testFindAllProduct() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        List<Product> products = Collections.singletonList(product);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Product"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testUpdateProductHandler() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Updated Product");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setTitle("Updated Product");

        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/products/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Product"));

        verify(productService, times(1)).updateProduct(anyLong(), any(Product.class));
    }

    
}
