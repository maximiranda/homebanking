package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.LoanApplicationDTO;
import com.mindhub.homebanking.Dtos.LoanDTO;
import com.mindhub.homebanking.Services.*;
import com.mindhub.homebanking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    LoanService loanService;
    @Autowired
    AccountService accountService;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientLoanService clientLoanService;
    @Autowired
    TransactionService transactionService;

    @RequestMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanService.getLoans().stream().map(LoanDTO::new).collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(Authentication authentication,@RequestBody LoanApplicationDTO loanApplication){
        if ( loanApplication.getAmount() == 0 || loanApplication.getPayments() == 0){
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }
        else {
            Loan loan = loanService.getLoanById(loanApplication.getId());
            if (loan != null){
                if (loanApplication.getAmount() <= loan.getMaxAmount()){
                    if (loan.getPayments().contains(loanApplication.getPayments())){
                        Account destinationAccount = accountService.getAccountByNumber(loanApplication.getDestinationNumberAccount());
                        if (destinationAccount != null){
                            Client client = clientService.getClientByEmail(authentication.getName());
                            if (client.getAccounts().contains(destinationAccount)){
                                List<Long> clientLoansIds = client.getClientLoans().stream().map(clientLoan -> clientLoan.getLoan().getId()).collect(Collectors.toList());
                                if (!clientLoansIds.contains(loanApplication.getId())){
                                    transactionService.saveTransaction(new Transaction(TransactionType.CREDIT, loanApplication.getAmount(),  " Prestamo " + loan.getName() + " aprobado", LocalDateTime.now(), destinationAccount));
                                    ClientLoan clientLoan = new ClientLoan(loanApplication.getAmount()*(1 + loan.getInterest()), loanApplication.getPayments(), client, loan);
                                    clientLoanService.saveClientLoan(clientLoan);
                                    destinationAccount.setBalance(destinationAccount.getBalance() + loanApplication.getAmount());
                                    return new ResponseEntity<>("Approved loan", HttpStatus.CREATED);
                                } else {
                                    return new ResponseEntity<>("Ya tienes un prestamo de este tipo", HttpStatus.FORBIDDEN);
                                }

                            } else {
                                return new ResponseEntity<>("La cuenta no te pertenece",HttpStatus.FORBIDDEN);
                            }
                        } else {
                            return new ResponseEntity<>("La cuenta de destino no existe", HttpStatus.FORBIDDEN);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Cantidad de cuotas no disponible", HttpStatus.FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity<>("Monto excedido", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
            }
        }
    }
}
