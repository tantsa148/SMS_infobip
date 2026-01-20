# TODO - Ajout méthode envoyerSmsTransactionComplet

## Plan d'implémentation

### 1. Modifier EvenementTransactionService.java
- [x] Ajouter la nouvelle méthode `envoyerSmsTransactionComplet()` avec les paramètres :
  - `Long idNumeroExpediteur`
  - `Long idNumeroDestinataire`
  - `Long idMessage`
  - `String reference`
  - `BigDecimal montant`
  - `String numero`
- [x] La méthode injecte `{{montant}}` et `{{numero}}` dans le template
- [x] L'ancienne méthode `envoyerSmsTransaction()` reste inchangée

### 2. Créer le nouveau DTO
- [x] Créer `TransactionSmsAvecNumeroRequestDTO.java` avec l'attribut `numero`

### 3. Modifier TransactionController.java
- [x] Ajouter l'import du nouveau DTO
- [x] Ajouter la nouvelle méthode endpoint `envoyerSmsTransactionAvecNumero()`

### 4. Tester la nouvelle méthode
- [ ] Vérifier que l'injection des placeholders fonctionne correctement
- [ ] Vérifier que la transaction est enregistrée en base

## Statut
- [ ] En attente de validation
- [ ] En cours d'implémentation
- [x] Terminé

