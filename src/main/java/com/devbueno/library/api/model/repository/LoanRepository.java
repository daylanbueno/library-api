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

    @Query(value = "select l from Loan as l join l.book as b  where l.customer = :customer or b.isbn = :isbn")
     Page<Loan> findByBookIsbnOrCustommer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);
}
