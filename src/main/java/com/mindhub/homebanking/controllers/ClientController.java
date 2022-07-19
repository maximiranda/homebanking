package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {
    @Autowired
    ClientRepository clientRepository;
    @RequestMapping("/clients")
    public List<Object> getClients(){
        return clientRepository.findAll().stream().map(client -> client.toDTO()).collect(Collectors.toList());
    }
}
