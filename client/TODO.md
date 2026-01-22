# TODO - Transmission idMessageEnvoye de RegisterController vers OtpController

## Tâches terminées

- [x] Analyser le code existant (RegisterController, OtpController, OtpService, DTOs)
- [x] Créer le plan de modification
- [x] Modifier RegisterController.java pour rediriger vers OTP avec idMessageEnvoye
- [x] Corriger le nom d'attribut de session JWT_TOKEN (LoginController vs OtpController)
- [x] Stocker idMessageEnvoye en session dans AuthRegisterService
- [x] Récupérer idMessageEnvoye depuis la session dans OtpController
- [x] Créer forms.css avec le style harmonisé
- [x] Mettre à jour verify-form.html, register.html, otp-send.html, testNumero.html
- [x] Harmoniser global.css pour les pages avec sidebar

## Résumé des modifications

### Fichiers Java modifiés:
1. **AuthRegisterService.java** - Stocke `idMessageEnvoye` en session après l'envoi OTP
2. **OtpController.java** - Récupère `idMessageEnvoye` depuis la session
3. **RegisterController.java** - Redirige vers OTP après inscription

### Fichiers CSS créés:
1. **forms.css** - Nouveau fichier avec les styles du login réutilisables

### Fichiers CSS modifiés:
1. **global.css** - Harmonisé pour les pages avec sidebar

### Templates mis à jour:
1. **verify-form.html** - Utilise forms.css et form-page
2. **register.html** - Utilise forms.css et form-page
3. **otp-send.html** - Utilise forms.css et form-page
4. **testNumero.html** - Utilise forms.css et form-page

### Pages avec sidebar (pas de changement de structure):
- dashboard.html
- soldeForm.html
- virement.html
- retrait.html


