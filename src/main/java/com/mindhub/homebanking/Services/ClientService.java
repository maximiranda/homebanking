package com.mindhub.homebanking.Services;

import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {

    public List<Client> getClients();
    public Client getClientById(Long id);
    public Client getClientByEmail(String email);
    public  Client getClientByAccountNumber(String number);
    public void saveClient(Client client);

}
