package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @RequestMapping("/transactions")
    public List<TransactionDTO> getTransactions(){
        return transactionRepository.findAll().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }
    @RequestMapping("/transactions/{id}")
    public TransactionDTO getTransaction(@PathVariable long id){
        return transactionRepository.findById(id).map(TransactionDTO::new).orElse(null);
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam double amount, @RequestParam String description, @RequestParam String sourceNumber, @RequestParam String destinationNumber, Authentication authentication){
        if (description.isEmpty() || sourceNumber.isEmpty() || destinationNumber.isEmpty() || amount == 0.0) {
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        } else {
            if (!sourceNumber.equals(destinationNumber)){
                Account sourceAccount = accountRepository.findByNumber(sourceNumber);
                if (sourceAccount != null){
                    Client client = clientRepository.findByEmail(authentication.getName());
                    if (client != null){
                        if (client.getAccounts().contains(sourceAccount)){
                            Account destinationAccount = accountRepository.findByNumber(destinationNumber);
                            if (destinationAccount != null){
                                String destinationClient = destinationAccount.getOwner().getFirstName() + " " + destinationAccount.getOwner().getLastName();
                                String sourceClient = client.getFirstName() + " " + client.getLastName();
                                if (sourceAccount.getBalance() >= amount){
                                    if (amount >= 10){
                                        Transaction transaction1 = new Transaction(TransactionType.DEBIT, amount, "Transferencia a " + destinationClient + " - " + description, LocalDateTime.now(), sourceAccount);
                                        Transaction transaction2 = new Transaction(TransactionType.CREDIT, amount, "Transferencia de " + sourceClient + " - " + description, LocalDateTime.now(), destinationAccount);
                                        transactionRepository.save(transaction1);
                                        transactionRepository.save(transaction2);
                                        sourceAccount.setBalance(sourceAccount.getBalance() - amount);
                                        destinationAccount.setBalance(destinationAccount.getBalance() + amount);
                                        return new ResponseEntity<>(HttpStatus.CREATED);
                                    } else {
                                        return  new ResponseEntity<>("Min amount",HttpStatus.FORBIDDEN);
                                    }
                                } else {
                                    return new ResponseEntity<>("Not enough amount", HttpStatus.FORBIDDEN);
                                }

                            }else {
                                return new ResponseEntity<>("Missing destination", HttpStatus.FORBIDDEN);
                            }
                        } else {
                            return new ResponseEntity<>("The account does not belong to the client", HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>("Missing client", HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>("Missing source account", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("Same Account", HttpStatus.FORBIDDEN);
            }
        }
    }

    @RequestMapping("/transactions/destination/{number}")
    public Client getDestinationName(@PathVariable String number){
        Client client = clientRepository.findByAccountsNumber(number);
        if (client != null){

            return new Client(client.getLastName(), client.getFirstName());
        } else {
            return new Client();
        }
    }
}
