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

			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.0);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.0);
			Account account3 = new Account("VIN003", LocalDateTime.now(),156000.0);
			Client client1 = new Client("Melba", "Morel", "melbamorel@gmail.com", account1);
			client1.addAccount(account2);
			Client client2 = new Client("Maxi", "Miranda", "maximiranda@gmail.com", account3);
			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);



/*			Client client1 = new Client("Melba", "Morel", "melbamorel@gmail.com");
			Client client2 = new Client("Maxi", "Miranda", "maximiranda@gmail.com");
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.0, client1);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.0, client1);
			Account account3 = new Account("VIN003", LocalDateTime.now(),156000.0, client2);
			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);*/
			System.out.println(client1.getAccounts());
			System.out.print(client2.getAccounts());
		};
	}
}
