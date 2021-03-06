package com.devbueno.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.model.repository.LoanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.repository.BookRepostiroy;
import com.devbueno.library.api.service.impl.BookServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class) // criar context spring
@ActiveProfiles("test") // subindo profile de test
public class BookServiceTest {

	BookService bookService;

	@MockBean // mock de instancia
	BookRepostiroy bookRepostiroy;

	@MockBean
	LoanRepository loanRepository;

	@BeforeEach
	public void setUp() {
		this.bookService = new BookServiceImpl(bookRepostiroy, loanRepository);
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
		assertDoesNotThrow(() -> bookService.delete(book));

		Mockito.verify(bookRepostiroy, Mockito.times(1)).delete(book);
	}

	@Test
	@DisplayName("Deve ocorrer um error quando tentar deletar um livro inexistente.")
	public void deletarLivroQueNaoExiste() {
		// cenário
		Book book = new Book();
		// execução
		assertThrows(IllegalArgumentException.class, () ->bookService.delete(book));

		// verificação
		Mockito.verify(bookRepostiroy, Mockito.never()).delete(book);
	}

	@Test
	@DisplayName("Deve atualizar o livro dado que o mesmo existe")
	public void deveAtualizarLivroDadoQueExiste() {
		// cenário
		Book book = Book.builder().id(12l).author("dailan").isbn("001").title("Java em 1 dia").build();

		//execução
		assertDoesNotThrow(() -> bookService.update(book));

		//verificação
		Mockito.verify(bookRepostiroy, Mockito.times(1)).save(book);
	}

	@Test
	@DisplayName("Deve lança um erro se o livro não existir.")
	public void deveLancaErroAoAtualizarLivroQuandoNaoExistir() {
		// cenário
		Book book = new Book();

		//execução
		assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

		//verificação
		Mockito.verify(bookRepostiroy, Mockito.never()).save(book);
	}

	@Test
	@DisplayName("Deve atualizar um livro")
	public void deveAtualizarLivro() {
		//cenário
		Book book = Book.builder().id(1l).build();
		Book updatingBook  = Book.builder().id(1l).title("titulo").isbn("123").author("Marcos").build();
		Mockito.when(bookRepostiroy.save(book)).thenReturn(updatingBook);

		//execução
		Book updatedBook = bookService.update(book);

		//verificação
		assertThat(updatedBook).isNotNull();
		assertThat(updatedBook.getTitle()).isEqualTo(updatingBook.getTitle());
		assertThat(updatedBook.getId()).isEqualTo(updatingBook.getId());
		assertThat(updatedBook.getAuthor()).isEqualTo(updatingBook.getAuthor());
		assertThat(updatedBook.getIsbn()).isEqualTo(updatingBook.getIsbn());
	}

	@Test
	@DisplayName("deve filtrar um livro com sucesso dado os parametros")
	public void findBookByFilter() {
		// cenário
		Book book = createNewValidBook();
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Book> bookList = Arrays.asList(book);
		Page<Book> page = new PageImpl<>(bookList, pageRequest,  1);
		Mockito.when(bookRepostiroy.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class))).thenReturn(page);

		// execução
	 	Page<Book> result =	bookService.findByFilter(book, pageRequest);

	 	//verificação
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(bookList);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}

	@Test
	@DisplayName("deve obter um livro por isbn")
	public void  findBookByIsbn() {
		// cenário
		String isbn = "1234";
	  	Mockito.when(bookRepostiroy.findByIsbn(isbn)).
				thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));

		//execução
		Optional<Book> book = bookService.findBookByIsbn(isbn);

		// verificação
		assertThat(book.isPresent()).isTrue();
		assertThat(book.get().getId()).isEqualTo(1l);
		assertThat(book.get().getIsbn()).isEqualTo(isbn);

		Mockito.verify(bookRepostiroy, Mockito.times(1)).findByIsbn(isbn);
	}

	@Test
	@DisplayName("deve recuperar emprestimos de um livro")
	public void findLoansByBookTest() {
		//  cenário
		Book book = createNewValidBook();
		Loan loan = Loan.builder().id(1l).customer("fulano").build();
		book.setLoans(Arrays.asList(loan));
		book.setId(1l);

		Mockito.when(bookRepostiroy.findById(book.getId())).
				thenReturn(Optional.of(book));

		Mockito.when(loanRepository.findByBook(book, PageRequest.of(0, 10))).
				thenReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

		// execução
		Page<Loan> result = bookService.obterEmprestimosPorLivro(1l, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		assertThat(result.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("deve lança uma exeption quando não existir um livro")
	public void findLoanByBookExpectionTest() {
		//  cenário
		Book book = createNewValidBook();
		Loan loan = Loan.builder().id(1l).customer("fulano").build();
		book.setLoans(Arrays.asList(loan));
		book.setId(1l);

		Mockito.when(bookRepostiroy.findById(book.getId())).
				thenReturn(Optional.empty());

		// execução
		Throwable exception = Assertions.catchThrowable(() -> bookService.obterEmprestimosPorLivro(1l, PageRequest.of(0, 10)));

		// verifição
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book not exists!");
//		Mockito.verify(bookRepostiroy, Mockito.never()).findByBook(book, PageRequest.of(0, 10));
	}

	private Book createNewValidBook() {
		return Book.builder().author("Marcos").isbn("123").title("Java em um dia").build();
	}
}
