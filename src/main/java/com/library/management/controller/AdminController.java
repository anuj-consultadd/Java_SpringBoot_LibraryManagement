package com.library.management.controller;

import com.library.management.dto.BookDto;
import com.library.management.dto.BorrowDto;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin-API", description = "Admin operations")
@RequiredArgsConstructor
public class AdminController {

    private final BookService bookService;
    private final BorrowService borrowService;

    @PostMapping("/books")
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) {
        return new ResponseEntity<>(bookService.createBook(bookDto), HttpStatus.CREATED);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    @PatchMapping("/books/{id}")
    public ResponseEntity<BookDto> patchBook(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(bookService.patchBook(id, updates));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/borrowed-books")
    public ResponseEntity<List<BorrowDto>> getBorrowedBooks() {
        return ResponseEntity.ok(borrowService.getAllBorrowedBooks());
    }
}
