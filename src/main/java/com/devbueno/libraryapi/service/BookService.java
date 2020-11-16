package com.devbueno.libraryapi.service;

import com.devbueno.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface BookService {

	Book save(Book any);

	Optional<Book> findById(Long id);

	void delete(Book book);

    Book update(Book entity);

	Page<Book> findByFilter(Book any, PageRequest pageRequest);

	Optional findBookByIsbn(String isbn);
}
