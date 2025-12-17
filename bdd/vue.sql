CREATE OR REPLACE VIEW vue_messages_complets AS
SELECT
    me.id AS id_envoi,
    u.id AS id_utilisateur,
    u.username AS expediteur,
    ne.valeur_numero AS numero_expediteur,
    nd.valeur_numero AS numero_destinataire,
    m.texte AS message,
    me.infobip_message_id,
    ii.base_url AS infobip_base_url,
    ii.api_key AS infobip_api_key,
    ne.id_plateforme,
    p.nom_plateform AS plateforme,
    me.date_envoi
FROM message_envoye me

INNER JOIN numero_expediteur ne 
    ON me.id_numero_expediteur = ne.id_numero

LEFT JOIN plateforme p
    ON ne.id_plateforme = p.id

LEFT JOIN infobip_info ii 
    ON ne.id_infobip = ii.id

LEFT JOIN users_detail ud 
    ON ne.id_numero = ud.id_numero
LEFT JOIN users u 
    ON ud.id_utilisateur = u.id

INNER JOIN numero_destinataire nd 
    ON me.id_numero_destinataire = nd.id_numero

INNER JOIN messages m 
    ON me.id_message = m.id;

