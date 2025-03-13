package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.entity.Borrow;
import com.library.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUserOrderByBorrowedAtDesc(User user);
    List<Borrow> findByReturnedAtIsNull();
    Optional<Borrow> findByBookAndUserAndReturnedAtIsNull(Book book, User user);
}
