package sms.back_end.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.TransactionSmsRequestDTO;
import sms.back_end.service.EvenementTransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final EvenementTransactionService transactionService;

    public TransactionController(EvenementTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/envoyer-sms")
    public ResponseEntity<String> envoyerSmsTransaction(@RequestBody TransactionSmsRequestDTO request) {
        
        try {
            transactionService.envoyerSmsTransaction(
                request.getIdNumeroExpediteur(),
                request.getIdNumeroDestinataire(),
                request.getIdMessage(),
                request.getReference(),
                request.getMontant()
            );
            
            return ResponseEntity.ok("Transaction envoyée avec succès: " + request.getReference());
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}