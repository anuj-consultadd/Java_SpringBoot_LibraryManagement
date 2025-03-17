package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .available(true)
                .build();

        bookDto = BookDto.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Happy Path - Get Book By ID")
    void whenValidId_thenBookShouldBeFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto found = bookService.getBookById(1L);

        assertNotNull(found);
        assertEquals(book.getId(), found.getId());
        assertEquals(book.getTitle(), found.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Unhappy Path - Get Book By Invalid ID")
    void whenInvalidId_thenResourceNotFoundExceptionShouldBeThrown() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.getBookById(99L);
        });

        verify(bookRepository).findById(99L);
    }

    @Test
    @DisplayName("Happy Path - Get All Books")
    void whenGetAllBooks_thenReturnAllBooks() {
        List<Book> books = Arrays.asList(
                book,
                Book.builder().id(2L).title("Book 2").author("Author 2").available(true).build()
        );

        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> foundBooks = bookService.getAllBooks();

        assertNotNull(foundBooks);
        assertEquals(2, foundBooks.size());
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Happy Path - Create Book")
    void whenCreateBook_thenBookShouldBeSaved() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto savedBook = bookService.createBook(bookDto);

        assertNotNull(savedBook);
        assertEquals(bookDto.getTitle(), savedBook.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Happy Path - Update Book")
    void whenUpdateBook_thenBookShouldBeUpdated() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto updatedBookDto = BookDto.builder()
                .id(1L)
                .title("Updated Title")
                .author("Updated Author")
                .available(true)
                .build();

        BookDto result = bookService.updateBook(1L, updatedBookDto);

        assertNotNull(result);
        assertEquals(updatedBookDto.getTitle(), result.getTitle());
        assertEquals(updatedBookDto.getAuthor(), result.getAuthor());
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Happy Path - Patch Book")
    void whenPatchBook_thenOnlyProvidedFieldsShouldBeUpdated() {

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);


        Map<String, Object> updates = Map.of(
                "title", "Patched Title"
        );


        BookDto result = bookService.patchBook(1L, updates);


        assertNotNull(result);
        assertEquals("Patched Title", result.getTitle());
        assertTrue(result.getAvailable());
        assertEquals(book.getAuthor(), result.getAuthor());

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("UnHappy Path - Patch Non-existent Book")
    void whenPatchNonExistentBook_thenResourceNotFoundExceptionShouldBeThrown() {

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        Map<String, Object> updates = Map.of(
                "title", "Patched Title"
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.patchBook(99L, updates);
        });

        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).save(any(Book.class));
    }


    @Test
    @DisplayName("Unhappy Path - Update Non-existent Book")
    void whenUpdateNonExistentBook_thenResourceNotFoundExceptionShouldBeThrown() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(99L, bookDto);
        });

        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Happy Path - Delete Book")
    void whenDeleteBook_thenBookShouldBeDeleted() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }

    @Test
    @DisplayName("Unhappy Path - Delete Non-existent Book")
    void whenDeleteNonExistentBook_thenResourceNotFoundExceptionShouldBeThrown() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(99L);
        });

        verify(bookRepository).findById(99L);
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
