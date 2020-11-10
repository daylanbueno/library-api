package com.devbueno.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public Page<Book> findByFilter(Book any, Pageable any1) {
		return null;
	}

}
