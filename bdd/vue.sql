CREATE OR REPLACE VIEW vue_messages_complets AS
SELECT
    me.id AS id_envoi,
    u.id AS id_utilisateur,
    u.username AS expediteur,
    ne.valeur_numero AS numero_expediteur,
    nd.valeur_numero AS numero_destinataire,
    m.texte AS message,
    me.infobip_message_id,
    ii.base_url AS infobip_base_url,   -- base URL
    ii.api_key AS infobip_api_key,     -- ajout de l'API key
    me.date_envoi
FROM message_envoye me
-- jointure avec numéro expéditeur
INNER JOIN numero_expediteur ne ON me.id_numero_expediteur = ne.id_numero
-- jointure avec Infobip pour récupérer la base_url et api_key
LEFT JOIN infobip_info ii ON ne.id_infobip = ii.id
-- jointure avec users_detail pour récupérer l'utilisateur
LEFT JOIN users_detail ud ON ne.id_numero = ud.id_numero
LEFT JOIN users u ON ud.id_utilisateur = u.id
-- jointure avec numéro destinataire
INNER JOIN numero_destinataire nd ON me.id_numero_destinataire = nd.id_numero
-- jointure avec le texte du message
INNER JOIN messages m ON me.id_message = m.id;

