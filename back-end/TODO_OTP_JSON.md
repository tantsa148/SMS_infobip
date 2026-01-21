# Plan: Modifier les réponses OTP en JSON

## Objectif
Remplacer les messages texte directs par des réponses JSON structurées pour les endpoints OTP.

## Étapes à suivre

### Étape 1: Créer OtpResponseDTO.java
- [x] Créer le DTO avec les champs : success, message, idMessageEnvoye, timestamp

### Étape 2: Modifier OtpController.java
- [x] Importer OtpResponseDTO
- [x] Changer le retour de `/send` pour retourner un JSON
- [x] Changer le retour de `/verify` pour retourner un JSON

## Fichiers concernés
- `src/main/java/sms/back_end/dto/OtpResponseDTO.java` (nouveau)
- `src/main/java/sms/back_end/controller/OtpController.java`

## Statut
Terminé ✅

