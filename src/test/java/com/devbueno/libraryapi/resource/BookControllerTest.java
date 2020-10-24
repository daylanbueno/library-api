package com.devbueno.libraryapi.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
	
	@Autowired
	ModelMapper modelMapper;

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
	
	@Test
	@DisplayName("Deve retornar um  livro por id dado que existe") 
	public void deveRetornaUmLivroPorId() throws Exception {
		// cenário
		Book book = modelMapper.map(createNewBook(), Book.class) ;
		book.setId(10l);
	    BDDMockito.given(bookService.findById(book.getId())).willReturn(Optional.of(book));
	    MockHttpServletRequestBuilder  request = MockMvcRequestBuilders.get(BOOK_API.concat("/")+book.getId())
	    		.accept(MediaType.APPLICATION_JSON);
	    
	  
	    // execução e verificação
	    mvc.perform(request)
	    .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
	    .andExpect(MockMvcResultMatchers.jsonPath("id").value(book.getId()))
	    .andExpect(MockMvcResultMatchers.status().isOk());
				
	}
	
	@Test
	@DisplayName("Deve retorna status code 404  quando nao existir um livro para o ID informado")
	public void recuperaLivroPorId() throws Exception {
		// cenário
		BDDMockito.given(bookService.findById(Mockito.anyLong())).willReturn(Optional.empty());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/")+12l)
				.accept(MediaType.APPLICATION_JSON);
		
		//execução e verificação
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	
	private BookDTO createNewBook() {
		return BookDTO.builder().author("T. Harv Eker").title("Os segredos da mente milionária").isbn("001").build();
	}


}
