package com.devbueno.library.api.model.repository;

import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.devbueno.library.api.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    @DisplayName("deve verificar se existe emprestimo não devolvido para o livro.")
    public void existsByBookAndNotReturnedTest() {
        // cenários
        Book book = criandoBookELoanNabase(LocalDate.now()).getBook();

        // execução
        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        // verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("deve pesquisar por  isbn ou customer")
    public void findByFilterTest() {
        Loan loan = criandoBookELoanNabase(LocalDate.now());
        Page<Loan> result = loanRepository.findByBookIsbnOrCustommer(
                "123", "fulano", PageRequest.of(0, 10)
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getContent()).contains(loan);

    }

    @Test
    @DisplayName("deve objter  os emprestimos atrasados, data maior dias e não foi devolvido")
    public void findByLoanDateLassThanAndNotReturnedTest() {
        criandoBookELoanNabase(LocalDate.now().minusDays(5));

        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
        assertThat(result).hasSize(1);
    }


    @Test
    @DisplayName("deve retorna lista vazia quando nao tiver  emprestimos atrasados")
    public void notFindByLoanDateLassThanAndNotReturnedTest() {
        criandoBookELoanNabase(LocalDate.now());

        List<Loan> result = loanRepository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));
        assertThat(result).hasSize(0);
    }


    private Loan criandoBookELoanNabase(LocalDate dataEmprestimo) {
        Book book = createNewBook("123");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).email("customer@gmail.com").customer("MARCOS").loanDate(dataEmprestimo).build();
        entityManager.persist(loan);
        return loan;
    }


}
