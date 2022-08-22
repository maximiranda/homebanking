package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.getRandomNumber;

@RequestMapping("/api")
@RestController
public class AccountController {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }
    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication){

        int random = getRandomNumber(0, 99999999);
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client != null){
            if (client.getAccounts().toArray().length < 3){
                Account account = new Account("VIN-" + random, LocalDateTime.now(), 0.0, client);
                accountRepository.save(account);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else  {
                return new ResponseEntity<>("Max Accounts",HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>( "Missing Data",HttpStatus.FORBIDDEN);
        }
    }
}
