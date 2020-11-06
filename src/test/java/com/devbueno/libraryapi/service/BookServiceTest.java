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

import java.util.Optional;

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

	@Test
	@DisplayName("deve retorna um livro dado que o mesmo existe na base")
	public void deveRetornaUmLivroSeExiste() {
		// cenário
		Book book = createNewValidBook();
		book.setId(12l);
		Mockito.when(bookRepostiroy.findById(Mockito.anyLong())).thenReturn(Optional.of(book));

		// execução
		Book newbook = bookRepostiroy.findById(12l).get();

		assertThat(newbook.getId()).isNotNull();
		assertThat(newbook.getAuthor()).isEqualTo("Marcos");
		assertThat(newbook.getTitle()).isEqualTo("Java em um dia");
	}

	@Test
	@DisplayName("deve retorna vazio se quando o livro nao existe")
	public void deveRetornaVazioQuandoOLivroNaoExiste() {
		// cenário
		Mockito.when(bookRepostiroy.findById(Mockito.anyLong())).thenReturn(Optional.empty());

		// execução
		Optional<Book> newbook = bookRepostiroy.findById(12l);

		// verificação
		assertThat(newbook.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Deve deletar um livro dado que exite")
	public void deveDeletarUmLivroQuandoExistir() {
		// cenário
		Book book = Book.builder().author("Marcos").isbn("123").id(100l).title("Java em um dia").build();

		//execução
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

		Mockito.verify(bookRepostiroy, Mockito.times(1)).delete(book);
	}

	@Test
	@DisplayName("Deve ocorrer um error quando tentar deletar um livro inexistente.")
	public void deletarLivroQueNaoExiste() {
		// cenário
		Book book = new Book();
		// execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->bookService.delete(book));

		// verificação
		Mockito.verify(bookRepostiroy, Mockito.never()).delete(book);
	}

	@Test
	@DisplayName("Deve atualizar o livro dado que o mesmo existe")
	public void deveAtualizarLivroDadoQueExiste() {
		// cenário
		Book book = Book.builder().id(12l).author("dailan").isbn("001").title("Java em 1 dia").build();

		//execução
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.update(book));

		//verificação
		Mockito.verify(bookRepostiroy, Mockito.times(1)).save(book);
	}

	@Test
	@DisplayName("Deve lança um erro se o livro não existir.")
	public void deveLancaErroAoAtualizarLivroQuandoNaoExistir() {
		// cenário
		Book book = new Book();

		//execução
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

		//verificação
		Mockito.verify(bookRepostiroy, Mockito.never()).save(book);
	}


	private Book createNewValidBook() {
		return Book.builder().author("Marcos").isbn("123").title("Java em um dia").build();
	}
}
