package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.LoanApplicationDTO;
import com.mindhub.homebanking.Dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
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
    LoanRepository loanRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ClientLoanRepository clientLoanRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @RequestMapping("/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<Object> createLoan(Authentication authentication,@RequestBody LoanApplicationDTO loanApplication){
        if ( loanApplication.getAmount() == 0 || loanApplication.getPayments() == 0){
            return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
        }
        else {
            Loan loan = loanRepository.findById(loanApplication.getId()).orElse(null);
            if (loan != null){
                if (loanApplication.getAmount() <= loan.getMaxAmount()){
                    if (loan.getPayments().contains(loanApplication.getPayments())){
                        Account destinationAccount = accountRepository.findByNumber(loanApplication.getDestinationNumberAccount());
                        if (destinationAccount != null){
                            Client client = clientRepository.findByEmail(authentication.getName());
                            if (client.getAccounts().contains(destinationAccount)){
                                List<Long> clientLoansIds = client.getClientLoans().stream().map(clientLoan -> clientLoan.getLoan().getId()).collect(Collectors.toList());
                                if (!clientLoansIds.contains(loanApplication.getId())){
                                    transactionRepository.save(new Transaction(TransactionType.CREDIT, loanApplication.getAmount(),  " Prestamo " + loan.getName() + " aprobado", LocalDateTime.now(), destinationAccount));
                                    ClientLoan clientLoan = new ClientLoan(loanApplication.getAmount()*(1 + loan.getInterest()), loanApplication.getPayments(), client, loan);
                                    clientLoanRepository.save(clientLoan);
                                    destinationAccount.setBalance(destinationAccount.getBalance() + loanApplication.getAmount());
                                    return new ResponseEntity<>("Approved loan", HttpStatus.CREATED);
                                } else {
                                    return new ResponseEntity<>("You cannot apply for a loan of the same type", HttpStatus.FORBIDDEN);
                                }

                            } else {
                                return new ResponseEntity<>("The account does not belong to the client",HttpStatus.FORBIDDEN);
                            }
                        } else {
                            return new ResponseEntity<>("Destination account does not exist", HttpStatus.FORBIDDEN);
                        }
                    }
                    else {
                        return new ResponseEntity<>("Wrong Payment", HttpStatus.FORBIDDEN);
                    }
                }
                else {
                    return new ResponseEntity<>("Excess amount", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("Missing Data", HttpStatus.FORBIDDEN);
            }
        }
    }
}
