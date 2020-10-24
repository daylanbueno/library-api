package com.devbueno.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devbueno.libraryapi.dto.BookDTO;
import com.devbueno.libraryapi.exceptions.BusinessException;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService bookService;

	@Test
	@DisplayName("deve salvar um livro com sucesso!")
	public void createBookTest() throws Exception {
		
		BookDTO dto = createNewBook();
		
		String jsonRequest = new ObjectMapper().writeValueAsString(dto);

		// mock servico
		Book book = Book.builder().author("T. Harv Eker").title("Os segredos da mente milionária").isbn("001").id(100l)
				.build();
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(book);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(jsonRequest);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(100l))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));

	}

	@Test
	@DisplayName("deve lanchar uma exeção quando não houver dados suficiênte para salvar o livro!")
	public void createInvalidBook() throws Exception {
		String jsonRequest = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(jsonRequest);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

	}

	@Test
	@DisplayName("Deve lanca status 400 se livro com codigo isbn já existe")
	public void createBookWithDuplicateIsbn() throws Exception {
		
		String msgError = "Isbn ja cadastrado";
		String jsonRequest = new ObjectMapper().writeValueAsString(createNewBook());

		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(msgError));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(jsonRequest);
		
		mvc.perform(request)
		.andExpect(status().isBadRequest())
		.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
		.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(msgError));

	}
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("T. Harv Eker").title("Os segredos da mente milionária").isbn("001").build();
	}


}
