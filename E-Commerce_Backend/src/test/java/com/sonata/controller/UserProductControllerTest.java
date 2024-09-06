package com.sonata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonata.exception.ProductException;
import com.sonata.modal.Product;
import com.sonata.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@WebMvcTest(UserProductController.class)
public class UserProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFindProductByCategoryHandler() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 10), 1);

        when(productService.getAllProduct(anyString(), anyList(), anyList(), anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(productPage);

        mockMvc.perform(get("/api/products")
                .param("category", "Electronics")
                .param("color", "Black")
                .param("size", "L")
                .param("minPrice", "100")
                .param("maxPrice", "500")
                .param("minDiscount", "10")
                .param("sort", "price")
                .param("stock", "inStock")
                .param("pageNumber", "0")
                .param("pageSize", "10"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.content[0].title").value("Test Product"));

        verify(productService, times(1)).getAllProduct(anyString(), anyList(), anyList(), anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt(), anyInt());
    }

    @Test
    public void testFindProductByIdHandler() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        when(productService.findProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/id/1"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title").value("Test Product"));

        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    public void testSearchProductHandler() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");

        List<Product> products = Collections.singletonList(product);

        when(productService.searchProduct("Test")).thenReturn(products);

        mockMvc.perform(get("/api/products/search")
                .param("q", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Product"));

        verify(productService, times(1)).searchProduct("Test");
    }
}
