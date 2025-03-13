package com.library.management.service;

import com.library.management.dto.BorrowDto;
import com.library.management.entity.Book;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import com.library.management.exception.BadRequestException;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final UserRepository userRepository;
    private final BorrowRepository borrowRepository;
    private final BookService bookService;
    private final AuthService authService;

    public List<BorrowDto> getAllBorrowedBooks() {
        return borrowRepository.findByReturnedAtIsNull().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BorrowDto> getUserBorrowHistory() {
        User currentUser = authService.getCurrentUser();
        return borrowRepository.findByUserOrderByBorrowedAtDesc(currentUser).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BorrowDto borrowBook(Long bookId) {
        User currentUser = authService.getCurrentUser();
        Book book = bookService.getBookEntityById(bookId);

        if (!book.getAvailable()) {
            throw new BadRequestException("Book is not available for borrowing");
        }

        // Mark book as unavailable
        book.setAvailable(false);

        // Create borrow record
        Borrow borrow = Borrow.builder()
                .user(currentUser)
                .book(book)
                .borrowedAt(LocalDateTime.now())
                .build();

        Borrow savedBorrow = borrowRepository.save(borrow);

        return mapToDto(savedBorrow);
    }

    @Transactional
    public BorrowDto returnBook(Long bookId) {
        User currentUser = authService.getCurrentUser();
        Book book = bookService.getBookEntityById(bookId);

        Borrow borrow = borrowRepository.findByBookAndUserAndReturnedAtIsNull(book, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found for this book and user"));

        // Mark book as available
        book.setAvailable(true);

        // Update return date
        borrow.setReturnedAt(LocalDateTime.now());

        Borrow updatedBorrow = borrowRepository.save(borrow);

        return mapToDto(updatedBorrow);
    }

    private BorrowDto mapToDto(Borrow borrow) {
        return BorrowDto.builder()
                .id(borrow.getId())
                .userId(borrow.getUser().getId())
                .username(borrow.getUser().getUsername())
                .bookId(borrow.getBook().getId())
                .bookTitle(borrow.getBook().getTitle())
                .borrowedAt(borrow.getBorrowedAt())
                .returnedAt(borrow.getReturnedAt())
                .build();
    }
}
