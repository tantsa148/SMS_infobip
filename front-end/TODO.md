# Plan: Ajouter l'option de message personnalisé dans SMS

## Modifications nécessaires:

### 1. `src/services/smsService.ts`
- Ajouter une nouvelle méthode `sendSmsWithMessage` qui accepte `message` (string) au lieu de `idMessage`
- La méthode devra envoyer:
  ```json
  {
    "idNumeroExpediteur": 1,
    "idNumeroDestinataire": 1,
    "message": "texte du message"
  }
  ```

### 2. `src/components/ModalSendMessage.vue`
- Ajouter une ref `useCustomMessage` (boolean) pour le checkbox
- Ajouter une ref `customMessageText` (string) pour le champ de texte
- Ajouter le checkbox et le champ de texte conditionnel
- Modifier `showConfirm()` pour gérer les deux cas (message prédéfini ou personnalisé)
- Modifier `finalSend()` pour utiliser la bonne méthode du service selon le cas
- **AJOUT**: Remplacer tous les `alert()` par des notifications toast

## Suivi:
- [x] 1. Modifier smsService.ts - ajouter sendSmsWithMessage
- [x] 2. Modifier ModalSendMessage.vue - ajouter checkbox et champ texte
- [x] 3. Modifier finalSend() pour utiliser la méthode appropriée
- [x] 4. Remplacer alerts par notifications toast


