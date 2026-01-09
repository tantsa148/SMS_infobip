package sms.client.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.dto.modele.ModeleMessageDTO;
import sms.client.dto.transaction.TransactionRequestDTO;
import sms.client.entity.Solde;
import sms.client.repository.SoldeRepository;
import sms.client.security.JwtUtilsClient;

@Service
public class SoldeService {

    private final SoldeRepository soldeRepository;
    private final JwtUtilsClient jwtUtilsClient;
    private final ModeleMessageClientService modeleMessageClientService;
    private final TransactionClientService transactionClientService;
    private final NumeroDestinataireService numeroDestinataireService; // 🔹 injecté

    public SoldeService(
            SoldeRepository soldeRepository,
            JwtUtilsClient jwtUtilsClient,
            ModeleMessageClientService modeleMessageClientService,
            TransactionClientService transactionClientService,
            NumeroDestinataireService numeroDestinataireService // 🔹 ajouté
    ) {
        this.soldeRepository = soldeRepository;
        this.jwtUtilsClient = jwtUtilsClient;
        this.modeleMessageClientService = modeleMessageClientService;
        this.transactionClientService = transactionClientService;
        this.numeroDestinataireService = numeroDestinataireService; // 🔹 assigné
    }

@Transactional
public Solde addSoldeEtEnvoyerTransaction(
        String token,
        BigDecimal montant,
        String methode
) {

    // 1️⃣ Récupérer user
    Long userId = jwtUtilsClient.getUserIdFromToken(token);

    // 2️⃣ Récupérer ou créer le solde
    Solde solde = soldeRepository.findByUserId(userId)
            .orElseGet(() -> {
                Solde s = new Solde();
                s.setUserId(userId);
                s.setMontant(BigDecimal.ZERO);
                return soldeRepository.save(s);
            });

    // 3️⃣ Ajouter le montant
    solde.setMontant(solde.getMontant().add(montant));
    soldeRepository.save(solde);

    // 4️⃣ Récupérer le modèle message
    ModeleMessageDTO modele = modeleMessageClientService.findByMethode(methode, token);

    if (modele == null) {
        throw new RuntimeException("Aucun modèle trouvé pour la méthode " + methode);
    }

    // 5️⃣ Récupérer le premier numéro destinataire pour cet utilisateur avec JWT
    var numeroDestinataire = numeroDestinataireService.getFirstNumeroByUserId(userId, token);
    if (numeroDestinataire == null) {
        throw new RuntimeException("Aucun numéro destinataire trouvé pour l'utilisateur " + userId);
    }

    // 6️⃣ Créer la transaction
    TransactionRequestDTO transaction = new TransactionRequestDTO();
    transaction.setIdNumeroExpediteur(modele.getIdExpediteur());
    transaction.setIdNumeroDestinataire(numeroDestinataire.getIdNumero()); // ✅ utilise l'idNumero récupéré
    transaction.setIdMessage(modele.getIdMessage());
    transaction.setMontant(montant);

    // 🔹 Référence EN DUR (temporaire)
    transaction.setReference("TXN-ADD-SOLDE-003");

    // 7️⃣ Appel API transaction
    transactionClientService.envoyerTransaction(transaction, token);

    // 8️⃣ Log console
    System.out.println("✅ Transaction envoyée");
    System.out.println("Référence : " + transaction.getReference());
    System.out.println("Montant   : " + montant);
    System.out.println("Message   : " + modele.getTexteMessage());

    return solde;
}
}
