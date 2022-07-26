package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
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
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args) -> {


			Client client1 = new Client("Melba", "Morel", "melbamorel@gmail.com");
			Client client2 = new Client("Maxi", "Miranda", "maximiranda@gmail.com");
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.0, client1);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.0, client1);
			Account account3 = new Account("VIN003", LocalDateTime.now(),156000.0, client2);
			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 100.0, "prestamo", LocalDateTime.now(), account1);
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, 2000.0, "compra", LocalDateTime.now(), account1);
			Transaction transaction3 = new Transaction(TransactionType.DEBIT, 1500.0, "compra", LocalDateTime.now(), account2);

			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
		};
	}
}
