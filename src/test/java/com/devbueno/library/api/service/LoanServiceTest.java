package com.devbueno.library.api.service;

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
        Loan loanSaving = Loan.builder()
                .customer("Eduardo")
                .book(Book.builder().id(1l).build())
                .loanDate(LocalDate.now())
                .build();

        Loan loanSaved = Loan.builder()
                .customer("Eduardo")
                .id(12l)
                .book(Book.builder().id(1l).build())
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.save(loanSaving)).thenReturn(loanSaved);

        // execução
        Loan saved = service.save(loanSaving);

        // verificação
        assertThat(saved.getId()).isEqualTo(loanSaved.getId());
        assertThat(saved.getCustomer()).isEqualTo(loanSaved.getCustomer());
        assertThat(saved.getBook().getId()).isEqualTo(loanSaved.getBook().getId());
    }
}
