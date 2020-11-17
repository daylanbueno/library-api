package com.devbueno.library.api.service;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.model.repository.LoanRepository;
import com.devbueno.library.api.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository loanRepository;

    private LoanService service;

    @BeforeEach
    public void setUp() {
        this.service =  new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("deve salvar um emprestimo com sucesso")
    public void saveLoanTest() {
        // cenário
        Book book = Book.builder().id(1l).build();
        Loan loanSaving = Loan.builder()
                .customer("Eduardo")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Loan loanSaved = Loan.builder()
                .customer("Eduardo")
                .id(12l)
                .book(Book.builder().id(1l).build())
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when(loanRepository.save(loanSaving)).thenReturn(loanSaved);

        // execução
        Loan saved = service.save(loanSaving);

        // verificação
        assertThat(saved.getId()).isEqualTo(loanSaved.getId());
        assertThat(saved.getCustomer()).isEqualTo(loanSaved.getCustomer());
        assertThat(saved.getBook().getId()).isEqualTo(loanSaved.getBook().getId());
    }

    @Test
    @DisplayName("deve lanca uma execao de negocio quando tentar emprestar um livro ja emprestado")
    public void loanedBookSaveTest() {
        // cenário
        Book book = Book.builder().id(1l).build();
        Loan loanSaving = Loan.builder()
                .customer("Eduardo")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        // execução
        Throwable exception = catchThrowable(() -> service.save(loanSaving));

        // verificação
        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned!");
    }


}
