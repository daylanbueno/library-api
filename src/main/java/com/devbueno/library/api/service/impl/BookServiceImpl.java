package com.devbueno.library.api.service.impl;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.model.repository.BookRepostiroy;
import com.devbueno.library.api.model.repository.LoanRepository;
import com.devbueno.library.api.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j  // disponibilizando log para essa classe
public class BookServiceImpl implements BookService {

	BookRepostiroy bookRepository;
	LoanRepository loanRepository;

	public BookServiceImpl(BookRepostiroy bookRepostiroy, LoanRepository loanRepository) {
		this.bookRepository = bookRepostiroy;
		this.loanRepository = loanRepository;
	}

	@Override
	public Book save(Book book) {
		if(bookRepository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn ja cadastrado");
		}
		log.info("create book for isbn "+book.getIsbn());
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
		log.info("delete book where ID  "+book.getId());
		bookRepository.delete(book);
	}

	@Override
	public Book update(Book entity) {
		if (entity == null || entity.getId() == null) {
			throw new IllegalArgumentException("Erro: o livro não existe existe");
		}
		log.info("updating book where isbn "+entity.getIsbn());
		return bookRepository.save(entity);
	}

	@Override
	public Page<Book> findByFilter(Book filter, Pageable pageRequest) {
		log.info("find book by filter ");
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
		log.info("find book by isbn "+isbn);
		return bookRepository.findByIsbn(isbn);
	}

	@Override
	public Page<Loan> obterEmprestimosPorLivro(Long codigoLivro, Pageable pageRequest) {
		log.info("find loans by book, ID-BOOK "+codigoLivro);
		Book book = bookRepository.findById(codigoLivro).
				orElseThrow(() -> new BusinessException("Book not exists!"));
		return loanRepository.findByBook(book, pageRequest);
	}

}
