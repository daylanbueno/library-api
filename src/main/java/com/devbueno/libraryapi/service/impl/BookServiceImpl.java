package com.devbueno.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.devbueno.libraryapi.exceptions.BusinessException;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.model.repository.BookRepostiroy;
import com.devbueno.libraryapi.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	BookRepostiroy  bookRepository;

	public BookServiceImpl(BookRepostiroy bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public Book save(Book book) {
		if(bookRepository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn ja cadastrado");
		}
		return bookRepository.save(book);
	}

	@Override
	public Optional<Book> findById(Long id) {
		return bookRepository.findById(id);
	}

	@Override
	public void delete(Book book) {
		bookRepository.delete(book);
	}

	@Override
	public Book update(Book entity) {
		return null;
	}

}
