package com.devbueno.library.api.resource;

import com.devbueno.library.api.dto.BookDTO;
import com.devbueno.library.api.dto.LoanDto;
import com.devbueno.library.api.dto.ReturnedLoanDto;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import com.devbueno.library.api.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Api("API DE LEANS")
public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modalMapper;

    @SneakyThrows
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("cadastrar um novo emprestimo")
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("recupera emprestimos por filtro")
    public Page<LoanDto> findByFilter(LoanDto loanFilter, Pageable pageRequest) {
      Loan filter = modalMapper.map(loanFilter, Loan.class);
      Page<Loan> result = service.findByFilter(filter, pageRequest);
        List<LoanDto> loans = result.getContent().stream()
                .map(entity -> {

                    Book book = entity.getBook();
                    BookDTO bookDto = modalMapper.map(book, BookDTO.class);
                    LoanDto loanDto = modalMapper.map(entity, LoanDto.class);
                    loanDto.setBookDTO(bookDto);
                    return loanDto;

                }).collect(Collectors.toList());
        return new PageImpl<LoanDto>(
                loans,
                pageRequest,
                result.getTotalElements()
        );
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("atualiza status de emprestimo")
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto) {
        Loan loan = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(returnedLoanDto.getReturned());
        service.update(loan);
    }

}
