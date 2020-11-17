package com.devbueno.library.api.service;

import com.devbueno.library.api.model.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    Loan update(Loan loan);
}
