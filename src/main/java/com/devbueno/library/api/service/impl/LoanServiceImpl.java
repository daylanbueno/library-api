package com.devbueno.library.api.service.impl;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.model.repository.LoanRepository;
import com.devbueno.library.api.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if(loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned!");
        }
        return loanRepository.save(loan);
    }
}
