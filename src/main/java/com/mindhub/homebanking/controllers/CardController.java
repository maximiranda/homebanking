package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.Dtos.CardDTO;
import com.mindhub.homebanking.Services.CardService;
import com.mindhub.homebanking.Services.ClientService;
import com.mindhub.homebanking.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.getRandomNumber;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    ClientService clientService;
    @Autowired
    CardService cardService;

    @GetMapping("/cards")
    public List<CardDTO> getCards(){
        return cardService.getCards().stream().map(CardDTO::new).collect(Collectors.toList());
    }
    @GetMapping("/card/{id}")
    public CardDTO getCard(@PathVariable Long id){
        return new CardDTO(cardService.getCardById(id));
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<Object> addCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor, Authentication authentication){
        Client client = clientService.getClientByEmail(authentication.getName());
        if (client != null){
            List<Card> cards =  client.getCards().stream().filter(Card::getIsActive).filter(card -> card.getType() == cardType).collect(Collectors.toList());
            boolean exist = cards.stream().anyMatch(c -> c.getColor() == cardColor);
            if (cards.size() < 3){
                if (!exist){
                    String number = (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999)) + " " + (getRandomNumber(1000, 9999));
                    int cvv = getRandomNumber(100, 999);
                    cardService.saveCard(new Card(number, cardType, cardColor, cvv, LocalDate.now(), LocalDate.now().plusYears(5), client));
                    return new ResponseEntity<>("Card created",HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Only one card per color", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("Maximum number of cards of that type",HttpStatus.FORBIDDEN);
            }

        } else {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
    }
    @PatchMapping("/cards/delete")
    public ResponseEntity<Object> disableAccount(Authentication authentication,@RequestParam String cardNumber){
        Client client = clientService.getClientByEmail(authentication.getName());
        Card card = cardService.getCardByNumber(cardNumber);
        if (client == null){
            return new ResponseEntity<>("1", HttpStatus.FORBIDDEN);
        }

        if (card == null){
            return new ResponseEntity<>("2", HttpStatus.FORBIDDEN);
        }
        card.setActive(false);
        cardService.saveCard(card);
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }
}
