package com.devbueno.libraryapi.resource;

import com.devbueno.libraryapi.dto.LoanDto;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.model.entity.Loan;
import com.devbueno.libraryapi.service.BookService;
import com.devbueno.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto loanDto) {
        Book book = (Book) bookService.findBookByIsbn(loanDto.getIsbn()).get();

        Loan entity =
                Loan.builder().book(book)
                .customer(loanDto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        Loan loanSaved = service.save(entity);
        return loanSaved.getId();
    }
}
