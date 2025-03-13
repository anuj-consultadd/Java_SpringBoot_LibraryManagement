package com.library.management.service;

import com.library.management.dto.BorrowDto;
import com.library.management.entity.Book;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import com.library.management.exception.BadRequestException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.UserRepository;
import org.hibernate.service.spi.InjectService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Spy
    private UserRepository userRepository;


    @InjectMocks
    private BookService bookService;

    @InjectMocks
    private AuthService authService;

    @InjectMocks
    private BorrowService borrowService;

    private User user;
    private Book book;
    private Borrow borrow;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(User.Role.USER)
                .build();


        // Create a mock Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null);

        // Set up a SecurityContext with the mock Authentication
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock UserRepository behavior
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.ofNullable(user));





        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .available(true)
                .build();

        borrow = Borrow.builder()
                .id(1L)
                .user(user)
                .book(book)
                .borrowedAt(LocalDateTime.now())
                .returnedAt(null)
                .build();

    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Happy Path - Get All Borrowed Books")
    void whenGetAllBorrowedBooks_thenReturnAllBorrowedBooks() {
        List<Borrow> borrows = Arrays.asList(borrow);
        when(borrowRepository.findByReturnedAtIsNull()).thenReturn(borrows);

        List<BorrowDto> result = borrowService.getAllBorrowedBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrow.getId(), result.get(0).getId());
        verify(borrowRepository).findByReturnedAtIsNull();
    }

    @Test
//    @WithMockUser(username = "testuser")
    @DisplayName("Happy Path - Get User Borrow History")
    void whenGetUserBorrowHistory_thenReturnUserBorrows() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(borrowRepository.findByUserOrderByBorrowedAtDesc(user)).thenReturn(List.of(borrow));

        List<BorrowDto> result = borrowService.getUserBorrowHistory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(borrow.getId(), result.get(0).getId());
        verify(authService).getCurrentUser();
        verify(borrowRepository).findByUserOrderByBorrowedAtDesc(user);
    }

    @Test
    @DisplayName("Happy Path - Borrow Book")
    void whenBorrowBook_thenBookShouldBeBorrowed() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrow);

        BorrowDto result = borrowService.borrowBook(1L);

        assertNotNull(result);
        assertEquals(borrow.getId(), result.getId());
        assertFalse(book.getAvailable());
        verify(authService).getCurrentUser();
        verify(bookService).getBookEntityById(1L);
        verify(borrowRepository).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unhappy Path - Borrow Unavailable Book")
    void whenBorrowUnavailableBook_thenBadRequestExceptionShouldBeThrown() {
        book.setAvailable(false);
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);

        assertThrows(BadRequestException.class, () -> {
            borrowService.borrowBook(1L);
        });

        verify(authService).getCurrentUser();
        verify(bookService).getBookEntityById(1L);
        verify(borrowRepository, never()).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Happy Path - Return Book")
    void whenReturnBook_thenBookShouldBeReturned() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);
        when(borrowRepository.findByBookAndUserAndReturnedAtIsNull(book, user))
                .thenReturn(Optional.of(borrow));
        when(borrowRepository.save(any(Borrow.class))).thenReturn(borrow);

        BorrowDto result = borrowService.returnBook(1L);

        assertNotNull(result);
        assertEquals(borrow.getId(), result.getId());
        assertTrue(book.getAvailable());
        assertNotNull(borrow.getReturnedAt());
        verify(authService).getCurrentUser();
        verify(bookService).getBookEntityById(1L);
        verify(borrowRepository).findByBookAndUserAndReturnedAtIsNull(book, user);
        verify(borrowRepository).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unhappy Path - Return Book Not Borrowed")
    void whenReturnBookNotBorrowed_thenResourceNotFoundExceptionShouldBeThrown() {
        when(authService.getCurrentUser()).thenReturn(user);
        when(bookService.getBookEntityById(1L)).thenReturn(book);
        when(borrowRepository.findByBookAndUserAndReturnedAtIsNull(book, user))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            borrowService.returnBook(1L);
        });

        verify(authService).getCurrentUser();
        verify(bookService).getBookEntityById(1L);
        verify(borrowRepository).findByBookAndUserAndReturnedAtIsNull(book, user);
        verify(borrowRepository, never()).save(any(Borrow.class));
    }
}
