package com.devbueno.library.api.resource;

import com.devbueno.library.api.dto.LoanDto;
import com.devbueno.library.api.dto.ReturnedLoanDto;
import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import com.devbueno.library.api.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.devbueno.library.api.service.LoanServiceTest.createNewLoan;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    static final String LOAN_API = "/api/loans";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("deve realizar um emprestimo com sucesso")
    public void createLoanTest() throws Exception {
        // cenário
        LoanDto dto = LoanDto.builder().isbn("123").customer("Marcos").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.findBookByIsbn("123"))
                .willReturn(Optional.of(Book.builder().id(1l).isbn("123").build()));

        Loan loan = Loan.builder().returned(true).id(1l).customer("Fulano").loanDate(LocalDate.now()).book(getNewBook()).build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // execução e verificação
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect( content().string("1") );
    }

    @Test
    @DisplayName("deve lançar uma exeção quando não encontrar o livro para o id informado.")
    public void erroLoansTest() throws Exception {
        // cenário
        LoanDto dto = LoanDto.builder().isbn("123").customer("Marcos").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.findBookByIsbn("123"))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // execução e verificação
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors").value(Matchers.hasSize(1)) )
                .andExpect( jsonPath("errors[0]").value("Book not found for passed isbn") );
    }


    @Test
    @DisplayName("Deve retorna um erro ao tentar emprestar um livro já emprestado ")
    public void createBookLeaned() throws Exception {
        // cenário
        LoanDto dto = LoanDto.builder().isbn("123").customer("Marcos").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.findBookByIsbn("123"))
                .willReturn(Optional.of(Book.builder().id(1l).isbn("123").build()));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // execução e verificação
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors").value(Matchers.hasSize(1)) );
              //  .andExpect( jsonPath("errors[0]").value("Book already loaned!") );
    }

    @Test
    @DisplayName("deve atualizas status de um livro devolvido")
    public void returnedLoadTest() throws Exception{
        // cenário { returned: true }
        ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Loan loan = Loan.builder().book(Book.builder().id(1l).build()).id(1l).returned(false).build();

        BDDMockito.given(loanService.findById(1l)).willReturn(Optional.of(loan));
        MockHttpServletRequestBuilder request =
                MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // execução
        ResultActions perform = mvc.perform(request);

        // verificação
        perform.andExpect(status().isOk());
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("deve devolver 404 ao tentar atualizar status de livro inexistente.")
    public void returnedBookEnexistente() throws Exception {
        // cenário { returned: true }
        ReturnedLoanDto dto = ReturnedLoanDto.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.findById(1l)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // execução
        ResultActions perform = mvc.perform(request);

        // verificação
        perform.andExpect(status().isNotFound());
        Mockito.verify(loanService, Mockito.never()).update(Mockito.any());
    }

    @Test
    @DisplayName("deve recupear  Loan por filtro")
    public void findLoanByFilter() throws Exception {
        // cenário
        Loan loan = createNewLoan();
        loan.setId(1l);
        loan.getBook().setIsbn("123");
        BDDMockito.given(loanService.findByFilter(Mockito.any(Loan.class), Mockito.any(PageRequest.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat("?isbn=123&customer=&page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        // execução
        ResultActions perform = mvc.perform(request);

        // verificação
        perform.andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("content[0].bookDTO.isbn").value("123"))
        .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
        .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }


    private Book getNewBook() {
        return Book.builder().id(1l).build();
    }
}
