package com.devbueno.library.api.service;

import com.devbueno.library.api.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    Loan update(Loan loan);

    Page<Loan> findByFilter(Loan loan, Pageable pageRequest);

    List<Loan> getAllLateLoans();
}
