package com.devbueno.library.api.resource;

import com.devbueno.library.api.dto.BookDTO;
import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static com.devbueno.library.api.service.LoanServiceTest.createNewLoan;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
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

        BookDTO dto = createNewBookDto();

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
        String jsonRequest = new ObjectMapper().writeValueAsString(createNewBookDto());

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
        Book book = modelMapper.map(createNewBookDto(), Book.class);
        book.setId(10l);
        BDDMockito.given(bookService.findById(book.getId())).willReturn(Optional.of(book));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/") + book.getId())
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
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/") + 12l)
                .accept(MediaType.APPLICATION_JSON);

        //execução e verificação
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deletarLivroExistente() throws Exception {
        // Cenário
        BDDMockito.given(bookService.findById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(10l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/") + 10l)
                .accept(MediaType.APPLICATION_JSON);

        //execução e verificação
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    @Test
    @DisplayName("Deve lanca 404 quando tentar deletar um livro inexistente!")
    public void deletarLivroInexistente() throws Exception {
        // Cenário
        BDDMockito.given(bookService.findById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/") + 10l)
                .accept(MediaType.APPLICATION_JSON);

        //execução e verificação
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar um livro dado que existe")
    public void deveAtualizarUmLivro() throws Exception {
        // cenário
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBookDto());
        Book bookDefault = Book.builder().id(id).title("Default").author("Default").isbn("111").build();

        Book updatedBook = Book.builder().id(id).title("Atravessando o inferno").author("Dailan").isbn("111").build();

        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willReturn(Optional.of(bookDefault));

        BDDMockito.given(bookService.update(Mockito.any()))
                .willReturn(updatedBook);

        // execução
        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificação
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(updatedBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(updatedBook.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(updatedBook.getIsbn()));
    }

    @Test
    @DisplayName("Deve atualizar um livro dado que nao existe")
    public void deveAtualizarQueNaoExiste() throws Exception {
        // cenário

        String json = new ObjectMapper().writeValueAsString(createNewBookDto());
        BDDMockito.given(bookService.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // execução
        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders
                .put(BOOK_API.concat("/"+1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificação
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve recuperar livros por paramentros")
    public void findBookByFilter() throws Exception {
        // cenário
        Long id = 1l;
        Book book = createEntityBook(id);

        BDDMockito.given(bookService.findByFilter(Mockito.any(Book.class), Mockito.any(PageRequest.class)))
                .willReturn(new PageImpl<>(Arrays.asList(book), PageRequest.of(0, 10), 1));

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("?title=os&author=T.&page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        // verificação
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect( MockMvcResultMatchers.jsonPath("content",Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }


    @Test
    @DisplayName("deve recuperar  emprestimos de um livro")
    public void findLoansBookTest() throws Exception {
       // cenário
        Long idBook = 1l;
        Loan loan = createNewLoan();
        Book book = createEntityBook(idBook);
        book.setId(idBook);

        book.setLoans(Arrays.asList(loan));

        BDDMockito.given(bookService.obterEmprestimosPorLivro(Mockito.anyLong(), Mockito.any(PageRequest.class)))
        .willReturn(new PageImpl<Loan>(book.getLoans(), PageRequest.of(0, 10), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/1/loans?page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        // execução
        ResultActions perform = mvc.perform(request);

        // verificação
       perform.andExpect(status().isOk())
              .andExpect(MockMvcResultMatchers.jsonPath("content",Matchers.hasSize(1)))
              .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
              .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
              .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBookDto() {
        return BookDTO.builder().author("T. Harv Eker").title("Os segredos da mente milionária").isbn("001").build();
    }

    private Book createEntityBook(Long id) {
        return Book.builder()
                .id(id)
                .title(createNewBookDto().getTitle())
                .author(createNewBookDto().getAuthor())
                .isbn(createNewBookDto().getIsbn()).build();
    }


}
