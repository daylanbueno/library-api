package com.devbueno.library.api.service.impl;

import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.model.repository.LoanRepository;
import com.devbueno.library.api.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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

    @Override
    public Optional<Loan> findById(Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> findByFilter(Loan loan, Pageable pageRequest) {
        return loanRepository.findByBookIsbnOrCustommer(loan.getBook().getIsbn(),loan.getCustomer(), pageRequest);
    }
}
