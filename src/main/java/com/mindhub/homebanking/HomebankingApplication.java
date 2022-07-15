package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository repository){
		return (args) -> {
			repository.save(new Client("Maxi", "Miranda", "maximiranda.95@gmail.com"));
			repository.save(new Client("Juan", "Topo", "juantopo@gmail.com"));
			repository.save(new Client("Maxi", "Miranda", "maximiranda.95@gmail.com"));
		};
	}
}
