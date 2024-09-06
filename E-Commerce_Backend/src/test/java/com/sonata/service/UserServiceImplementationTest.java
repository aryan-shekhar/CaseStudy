package com.sonata.service;

import com.sonata.config.JwtTokenProvider;
import com.sonata.exception.UserException;
import com.sonata.modal.User;
import com.sonata.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImplementation userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindUserById_UserExists() throws UserException {
        // Setup
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Action
        User foundUser = userService.findUserById(1L);

        // Assertion
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("test@example.com", foundUser.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    public void testFindUserById_UserNotFound() {
        // Setup
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Action & Assertion
        UserException exception = assertThrows(UserException.class, () -> {
            userService.findUserById(1L);
        });

        assertEquals("user not found with id 1", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    public void testFindUserProfileByJwt_UserExists() throws UserException {
        // Setup
        String jwt = "valid.jwt.token";
        String email = "test@example.com";

        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        when(jwtTokenProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Action
        User foundUser = userService.findUserProfileByJwt(jwt);

        // Assertion
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals(email, foundUser.getEmail());

        verify(jwtTokenProvider).getEmailFromJwtToken(jwt);
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void testFindUserProfileByJwt_UserNotFound() {
        // Setup
        String jwt = "valid.jwt.token";
        String email = "test@example.com";

        when(jwtTokenProvider.getEmailFromJwtToken(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Action & Assertion
        UserException exception = assertThrows(UserException.class, () -> {
            userService.findUserProfileByJwt(jwt);
        });

        assertEquals("user not exist with email test@example.com", exception.getMessage());

        verify(jwtTokenProvider).getEmailFromJwtToken(jwt);
        verify(userRepository).findByEmail(email);
    }
}
