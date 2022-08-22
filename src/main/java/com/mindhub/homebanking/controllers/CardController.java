package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
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

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CardRepository cardRepository;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> addCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client != null){
            List<Card> cards =  client.getCards().stream().filter(card -> card.getType() == cardType).collect(Collectors.toList());
            if (cards.size() < 3){
                String number = (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999));
                int cvv = getRandomNumber(100, 999);
                cardRepository.save(new Card(number, cardType, cardColor, cvv, LocalDateTime.now(), LocalDateTime.now().plusYears(5), client));
                return new ResponseEntity<>("Card created",HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Maximum number of cards of that type",HttpStatus.FORBIDDEN);
            }

        } else {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
    }
}
