package com.library.management.service;

import com.library.management.dto.BookDto;
import com.library.management.entity.Book;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> getAvailableBooks() {
        return bookRepository.findByAvailable(true).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        return mapToDto(book);
    }

    public BookDto createBook(BookDto bookDto) {
        Book book = Book.builder()
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .available(bookDto.getAvailable())
                .build();

        Book savedBook = bookRepository.save(book);

        return mapToDto(savedBook);
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setAvailable(bookDto.getAvailable());

        Book updatedBook = bookRepository.save(book);

        return mapToDto(updatedBook);
    }

    public BookDto patchBook(Long id, Map<String, Object> updates) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        if (updates.containsKey("title")) {
            book.setTitle((String) updates.get("title"));
        }

        if (updates.containsKey("author")) {
            book.setAuthor((String) updates.get("author"));
        }

        if (updates.containsKey("available")) {
            book.setAvailable((Boolean) updates.get("available"));
        }

        Book updatedBook = bookRepository.save(book);

        return mapToDto(updatedBook);
    }


    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        bookRepository.delete(book);
    }

    public List<BookDto> searchBooks(String query) {
        return bookRepository.findByTitleContainingOrAuthorContaining(query, query).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BookDto mapToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .available(book.getAvailable())
                .build();
    }

    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }
}
