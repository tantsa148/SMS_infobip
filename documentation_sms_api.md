# Documentation --- Messagerie multicanal basée sur des API

## Présentation

Pour configurer l'application, l'utilisateur doit d'abord créer un
compte sur la plateforme **Infobip**.\
Une fois le compte activé, récupérez les informations suivantes afin de
finaliser l'intégration :

-   **Base URL**
-   **API Key**

Ces identifiants seront utilisés pour permettre à l'application
d'envoyer des SMS via l'API Infobip.

------------------------------------------------------------------------

# Configuration du projet

## 1. Base de données

Insérez tous les scripts de structure et de données dans le dossier :

    bdd/

L'application utilise ce dossier pour initialiser la base de données.

------------------------------------------------------------------------

## 2. Fichier de configuration

Un fichier **application.properties** doit être présent dans :

    SMS_/back-end/src/main/resources/

Ce fichier contient les variables nécessaires au fonctionnement du
service : - PostgreSQL - Logs - JWT - Actuator

### Exemple de configuration

``` properties
# Général
spring.application.name=SMS
logging.level.root=INFO
logging.level.Birger.SMS=DEBUG

# Configuration PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/votre_base
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=off

# Sécurité & JWT
jwt.expiration-ms=86400000
jwt.refresh-expiration-ms=604800000
jwt.secret=uneCleSuperSecreteTresLonguePourJWT1234567890

# Monitoring & Actuator
management.endpoints.web.exposure.include=*
management.endpoint.mappings.enabled=true
```

⚠️ **Important :**\
La valeur `jwt.secret` doit être modifiée en production pour garantir la
sécurité des jetons.

------------------------------------------------------------------------

# Prérequis système

Avant toute manipulation, vérifiez que votre environnement respecte les
versions suivantes :

-   **Java SDK : version 17**
-   **Node.js : version 20**

Si nécessaire :

``` bash
nvm install 20
nvm use 20
```

------------------------------------------------------------------------

# Lancement de l'application

## 1. Lancement du Back‑end

Depuis la racine du projet :

``` bash
cd SMS_/back-end
mvn clean install
mvn spring-boot:run
```

Cette commande : - nettoie les builds précédents - installe les
dépendances Maven - démarre le serveur Spring Boot

------------------------------------------------------------------------

## 2. Lancement du Front‑end

Dans un nouveau terminal :

``` bash
cd SMS_/front-end
npm install
npm run dev
```

L'application sera accessible localement sur :

    http://localhost:5173

(selon la configuration Vite ou Next.js).

⚠️ Assurez-vous que : - PostgreSQL est démarré - le fichier
`application.properties` est correctement configuré

------------------------------------------------------------------------

# Utilisation de l'application

## Création d'un compte utilisateur

Avant d'utiliser l'application, un utilisateur doit être créé via l'API.

### Endpoint d'inscription

-   **Méthode :** POST
-   **URL :**

```{=html}
<!-- -->
```
    http://localhost:8080/api/auth/register

-   **Content-Type :** `application/json`

### Exemple de requête

``` json
{
  "username": "votre_nom",
  "password": "mot_de_passe",
  "role": "ADMIN"
}
```

### Points importants

-   Une fois le compte créé, vous pouvez vous connecter à l'application.
-   Utilisez un mot de passe sécurisé en production.
-   Vérifiez que le back-end fonctionne sur le port **8080**.

------------------------------------------------------------------------

# Accès à l'interface Web

## URL

    http://localhost:5173

## Connexion

1.  Ouvrir votre navigateur (Chrome, Firefox ou Edge).
2.  Accéder à l'URL ci‑dessus.
3.  Se connecter avec les identifiants créés.

Exemple :

    admin / 1234

------------------------------------------------------------------------

# Configuration Infobip

Une fois connecté :

1.  Cliquer sur votre **nom d'utilisateur** en haut à droite.
2.  Sélectionner **Mes numéros**.
3.  Cliquer sur **Ajouter**.
4.  Remplir les informations suivantes :

-   **Base URL**
-   **API Key**
-   **Numéro Infobip**

------------------------------------------------------------------------

# Gestion des événements et messages

## 1. Création d'un événement

Un événement correspond au **contexte d'envoi du message**.

Exemples :

-   Rappel de rendez‑vous
-   Alerte sécurité
-   Message d'anniversaire

Créer un événement dans la section **Événements**.

------------------------------------------------------------------------

## 2. Création d'un message

Ensuite :

1.  Aller dans la section **Messages**
2.  Rédiger le texte du SMS
3.  Associer le message à un événement

Avantage :\
Le texte peut être modifié sans changer la logique métier.

------------------------------------------------------------------------

# Gestion des destinataires

## Option 1 : via l'interface

1.  Aller dans **Destinataires / Clients**
2.  Ajouter un numéro
3.  Choisir :
    -   la plateforme
    -   l'utilisateur

------------------------------------------------------------------------

## Option 2 : via l'API

### Endpoint

    POST http://localhost:8080/api/numeros_destinataire

### Requête JSON

``` json
{
  "valeur": "261377428541",
  "plateforme": {
    "id": 1
  },
  "user": {
    "id": 3
  }
}
```

### Description des champs

  Champ           Description
  --------------- ------------------------------------------------
  valeur          Numéro du destinataire au format international
  plateforme.id   Configuration Infobip utilisée
  user.id         Utilisateur propriétaire du contact

------------------------------------------------------------------------

# Envoi des messages (interface)

Pour envoyer un SMS :

1.  Sélectionner un **message**
2.  Choisir un **destinataire**
3.  Cliquer sur **envoyer**

L'application gère automatiquement la correspondance entre : -
événement - message - destinataire - configuration Infobip

------------------------------------------------------------------------

# Envoi des messages via API

## Endpoint

    POST http://localhost:8080/api/sms/send

## Corps de la requête

``` json
{
  "idNumeroExpediteur": 1,
  "idNumeroDestinataire": 1,
  "idMessage": 1
}
```

## Description

  Paramètre              Description
  ---------------------- -----------------------
  idNumeroExpediteur     Configuration Infobip
  idNumeroDestinataire   Destinataire
  idMessage              Message à envoyer
                         
