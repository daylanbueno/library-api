package com.devbueno.libraryapi.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.devbueno.libraryapi.dto.BookDTO;
import com.devbueno.libraryapi.exceptions.ApiErros;
import com.devbueno.libraryapi.exceptions.BusinessException;
import com.devbueno.libraryapi.model.entity.Book;
import com.devbueno.libraryapi.service.BookService;



@RestController
@RequestMapping("/api/books")
public class BookController {

	@Autowired
	BookService servico;

	@Autowired
	ModelMapper modelMapper;

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
