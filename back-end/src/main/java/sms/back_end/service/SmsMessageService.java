package sms.back_end.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import sms.back_end.entity.SmsMessage;
import sms.back_end.repository.SmsMessageRepository;

@Service
public class SmsMessageService {

    private final SmsMessageRepository repository;

    public SmsMessageService(SmsMessageRepository repository) {
        this.repository = repository;
    }

    public SmsMessage createMessage(SmsMessage message) {
    Optional<SmsMessage> existing = repository.findByTexte(message.getTexte());
    
    if (existing.isPresent()) {
        return existing.get();
    }

    // Associer l'événement si fourni
    if (message.getEvenement() != null && message.getEvenement().getId() != null) {
        // Optionnel : vérifier si l'événement existe en base
        // Si tu veux, tu peux injecter EvenementRepository et valider ici
    }

    return repository.save(message);
}

    // CREATE - Version qui retourne l'ID (existant ou nouveau)
    public Long createMessageAndGetId(SmsMessage message) {
        Optional<SmsMessage> existing = repository.findByTexte(message.getTexte());
        
        if (existing.isPresent()) {
            return existing.get().getId(); // Retourne l'ID existant
        }
        
        SmsMessage saved = repository.save(message);
        return saved.getId(); // Retourne le nouvel ID
    }

    // READ ALL
    public List<SmsMessage> getAllMessages() {
        return repository.findAll();
    }

    // READ BY ID
    public Optional<SmsMessage> getMessageById(Long id) {
        return repository.findById(id);
    }

    // READ BY TEXTE
    public Optional<SmsMessage> getMessageByTexte(String texte) {
        return repository.findByTexte(texte);
    }

    // VÉRIFIER l'existence
    public boolean messageExists(String texte) {
        return repository.existsByTexte(texte);
    }

    // GET ou CREATE (retourne toujours un message)
    public SmsMessage getOrCreateMessage(String texte) {
        return repository.findByTexte(texte)
                .orElseGet(() -> {
                    SmsMessage newMessage = new SmsMessage(texte);
                    return repository.save(newMessage);
                });
    }

    // UPDATE
    public SmsMessage updateMessage(Long id, SmsMessage updatedMessage) {
    return repository.findById(id).map(message -> {
        message.setTexte(updatedMessage.getTexte());
        message.setEvenement(updatedMessage.getEvenement()); // ← ajouter ici
        return repository.save(message);
    }).orElseThrow(() -> new RuntimeException("Message non trouvé"));
}
    // DELETE
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }
}