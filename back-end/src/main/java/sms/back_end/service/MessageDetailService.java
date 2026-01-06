package sms.back_end.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import sms.back_end.entity.MessageDetail;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.repository.MessageDetailRepository;
import sms.back_end.repository.MessageEnvoyeRepository;

@Service
public class MessageDetailService {

    private final MessageDetailRepository messageDetailRepository;
    private final MessageEnvoyeRepository messageEnvoyeRepository;

    public MessageDetailService(MessageDetailRepository messageDetailRepository,
                                MessageEnvoyeRepository messageEnvoyeRepository) {
        this.messageDetailRepository = messageDetailRepository;
        this.messageEnvoyeRepository = messageEnvoyeRepository;
    }

    /**
     * Sauvegarde les détails d'un message Infobip (SMS ou WhatsApp)
     * Recherche automatiquement le MessageEnvoye associé via infobip_message_id
     * 
     * @param json JsonNode renvoyé par l'API Infobip
     * @return MessageDetail sauvegardé
     */
    public MessageDetail saveMessageDetail(JsonNode json) {
        System.out.println("[MessageDetailService] DÉBUT saveMessageDetail");
        System.out.println("[MessageDetailService] JSON reçu: " + json);
        
        MessageDetail detail = new MessageDetail();

        try {
            // 1️⃣ Message ID et association avec MessageEnvoye
            String messageId = null;
            if (json.has("messageId") && !json.get("messageId").isNull()) {
                messageId = json.path("messageId").asText();
                detail.setMessageId(messageId);
                System.out.println("[MessageDetailService] messageId: " + messageId);
                
                // 🆕 Rechercher le MessageEnvoye correspondant
                MessageEnvoye messageEnvoye = messageEnvoyeRepository.findByInfobipMessageId(messageId)
                        .orElse(null);
                
                if (messageEnvoye != null) {
                    detail.setMessageEnvoye(messageEnvoye);
                    System.out.println("[MessageDetailService] ✓ MessageEnvoye trouvé et associé: ID=" + messageEnvoye.getId());
                } else {
                    System.out.println("[MessageDetailService] WARNING: Aucun MessageEnvoye trouvé pour messageId: " + messageId);
                }
            } else {
                System.out.println("[MessageDetailService] WARNING: messageId manquant");
            }

            // 2️⃣ Texte du message
            if (json.has("text") && !json.get("text").isNull()) {
                String text = json.path("text").asText();
                detail.setText(text);
                System.out.println("[MessageDetailService] text: " + text);
            } else {
                System.out.println("[MessageDetailService] WARNING: text manquant");
            }

            // 3️⃣ Dates
            if (json.has("sentAt") && !json.get("sentAt").isNull()) {
                String sentAtStr = json.path("sentAt").asText();
                System.out.println("[MessageDetailService] sentAt brut: " + sentAtStr);
                LocalDateTime sentAt = parseDate(sentAtStr);
                detail.setSentAt(sentAt);
                System.out.println("[MessageDetailService] sentAt parsé: " + sentAt);
            }

            if (json.has("doneAt") && !json.get("doneAt").isNull()) {
                String doneAtStr = json.path("doneAt").asText();
                System.out.println("[MessageDetailService] doneAt brut: " + doneAtStr);
                LocalDateTime doneAt = parseDate(doneAtStr);
                detail.setDoneAt(doneAt);
                System.out.println("[MessageDetailService] doneAt parsé: " + doneAt);
            }

            // 4️⃣ Statut
            if (json.has("status") && !json.get("status").isNull()) {
                JsonNode statusNode = json.path("status");
                detail.setStatusId(statusNode.path("id").asInt(0));
                detail.setStatusName(statusNode.path("name").asText(null));
                detail.setStatusDescription(statusNode.path("description").asText(null));
                System.out.println("[MessageDetailService] status: id=" + detail.getStatusId() 
                                    + ", name=" + detail.getStatusName());
            } else {
                System.out.println("[MessageDetailService] WARNING: status manquant");
            }

            // 5️⃣ Prix
            if (json.has("price") && !json.get("price").isNull()) {
                JsonNode priceNode = json.path("price");
                
                if (priceNode.has("pricePerMessage") && !priceNode.get("pricePerMessage").isNull()) {
                    try {
                        BigDecimal price = new BigDecimal(priceNode.path("pricePerMessage").asText());
                        detail.setPricePerMessage(price);
                        System.out.println("[MessageDetailService] pricePerMessage: " + price);
                    } catch (Exception e) {
                        System.err.println("[MessageDetailService] Erreur parsing prix: " + e.getMessage());
                    }
                }
                
                if (priceNode.has("currency") && !priceNode.get("currency").isNull()) {
                    detail.setCurrency(priceNode.path("currency").asText());
                    System.out.println("[MessageDetailService] currency: " + detail.getCurrency());
                }
            } else {
                System.out.println("[MessageDetailService] WARNING: price manquant");
            }

            // Sauvegarde en base
            System.out.println("[MessageDetailService] Tentative de sauvegarde en base...");
            MessageDetail saved = messageDetailRepository.save(detail);
            System.out.println("[MessageDetailService] ✓ Sauvegarde réussie! ID: " + saved.getId());
            
            if (saved.getMessageEnvoye() != null) {
                System.out.println("[MessageDetailService] ✓ Relation enregistrée avec MessageEnvoye ID: " + saved.getMessageEnvoye().getId());
            }
            
            return saved;
            
        } catch (Exception e) {
            System.err.println("[MessageDetailService] ✗ ERREUR lors de la sauvegarde:");
            System.err.println("[MessageDetailService] Type: " + e.getClass().getName());
            System.err.println("[MessageDetailService] Message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur sauvegarde MessageDetail: " + e.getMessage(), e);
        }
    }

    /**
     * Parse une date au format ISO avec offset de fuseau horaire
     * Gère plusieurs formats:
     * - 2026-01-04T17:58:52.675+0000
     * - 2026-01-04T17:58:52.675Z
     * - 2026-01-04T17:58:52Z
     * - 2026-01-04T17:58:52
     */
    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            System.out.println("[MessageDetailService] Date vide ou null");
            return null;
        }

        try {
            System.out.println("[MessageDetailService] Parsing date: " + dateStr);
            
            // Cas 1: Format avec offset +0000 ou +01:00
            if (dateStr.contains("+") || (dateStr.contains("-") && dateStr.lastIndexOf('-') > 10)) {
                // Remplacer +0000 par Z si nécessaire
                String normalized = dateStr;
                if (dateStr.matches(".*\\+\\d{4}$")) {
                    // Format: 2026-01-04T17:58:52.675+0000 -> ajouter ':'
                    normalized = dateStr.substring(0, dateStr.length() - 2) + ":" + dateStr.substring(dateStr.length() - 2);
                }
                
                ZonedDateTime zdt = ZonedDateTime.parse(normalized, DateTimeFormatter.ISO_DATE_TIME);
                return zdt.toLocalDateTime();
            }
            // Cas 2: Format avec Z (UTC)
            else if (dateStr.endsWith("Z")) {
                ZonedDateTime zdt = ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                return zdt.toLocalDateTime();
            }
            // Cas 3: Format simple sans fuseau horaire
            else {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
        } catch (DateTimeParseException e) {
            System.err.println("[MessageDetailService] ✗ Erreur parsing date: " + dateStr);
            System.err.println("[MessageDetailService] Erreur: " + e.getMessage());
            
            // Tentative de parsing alternatif (supprimer millisecondes si problème)
            try {
                String withoutMillis = dateStr.replaceAll("\\.\\d+", "");
                System.out.println("[MessageDetailService] Tentative sans millisecondes: " + withoutMillis);
                return parseDate(withoutMillis);
            } catch (Exception e2) {
                System.err.println("[MessageDetailService] Impossible de parser la date, retour null");
                return null;
            }
        }
    }

    /**
     * Récupère un MessageDetail par son ID
     *
     * @param id ID du MessageDetail
     * @return MessageDetail trouvé
     * @throws RuntimeException si aucun MessageDetail n'est trouvé
     */
    public MessageDetail getById(Long id) {
        System.out.println("[MessageDetailService] Recherche MessageDetail par ID: " + id);
        return messageDetailRepository.findById(id)
                .orElseThrow(() -> {
                    System.err.println("[MessageDetailService] ✗ Aucun MessageDetail trouvé pour ID: " + id);
                    return new RuntimeException("MessageDetail introuvable pour l'ID " + id);
                });
    }

    /**
     * Récupère tous les MessageDetail
     *
     * @return Liste de tous les MessageDetail en base
     */
    public List<MessageDetail> getAll() {
        System.out.println("[MessageDetailService] Récupération de tous les MessageDetail");
        List<MessageDetail> messages = messageDetailRepository.findAll();
        System.out.println("[MessageDetailService] ✓ " + messages.size() + " MessageDetail trouvés");
        return messages;
    }

    /**
     * Récupère tous les MessageDetail liés à un MessageEnvoye
     *
     * @param idMessageEnvoye ID du MessageEnvoye
     * @return Liste des MessageDetail associés
     */
    public List<MessageDetail> getByMessageEnvoyeId(Integer idMessageEnvoye) {
        System.out.println("[MessageDetailService] Recherche MessageDetail par idMessageEnvoye: " + idMessageEnvoye);
        List<MessageDetail> details = messageDetailRepository.findByMessageEnvoyeId(idMessageEnvoye);
        System.out.println("[MessageDetailService] ✓ " + details.size() + " MessageDetail trouvés pour idMessageEnvoye: " + idMessageEnvoye);
        return details;
    }

    /**
     * Récupère un MessageDetail unique par son idMessageEnvoye
     * Utile si la relation est 1-1
     *
     * @param idMessageEnvoye ID du MessageEnvoye
     * @return MessageDetail trouvé
     * @throws RuntimeException si aucun MessageDetail n'est trouvé
     */
    public MessageDetail getByMessageEnvoyeIdUnique(Integer idMessageEnvoye) {
        System.out.println("[MessageDetailService] Recherche MessageDetail unique par idMessageEnvoye: " + idMessageEnvoye);
        return messageDetailRepository.findByMessageEnvoye_Id(idMessageEnvoye)
                .orElseThrow(() -> {
                    System.err.println("[MessageDetailService] ✗ Aucun MessageDetail trouvé pour idMessageEnvoye: " + idMessageEnvoye);
                    return new RuntimeException("MessageDetail introuvable pour l'idMessageEnvoye " + idMessageEnvoye);
                });
    }

    
}