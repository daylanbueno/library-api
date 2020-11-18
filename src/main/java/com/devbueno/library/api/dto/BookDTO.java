package com.devbueno.library.api.dto;

import javax.validation.constraints.NotEmpty;

import com.devbueno.library.api.model.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder // cria construtor com arqumentos
@NoArgsConstructor // construtor sem arqumentos
@AllArgsConstructor
public class BookDTO {

	private Long id;

	@NotEmpty
	private String title;

	@NotEmpty
	private String author;

	@NotEmpty
	private String isbn;

	private List<LoanDto> loans;

}
