package com.devbueno.libraryapi.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	 
}
