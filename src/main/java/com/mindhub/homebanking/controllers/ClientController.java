package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;
    @RequestMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(Collectors.toList());
    }
    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }
    @PostMapping("/clients")
    Client newClient(@RequestBody Client newClient) {
        String formatString = String.format("%%0%dd", 7);
        Account account = new Account();
        accountRepository.save(account);
        String number = String.format(formatString, account.getId());
        account.setNumber("VIN" + number);
        account.setBalance(0.0);
        account.setCreationDate(LocalDateTime.now());
        account.setOwner(newClient);
        Client client = clientRepository.save(newClient);
        accountRepository.save(account);
        return  client;
    }
    @PatchMapping("/clients/{id}")
    Client updateClient(@RequestBody Client upClient){
        return clientRepository.save(upClient);
    }

    @DeleteMapping("/clients/{id}")
    public void deleteClient(@PathVariable long id){
        Client client = clientRepository.findById(id).orElse(null);
        Stack<Long> transactionsIds = new Stack<>();
        Set<Account> accounts = client.getAccounts();
        accounts.forEach(account -> {
            account.getTransactions().forEach(transaction -> {
                transactionsIds.push(transaction.getId());
            });
        });
        transactionRepository.deleteAllById(transactionsIds);
        accountRepository.deleteAllById(accounts.stream().map(Account::getId).collect(Collectors.toList()));
        clientRepository.delete(client);
    }
    @RequestMapping("/clients/current")
    public ClientDTO get(Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }
}