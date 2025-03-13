package com.library.management.controller;

import com.library.management.dto.BookDto;
import com.library.management.dto.BorrowDto;
import com.library.management.service.BookService;
import com.library.management.service.BorrowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book-API", description = "Book operations")
public class BookController {

    private final BookService bookService;
    private final BorrowService borrowService;

    @GetMapping
    public ResponseEntity<List<BookDto>> browseBooks(
            @RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok(bookService.searchBooks(query));
        }
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<BorrowDto> borrowBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.borrowBook(id));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BorrowDto> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(borrowService.returnBook(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<BorrowDto>> getBorrowHistory() {
        return ResponseEntity.ok(borrowService.getUserBorrowHistory());
    }
}
