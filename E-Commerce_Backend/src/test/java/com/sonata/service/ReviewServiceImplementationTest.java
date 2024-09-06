package com.sonata.service;

import com.sonata.exception.ProductException;
import com.sonata.modal.Product;
import com.sonata.modal.Review;
import com.sonata.modal.User;
import com.sonata.repository.ProductRepository;
import com.sonata.repository.ReviewRepository;
import com.sonata.request.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceImplementationTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewServiceImplementation reviewService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReview_Success() throws ProductException {
        // Setup
        Product product = new Product();
        product.setId(1L);

        User user = new User();
        user.setId(1L);

        ReviewRequest request = new ReviewRequest();
        request.setProductId(1L);
        request.setReview("Great product!");

        Review review = new Review();
        review.setId(1L); // Assume the ID is set after saving
        review.setUser(user);
        review.setProduct(product);
        review.setReview("Great product!");
        review.setCreatedAt(LocalDateTime.now());

        // Mock behavior
        when(productService.findProductById(1L)).thenReturn(product);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            savedReview.setId(1L); // Simulate ID assignment
            return savedReview;
        });

        // Action
        Review createdReview = reviewService.createReview(request, user);

        // Assertion
        assertNotNull(createdReview);
        assertEquals("Great product!", createdReview.getReview());
        assertEquals(user, createdReview.getUser());
        assertEquals(product, createdReview.getProduct());
        assertNotNull(createdReview.getCreatedAt());
        assertEquals(1L, createdReview.getId()); // Verify the ID is set

        // Verify interactions
        verify(productService).findProductById(1L);
        verify(productRepository).save(product); // Verify that productRepository.save was called
        verify(reviewRepository).save(createdReview); // Verify that reviewRepository.save was called
    }

    @Test
    public void testCreateReview_ProductNotFound() throws ProductException {
        // Setup
        User user = new User();
        user.setId(1L);

        ReviewRequest request = new ReviewRequest();
        request.setProductId(1L);
        request.setReview("Great product!");

        // Mock behavior
        when(productService.findProductById(1L)).thenThrow(new ProductException("Product not found"));

        // Action & Assertion
        ProductException exception = assertThrows(ProductException.class, () -> {
            reviewService.createReview(request, user);
        });

        assertEquals("Product not found", exception.getMessage());

        // Verify interactions
        verify(productService).findProductById(1L);
        verifyNoInteractions(productRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void testGetAllReview() {
        // Setup
        Product product = new Product();
        product.setId(1L);

        Review review1 = new Review();
        review1.setProduct(product);
        review1.setReview("Great product!");

        Review review2 = new Review();
        review2.setProduct(product);
        review2.setReview("Not bad!");

        List<Review> reviews = Arrays.asList(review1, review2);

        // Mock behavior
        when(reviewRepository.getAllProductsReview(1L)).thenReturn(reviews);

        // Action
        List<Review> retrievedReviews = reviewService.getAllReview(1L);

        // Assertion
        assertNotNull(retrievedReviews);
        assertEquals(2, retrievedReviews.size());
        assertTrue(retrievedReviews.contains(review1));
        assertTrue(retrievedReviews.contains(review2));

        // Verify interaction
        verify(reviewRepository).getAllProductsReview(1L);
    }
}
