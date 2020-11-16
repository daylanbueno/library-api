package com.devbueno.library.api.model.repository;

import com.devbueno.library.api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepostiroy extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
