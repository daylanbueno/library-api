package com.devbueno.library.api;

import com.devbueno.library.api.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

//	@Autowired
//	EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	public CommandLineRunner commandLineRunner () {
//		return args -> {
//			List<String> emails = Arrays.asList("book-api-44e708@inbox.mailtrap.io");
//			emailService.sendMails("Olá, seu livro está atrasado! Favor Entrega-lo logo.",emails);
//			System.out.println("SEU EMAIL FOI ENVIADO!");
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
