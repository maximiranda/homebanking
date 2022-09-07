package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.ClientDTO;
import com.mindhub.homebanking.Services.AccountService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.getRandomNumber;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    ClientService clientService;
    @Autowired
    AccountService accountService;

    @Autowired
    TransactionRepository transactionRepository;
    @GetMapping("/clients")
    public List<ClientDTO> getClients(){
        return clientService.getClients().stream().map(ClientDTO::new).collect(Collectors.toList());
    }
    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable long id){
        return new ClientDTO(clientService.getClientById(id));
    }
    @PatchMapping("/clients/{id}")
     public void updateClient(@RequestBody Client upClient){
        clientService.saveClient(upClient);
    }
    @PostMapping("/clients")
    public ResponseEntity<Object> register(

            @RequestParam String firstName, @RequestParam String lastName,

            @RequestParam String email, @RequestParam String password) {


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {

            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);

        }

        if (clientService.getClientByEmail(email) !=  null) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }
        int random = getRandomNumber(0, 99999999);
        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        clientService.saveClient(client);
        accountService.saveAccount(new Account("VIN-" + random, LocalDateTime.now(), 0.0, AccountType.SAVINGS, client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/clients/current")
    public ClientDTO get(Authentication authentication){
        Client client = clientService.getClientByEmail(authentication.getName());
        return new ClientDTO(client);
    }
}