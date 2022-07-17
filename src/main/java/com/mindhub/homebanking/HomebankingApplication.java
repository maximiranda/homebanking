package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return (args) -> {
			Client client1 = new Client("Pedro", "Martinez", "pedromartinez@gmail.com");
			clientRepository.save(client1);
			clientRepository.save(new Client("Maxi", "Miranda", "maximiranda.95@gmail.com"));
			clientRepository.save(new Client("Juan", "Topo", "juantopo@gmail.com"));
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDateTime.now(), 7500.0);
			client1.addAccount(account1);
			client1.addAccount((account2));
			accountRepository.save(account1);
			accountRepository.save(account2);

		};
	}
}
/*.addAccount(new Account("VIN001", LocalDateTime.now(), 5000.0))*/