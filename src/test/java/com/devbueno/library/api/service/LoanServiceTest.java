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

import javax.swing.text.html.Option;
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
        Loan loanSaving = createNewLoan();

        Loan loanSaved = Loan.builder()
                .customer("Eduardo")
                .id(12l)
                .book(Book.builder().id(1l).build())
                .loanDate(LocalDate.now())
                .build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(loanSaving.getBook())).thenReturn(false);
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
        Loan loanSaving = createNewLoan();

        Mockito.when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        // execução
        Throwable exception = catchThrowable(() -> service.save(loanSaving));

        // verificação
        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned!");
    }

    public static  Loan createNewLoan() {
        Book book = Book.builder().id(1l).build();
        return Loan.builder()
                .customer("Eduardo")
                .book(book)
                .loanDate(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("deve obter LOAN por ID")
    public void loanByIdTest() {
        // cenário
        Long id = 1l;
        Loan loan = createNewLoan();
        loan.setId(id);
        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        // execução
        Optional<Loan> loanSaved = service.findById(id);

        // verifição
        assertThat(loanSaved.isPresent()).isTrue();
        assertThat(loanSaved.get().getId()).isEqualTo(id);
        assertThat(loanSaved.get().getCustomer()).isEqualTo(loan.getCustomer());
    }

    @Test
    @DisplayName("deve atualizar o emprestimo  para retornado ")
    public void updateLoanTest() {
        // cenário
        Loan loanSaving = createNewLoan();
        Loan mockLoanUpdated = createNewLoan();
        mockLoanUpdated.setId(10l);
        mockLoanUpdated.setReturned(true);
        Mockito.when(loanRepository.save(loanSaving)).thenReturn(mockLoanUpdated);

        // execução
        Loan loanUpdated = service.update(loanSaving);

        // verifição;
        assertThat(loanUpdated.getId()).isNotNull();
        assertThat(loanUpdated.getCustomer()).isEqualTo(mockLoanUpdated.getCustomer());
        assertThat(loanUpdated.getReturned()).isEqualTo(mockLoanUpdated.getReturned());
    }




}
