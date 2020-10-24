package com.devbueno.libraryapi.service;

import java.util.Optional;

import com.devbueno.libraryapi.model.entity.Book;

public interface BookService {

	Book save(Book any);

	Optional<Book> findById(Long id);

}
