package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.AccountDTO;
import com.mindhub.homebanking.Services.AccountService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
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
    ClientService clientService;
    @Autowired
    AccountService accountService;

    @GetMapping("/accounts")
    public List<AccountDTO> getAccounts(){
        return accountService.getAccounts().stream().map(AccountDTO::new).collect(Collectors.toList());
    }
    @GetMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable long id){
        return new AccountDTO(accountService.getAccountById(id));
    }
    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam AccountType accountType){

        int random = getRandomNumber(0, 99999999);
        Client client = clientService.getClientByEmail(authentication.getName());
        if (client != null){
            if (client.getAccounts().toArray().length < 3){
                Account account = new Account("VIN-" + random, LocalDateTime.now(), 0.0, accountType, client);
                accountService.saveAccount(account);
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else  {
                return new ResponseEntity<>("Max Accounts",HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>( "Missing Data",HttpStatus.FORBIDDEN);
        }
    }
}
