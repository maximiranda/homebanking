package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository){
		return (args) -> {


			Client client1 = new Client("Melba", "Morel", "melbamorel@gmail.com");
			Client client2 = new Client("Maxi", "Miranda", "maximiranda@gmail.com");
			Account account1 = new Account("VIN001", LocalDateTime.now(), 5000.0, client1);
			Account account2 = new Account("VIN002", LocalDateTime.now().plusDays(1), 7500.0, client1);
			Account account3 = new Account("VIN003", LocalDateTime.now(),156000.0, client2);
			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 100.0, "Prestamo", LocalDateTime.now(), account3);
			Transaction transaction2 = new Transaction(TransactionType.DEBIT, 2000.0, "Compra", LocalDateTime.now(), account1);
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 15000.0, "Venta", LocalDateTime.now(), account2);
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, 200.0, "Compra", LocalDateTime.now(), account2);
			Transaction transaction5 = new Transaction(TransactionType.DEBIT, 20.0, "Compra", LocalDateTime.now(), account3);
			Transaction transaction6 = new Transaction(TransactionType.DEBIT, 500.0, "Compra", LocalDateTime.now(), account2);


			Loan loanH = new Loan("Hipotecario", 500000.0, List.of(12, 24, 36,48, 60));
			Loan loanP = new Loan("Personal", 500000.0, List.of(6, 12, 24));
			Loan loanA = new Loan("Automotriz", 500000.0, List.of(6, 12, 24, 36));

			ClientLoan loan1 = new ClientLoan(400000.0, 60, client1, loanH);
			ClientLoan loan2 = new ClientLoan(50000.0, 12, client1, loanP);
			ClientLoan loan3 = new ClientLoan(100000.0, 24, client2, loanH);
			ClientLoan loan4 = new ClientLoan(200000.0, 36, client2, loanP);

			Card card1 = new Card("1111 2222 3333 4444", CardType.DEBIT, CardColor.GOLD, 345, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);
			Card card2 = new Card("3333 6666 8888 1234", CardType.CREDIT, CardColor.TITANIUM, 987, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client1);
			Card card3 = new Card("3333 6666 8888 1234", CardType.CREDIT, CardColor.SILVER, 456, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client2);

			clientRepository.save(client1);
			clientRepository.save(client2);
			accountRepository.save(account1);
			accountRepository.save(account2);
			accountRepository.save(account3);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction4);
			loanRepository.save(loanH);
			loanRepository.save(loanP);
			loanRepository.save(loanA);
			clientLoanRepository.save(loan1);
			clientLoanRepository.save(loan2);
			clientLoanRepository.save(loan3);
			clientLoanRepository.save(loan4);
			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);
		};
	}
}
