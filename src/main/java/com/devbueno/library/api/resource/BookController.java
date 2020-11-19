package com.devbueno.library.api.resource;

import com.devbueno.library.api.dto.BookDTO;
import com.devbueno.library.api.dto.LoanDto;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("BOOK API")
public class BookController {

	private final BookService servico;
	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("cadastrar um novo livro")
	public BookDTO create(@RequestBody @Valid BookDTO bookDto) {
		Book entity = servico.save(modelMapper.map(bookDto, Book.class));
		return  modelMapper.map(entity, BookDTO.class);
	}

	@GetMapping("/{id}")
	@ApiOperation("recupera um livro por id")
	public BookDTO findById(@PathVariable Long id) {
		return servico.findById(id)
				.map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	@ApiOperation("Apaga um livro dado o seu ID")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Book entity = servico.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		servico.delete(entity);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation("atualiza um livro")
	public BookDTO update (@PathVariable Long id, @RequestBody BookDTO book) {
		return servico.findById(id).map(entity -> {
			entity.setAuthor(book.getAuthor());
			entity.setTitle(book.getTitle());
			entity = servico.update(entity);
			return modelMapper.map(entity, BookDTO.class);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	@ApiOperation("recupera livro por filtro")
	public Page<BookDTO> findByFilter(BookDTO dto, Pageable pageRequest) {
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = servico.findByFilter(filter, pageRequest);

		List<BookDTO> list = result.getContent().stream()
				.map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<>(
				list,
				pageRequest,
				result.getTotalElements()
		);
	}

	@GetMapping("/{id}/loans")
	@ApiOperation("recupera emprestimo de um livro")
	public Page<LoanDto> obterEmprestimosPorLivro(@PathVariable Long id, Pageable pageRequest) {
		Page<Loan> result = servico.obterEmprestimosPorLivro(id, pageRequest);
		List<LoanDto> resultado = result.getContent().stream()
				.map(loan -> {
					Book loanBook = modelMapper.map(loan.getBook(), Book.class);
					BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
					LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
					loanDto.setBookDTO(bookDTO);
					return loanDto;
				}).collect(Collectors.toList());
		return new PageImpl<>(resultado, pageRequest, result.getTotalElements());
	}
}
