package com.devbueno.library.api.model.repository;

import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    Loan save(Loan loanSaving);

    @Query(value = " select case when (count (l.id) > 0) then true else false end " +
            " from Loan l where l.book = :book and ( l.returned is null or  l.returned is false )")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    Loan update(Loan loan);

    Page<Loan> findByBookIsbnOrCustommer(String anyString, String anyString1, Pageable pageable);
}
