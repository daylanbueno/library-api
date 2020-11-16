package com.devbueno.library.api.service.impl;

import java.util.Optional;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.repository.BookRepostiroy;
import com.devbueno.library.api.service.BookService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.devbueno.library.api.model.entity.Book;

@Service
public class BookServiceImpl implements BookService {

	BookRepostiroy bookRepository;

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
		if (book == null || book.getId() == null) {
			throw new IllegalArgumentException("Erro ao deletar um livro que não existe");
		}
		bookRepository.delete(book);
	}

	@Override
	public Book update(Book entity) {
		if (entity == null || entity.getId() == null) {
			throw new IllegalArgumentException("Erro: o livro não existe existe");
		}
		return bookRepository.save(entity);
	}

	@Override
	public Page<Book> findByFilter(Book filter, Pageable pageRequest) {
		Example<Book> example = Example.of(
				filter,
				ExampleMatcher
						.matching()
						.withIgnoreCase()
						.withIgnoreNullValues()
						.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
		);
		return bookRepository.findAll(example, pageRequest);
	}

	@Override
	public Optional<Book> findBookByIsbn(String isbn) {
		return bookRepository.findByIsbn(isbn);
	}

}
