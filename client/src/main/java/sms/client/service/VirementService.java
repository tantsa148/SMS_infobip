package sms.client.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.entity.Solde;
import sms.client.security.JwtUtilsClient;

@Service
public class VirementService {

    private final RetraitService retraitService;
    private final SoldeService soldeService;
    private final NumeroDestinataireService numeroDestinataireService;
    private final JwtUtilsClient jwtUtilsClient;

    public VirementService(
            RetraitService retraitService,
            SoldeService soldeService,
            NumeroDestinataireService numeroDestinataireService,
            JwtUtilsClient jwtUtilsClient
    ) {
        this.retraitService = retraitService;
        this.soldeService = soldeService;
        this.numeroDestinataireService = numeroDestinataireService;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    /**
     * EFFECTUER UN VIREMENT
     */
    @Transactional
    public Solde effectuerVirement(String token, BigDecimal montant, String numeroRecepteur) {
        
        Long expediteurId = jwtUtilsClient.getUserIdFromToken(token);
        
        List<NumeroDestinataireResponseDTO> tousNumeros = numeroDestinataireService.getAllNumeros(token);
        
        NumeroDestinataireResponseDTO numeroTrouve = tousNumeros.stream()
                .filter(n -> n.getValeur().equals(numeroRecepteur))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Numéro destinataire non trouvé: " + numeroRecepteur));
        
        if (numeroTrouve.getIdUser() == null) {
            throw new RuntimeException("Aucun utilisateur associé au numéro: " + numeroRecepteur);
        }
        Long destinataireId = numeroTrouve.getIdUser().longValue();
        
        Solde soldeExpediteur = retraitService.retirerSolde(token, montant);
        Solde soldeDestinataire = soldeService.addSoldeByUserId(destinataireId, montant);
        
        System.out.println("Virement effectué:");
        System.out.println("Expéditeur (ID " + expediteurId + "): -" + montant + " → Solde: " + soldeExpediteur.getMontant());
        System.out.println("Destinataire (ID " + destinataireId + "): +" + montant + " → Solde: " + soldeDestinataire.getMontant());
        
        return soldeExpediteur;
    }

    public Long getUserIdByNumero(String token, String numero) {
        
        List<NumeroDestinataireResponseDTO> tousNumeros = numeroDestinataireService.getAllNumeros(token);
        
        NumeroDestinataireResponseDTO numeroTrouve = tousNumeros.stream()
                .filter(n -> n.getValeur().equals(numero))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Numéro non trouvé: " + numero));
        
        if (numeroTrouve.getIdUser() != null) {
            return numeroTrouve.getIdUser().longValue();
        }
        
        throw new RuntimeException("Aucun utilisateur associé au numéro: " + numero);
    }
}

