package com.devbueno.library.api.model.repository;

import com.devbueno.library.api.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepostiroy bookRepostiroy;

	@Test
	@DisplayName("deve retorna verdadeiro se existir um livro com o codigo isbn")
	public void deveRetornaVerdadeiroQuandoBookIsbnExistir() {
		// cenário
		String isbn = "123";
		Book newBook = createNewBook(isbn);
		entityManager.persist(newBook);

		// execução
		boolean bookExiste = bookRepostiroy.existsByIsbn(isbn);

		//verificação
		Assertions.assertThat(bookExiste).isTrue();

	}

	@Test
	@DisplayName("deve retorna false se existir um livro com o codigo isbn nao existir")
	public void deveRetornaFalsoQuandoBookIsbnNaoExistir() {
		// cenário
		String isbn = "123";

		// execução
		boolean bookExiste = bookRepostiroy.existsByIsbn(isbn);

		//verificação
		Assertions.assertThat(bookExiste).isFalse();

	}

	@Test
	@DisplayName("deve obter um livro por id")
	public void deveObterUmLivroPorId() {
		// cenário
		Book book = createNewBook("123");
		entityManager.persist(book);

		// execução.'
		Optional<Book> foundBook = bookRepostiroy.findById(book.getId());

		// verificação
		Assertions.assertThat(foundBook.isPresent()).isTrue();
	}

	@Test
	@DisplayName("deve deletar um livro dado que existe")
	public void deveDeletarUmLivroSeExiste() {
		// cenario
		Book book = createNewBook("123");
		entityManager.persist(book);

		//execuçao
		bookRepostiroy.delete(book);
		Optional<Book> foundBook = bookRepostiroy.findById(book.getId());

		// verificão
		Assertions.assertThat(foundBook.isPresent()).isFalse();
	}

	@Test
	@DisplayName("deve salvar um livro")
	public void salvaLivroValido() {
		// cenário
		Book book = createNewBook("123");

		// execução
		Book newBook = bookRepostiroy.save(book);

		// verificação
		Assertions.assertThat(newBook.getId()).isNotNull();

	}

	@Test
	@DisplayName("deve alterar um livro")
	public void updateBookTest() {
		// cenário
		Book book = createNewBook("123");
		entityManager.persist(book);

		Book bookUpdating = bookRepostiroy.findById(book.getId()).get();
		bookUpdating.setAuthor("DAILAN BUENO");
		// execução
		Book bookUpdated = bookRepostiroy.save(bookUpdating);

		//verificação
		Assertions.assertThat(bookUpdated.getAuthor()).isEqualTo("DAILAN BUENO");

	}

	public static Book createNewBook(String isbn) {
		return Book.builder().author("DAINEL GOLEMAN").title("INTELIGÊNCIA EMOCIONAL").isbn(isbn).build();
	}
}
