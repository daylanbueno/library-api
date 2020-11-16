package com.devbueno.library.api.service;

import com.devbueno.library.api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

	Book save(Book any);

	Optional<Book> findById(Long id);

	void delete(Book book);

    Book update(Book entity);

	Page<Book> findByFilter(Book any, Pageable pageRequest);

	Optional<Book> findBookByIsbn(String isbn);
}
