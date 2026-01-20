package sms.back_end.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.TransactionSmsAvecNumeroRequestDTO;
import sms.back_end.dto.TransactionSmsRequestDTO;
import sms.back_end.dto.TransactionSmsResponseDTO;
import sms.back_end.service.EvenementTransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final EvenementTransactionService transactionService;

    public TransactionController(EvenementTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/envoyer-sms")
    public ResponseEntity<TransactionSmsResponseDTO> envoyerSmsTransaction(
            @RequestBody TransactionSmsRequestDTO request
    ) {

        try {
            transactionService.envoyerSmsTransaction(
                    request.getIdNumeroExpediteur(),
                    request.getIdNumeroDestinataire(),
                    request.getIdMessage(),
                    request.getReference(),
                    request.getMontant()
            );

            TransactionSmsResponseDTO response =
                    new TransactionSmsResponseDTO(
                            "SUCCESS",
                            "Transaction envoyée avec succès",
                            request.getReference()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            TransactionSmsResponseDTO response =
                    new TransactionSmsResponseDTO(
                            "ERROR",
                            e.getMessage(),
                            request.getReference()
                    );

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/envoyer-sms-avec-numero")
    public ResponseEntity<TransactionSmsResponseDTO> envoyerSmsTransactionAvecNumero(
            @RequestBody TransactionSmsAvecNumeroRequestDTO request
    ) {

        try {
            transactionService.envoyerSmsTransactionComplet(
                    request.getIdNumeroExpediteur(),
                    request.getIdNumeroDestinataire(),
                    request.getIdMessage(),
                    request.getReference(),
                    request.getMontant(),
                    request.getNumero()
            );

            TransactionSmsResponseDTO response =
                    new TransactionSmsResponseDTO(
                            "SUCCESS",
                            "Transaction envoyée avec succès (avec numero)",
                            request.getReference()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            TransactionSmsResponseDTO response =
                    new TransactionSmsResponseDTO(
                            "ERROR",
                            e.getMessage(),
                            request.getReference()
                    );

            return ResponseEntity.badRequest().body(response);
        }
    }
}

