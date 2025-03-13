package com.library.management.service;

import com.library.management.dto.UserDto;
import com.library.management.entity.User;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(User.Role.USER)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .role(User.Role.USER)
                .build();
    }

    @Test
    @DisplayName("Happy Path - Load User By Username")
    void whenValidUsername_thenUserShouldBeFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> {
            userService.loadUserByUsername("testuser");
        });

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Unhappy Path - Load User By Invalid Username")
    void whenInvalidUsername_thenUsernameNotFoundExceptionShouldBeThrown() {
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("invalid");
        });

        verify(userRepository).findByUsername("invalid");
    }

    @Test
    @DisplayName("Happy Path - Get User By ID")
    void whenValidId_thenUserShouldBeFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto found = userService.getUserById(1L);

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals(user.getUsername(), found.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Unhappy Path - Get User By Invalid ID")
    void whenInvalidId_thenResourceNotFoundExceptionShouldBeThrown() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(99L);
        });

        verify(userRepository).findById(99L);
    }

    @Test
    @DisplayName("Happy Path - Get User By Username")
    void whenValidUsername_thenUserDtoShouldBeReturned() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDto found = userService.getUserByUsername("testuser");

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals(user.getUsername(), found.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Unhappy Path - Get User By Invalid Username")
    void whenInvalidUsername_thenResourceNotFoundExceptionShouldBeThrown() {
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("invalid");
        });

        verify(userRepository).findByUsername("invalid");
    }
}
