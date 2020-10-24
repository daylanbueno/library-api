package com.devbueno.libraryapi.resource;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
