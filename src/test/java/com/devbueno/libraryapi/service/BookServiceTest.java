package com.devbueno.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devbueno.libraryapi.exceptions.BusinessException;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.model.repository.BookRepostiroy;
import com.devbueno.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class) // criar context spring
@ActiveProfiles("test") // subindo profile de test
public class BookServiceTest {

	BookService bookService;

	@MockBean // mock de instancia
	BookRepostiroy bookRepostiroy;

	@BeforeEach
	public void setUp() {
		this.bookService = new BookServiceImpl(bookRepostiroy);
	}

	@Test
	@DisplayName("deve salvar um livro com sucesso")
	public void savedBookTest() {
		// Cenário
		Book book = createNewValidBook();
		Book newbook = Book.builder().author("Marcos").isbn("123").id(100l).title("Java em um dia").build();
		Mockito.when(bookRepostiroy.save(book)).thenReturn(newbook);
		Mockito.when(bookRepostiroy.existsByIsbn(Mockito.anyString())).thenReturn(false);

		// execução
		Book newBook = bookService.save(book);

		// verificação
		assertThat(newBook.getId()).isNotNull();
		assertThat(newBook.getTitle()).isEqualTo("Java em um dia");
		assertThat(newBook.getAuthor()).isEqualTo("Marcos");
		assertThat(newBook.getIsbn()).isEqualTo("123");

	}

	@Test
	@DisplayName("Deve gerar erro de negocio ao tentar salvar um livro com isbn ja cadastrado")
	public void deveLanchaExcecaoIsbnJaCadastrado() {

		Book book = createNewValidBook();
		String msgError = "Isbn ja cadastrado";
		Mockito.when(bookRepostiroy.existsByIsbn(Mockito.anyString())).thenReturn(true);

		Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage(msgError);

		Mockito.verify(bookRepostiroy, Mockito.never()).save(book);
	}

	private Book createNewValidBook() {
		return Book.builder().author("Marcos").isbn("123").title("Java em um dia").build();
	}
}
