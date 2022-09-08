package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.CardPaymentDTO;
import com.mindhub.homebanking.Dtos.TransactionDTO;
import com.mindhub.homebanking.Services.AccountService;
import com.mindhub.homebanking.Services.CardService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.Services.TransactionService;
import com.mindhub.homebanking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static com.mindhub.homebanking.utils.PdfGenerator.getPdf;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    AccountService accountService;
    @Autowired
    ClientService clientService;
    @Autowired
    TransactionService transactionService;

    @Autowired
    CardService cardService;

    @GetMapping("/transactions")
    public List<TransactionDTO> getTransactions(){
        return transactionService.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toList());
    }
    @GetMapping("/transactions/{id}")
    public TransactionDTO getTransaction(@PathVariable long id){
        return new TransactionDTO(transactionService.getTransactionById(id));
    }
    @GetMapping("/transactions/current")
    public ResponseEntity<Object> getTransactionsCurrent(HttpServletResponse response, Authentication authentication, @RequestParam String accountNumber, @RequestParam(required = false) String start, @RequestParam(required = false) String end){
        Client client = clientService.getClientByEmail(authentication.getName());
        Account account = accountService.getAccountByNumber(accountNumber);
        List<Transaction> transactions;
        if (client.getAccounts().contains(account)){
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "inline; filename=maxbank.pdf";
            response.setHeader(headerKey, headerValue);
            if (!(start.isEmpty() || end.isEmpty())){
                LocalDateTime startDate = LocalDateTime.parse(start);
                LocalDateTime endDate = LocalDateTime.parse(end);
                transactions = transactionService.getTransactionsByAccountAndDate(account,startDate,endDate);

            } else {
                transactions = transactionService.getTransactionsByAccount(account);
            }
            getPdf(response, transactions);
            return  new ResponseEntity<>("", HttpStatus.ACCEPTED);
        } else {
            return null;
        }
    }

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestParam double amount, @RequestParam String description, @RequestParam String sourceNumber, @RequestParam String destinationNumber, Authentication authentication){
        if (description.isEmpty() || sourceNumber.isEmpty() || destinationNumber.isEmpty() || amount == 0.0) {
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        } else {
            if (!sourceNumber.equals(destinationNumber)){
                Account sourceAccount = accountService.getAccountByNumber(sourceNumber);
                if (sourceAccount != null){
                    Client client = clientService.getClientByEmail(authentication.getName());
                    if (client != null){
                        if (client.getAccounts().contains(sourceAccount)){
                            Account destinationAccount = accountService.getAccountByNumber(destinationNumber);
                            if (destinationAccount != null){
                                String destinationClient = destinationAccount.getOwner().getFirstName() + " " + destinationAccount.getOwner().getLastName();
                                String sourceClient = client.getFirstName() + " " + client.getLastName();
                                if (sourceAccount.getBalance() >= amount){
                                    if (amount >= 10){
                                        Transaction transaction1 = new Transaction(TransactionType.DEBIT, amount, "Transferencia a " + destinationClient + " - " + description, LocalDateTime.now(), sourceAccount);
                                        Transaction transaction2 = new Transaction(TransactionType.CREDIT, amount, "Transferencia de " + sourceClient + " - " + description, LocalDateTime.now(), destinationAccount);
                                        transactionService.saveTransaction(transaction1);
                                        transactionService.saveTransaction(transaction2);
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

    @GetMapping("/transactions/destination/{number}")
    public Client getDestinationName(@PathVariable String number){
        Client client = clientService.getClientByAccountNumber(number);
        if (client != null){
            return new Client(client.getLastName(), client.getFirstName());
        } else {
            return new Client();
        }
    }
    @Transactional
    @PostMapping("/pays")
    public ResponseEntity<Object> makePay(@RequestBody CardPaymentDTO cardPaymentDTO){
        Card card = cardService.getCardByNumber(cardPaymentDTO.getCardNumber());
        if (card != null){
            if (card.getCardHolder().equals(cardPaymentDTO.getCardHolder()) && card.getCvv() == cardPaymentDTO.getCardCvv() && card.getThruDate().getMonth() == cardPaymentDTO.getCardExp().getMonth() && card.getThruDate().getYear() == cardPaymentDTO.getCardExp().getYear()){
                if (card.getThruDate().isAfter(LocalDate.now())){
                    Client client = card.getClient();
                    Account account = client.getAccounts().stream().findFirst().get();
                    if (account.getBalance() >= cardPaymentDTO.getAmount()){
                        Transaction transaction = new Transaction(TransactionType.DEBIT, cardPaymentDTO.getAmount(), "Pago con tarjeta terminada en "+ card.getNumber().substring(14, 19) + " - " + cardPaymentDTO.getDescription(), LocalDateTime.now(), account);
                        transactionService.saveTransaction(transaction);
                        account.setBalance(account.getBalance() - cardPaymentDTO.getAmount());
                        return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
                    } else {
                        return new ResponseEntity<>("Monto unsificiente",HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>("Tarjeta vencida",HttpStatus.FORBIDDEN);
                }
            }else {
                return new ResponseEntity<>("Datos erroneos",HttpStatus.FORBIDDEN);
            }
        }else {
            return new ResponseEntity<>("Missing data",HttpStatus.FORBIDDEN);
        }
    }
}
