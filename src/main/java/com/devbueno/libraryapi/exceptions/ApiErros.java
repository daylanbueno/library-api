package com.devbueno.libraryapi.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import lombok.Getter;

public class ApiErros {
	
	@Getter
	private List<String> errors;

	public ApiErros(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}

	public ApiErros(BusinessException ex) {
		this.errors  = Arrays.asList(ex.getMessage());
	}
	
}
