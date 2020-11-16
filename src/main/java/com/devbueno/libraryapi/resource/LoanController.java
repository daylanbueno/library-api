package com.devbueno.libraryapi.resource;

import com.devbueno.libraryapi.dto.LoanDto;
import com.devbueno.libraryapi.exceptions.ApiErros;
import com.devbueno.libraryapi.exceptions.BusinessException;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.model.entity.Loan;
import com.devbueno.libraryapi.service.BookService;
import com.devbueno.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErros(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(BusinessException ex) {
        return new ApiErros(ex);
    }
}
