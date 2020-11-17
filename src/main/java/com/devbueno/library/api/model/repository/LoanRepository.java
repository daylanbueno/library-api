package com.devbueno.library.api.model.repository;


import com.devbueno.library.api.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    Loan save(Loan loanSaving);

}
