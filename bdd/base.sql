-- Supprimer la base (si elle existe)
DROP DATABASE IF EXISTS sms;

-- Créer la base
CREATE DATABASE sms;

-- Pour se connecter à la base sms
\c sms



-- Table Utilisateur (exemple existant)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table Numero
CREATE TABLE numero (
    id_numero SERIAL PRIMARY KEY,
    valeur_numero VARCHAR(20) NOT NULL UNIQUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE numero
ADD CONSTRAINT valeur_numero_non_vide CHECK (valeur_numero <> '');
ALTER TABLE numero RENAME TO numero_expediteur;

-- infobip_info

-- Recréation correcte de infobip_info avec foreign key
CREATE TABLE infobip_info (
    id SERIAL PRIMARY KEY,  -- ID auto-incrémenté
    api_key VARCHAR(50),
    base_url VARCHAR(50),
    id_numero INTEGER,
    CONSTRAINT fk_numero FOREIGN KEY (id_numero)
        REFERENCES numero_expediteur(id_numero)
        ON DELETE SET NULL
);

--usersDetail
CREATE TABLE users_detail (
    id_utilisateur INT NOT NULL,
    id_infobip VARCHAR(50) NOT NULL,  -- on change id_numero en id_infobip
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_utilisateur, id_infobip),
    FOREIGN KEY (id_utilisateur) REFERENCES users(id),
    FOREIGN KEY (id_infobip) REFERENCES infobip_info(id)
);

--Dest
CREATE TABLE numero_destinataire (
    id_numero SERIAL PRIMARY KEY,
    valeur_numero VARCHAR(50) NOT NULL UNIQUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valeur_numero_non_vide CHECK (valeur_numero <> '')
);


CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    texte TEXT NOT NULL
);

-- Table Plateforme
CREATE TABLE plateforme (
    id SERIAL PRIMARY KEY,
    nom_plateform VARCHAR(50) UNIQUE NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Table DisponibleSur (relation N:N entre Numero et Plateforme)
CREATE TABLE destinataire_plateforme (
    id_numero INT NOT NULL,
    id_plateforme INT NOT NULL,
    PRIMARY KEY (id_numero, id_plateforme),
    FOREIGN KEY (id_numero) REFERENCES numero_destinataire(id_numero),
    FOREIGN KEY (id_plateforme) REFERENCES plateforme(id)
);


CREATE TABLE expediteur_plateforme (
    id_numero INT NOT NULL,
    id_plateforme INT NOT NULL,
    PRIMARY KEY (id_numero, id_plateforme),
    FOREIGN KEY (id_numero) REFERENCES numero_expediteur(id_numero),
    FOREIGN KEY (id_plateforme) REFERENCES plateforme(id)
);




