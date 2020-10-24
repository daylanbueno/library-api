package com.devbueno.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devbueno.libraryapi.model.entity.Book;

@Repository
public interface BookRepostiroy extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

}
