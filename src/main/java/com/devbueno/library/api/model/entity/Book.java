package com.devbueno.library.api.model.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data // get, set, hascode, equals
@Builder // construtor
@NoArgsConstructor // construtor sem arqumentos
@AllArgsConstructor // construtor com todos os asrqumentos
@Entity
@Table
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String author;

	private String isbn;

	@OneToMany(mappedBy = "book")
	private List<Loan> loans;

}
