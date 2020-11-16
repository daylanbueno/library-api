package com.devbueno.libraryapi.resource;

import com.devbueno.libraryapi.dto.LoanDto;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.model.entity.Loan;
import com.devbueno.libraryapi.service.BookService;
import com.devbueno.libraryapi.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDate;
import java.util.Optional;

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

    private Book getNewBook() {
        return Book.builder().id(1l).build();
    }
}
