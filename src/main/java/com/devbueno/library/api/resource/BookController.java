package com.devbueno.library.api.resource;

import javax.validation.Valid;

import com.devbueno.library.api.dto.LoanDto;
import com.devbueno.library.api.exceptions.ApiErros;
import com.devbueno.library.api.exceptions.BusinessException;
import com.devbueno.library.api.model.entity.Book;
import com.devbueno.library.api.model.entity.Loan;
import com.devbueno.library.api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.devbueno.library.api.dto.BookDTO;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookService servico;
	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO bookDto) {
		Book entity = servico.save(modelMapper.map(bookDto, Book.class));
		return  modelMapper.map(entity, BookDTO.class);
	}

	@GetMapping("/{id}")
	public BookDTO findById(@PathVariable Long id) {
		return servico.findById(id)
				.map(book -> modelMapper.map(book, BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Book entity = servico.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		servico.delete(entity);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO update (@PathVariable Long id, @RequestBody BookDTO book) {
		return servico.findById(id).map(entity -> {
			entity.setAuthor(book.getAuthor());
			entity.setTitle(book.getTitle());
			entity = servico.update(entity);
			return modelMapper.map(entity, BookDTO.class);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@GetMapping
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
