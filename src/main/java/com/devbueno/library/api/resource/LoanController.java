package com.devbueno.library.api.resource;

import com.devbueno.library.api.dto.LoanDto;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import com.devbueno.library.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @SneakyThrows
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto loanDto) {
        Book book = (Book) bookService.findBookByIsbn(loanDto.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity =
                Loan.builder().book(book)
                .customer(loanDto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        Loan loanSaved = service.save(entity);
        return loanSaved.getId();
    }

}
