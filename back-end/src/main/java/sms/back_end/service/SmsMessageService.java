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

    // CREATE - Ne crée que si le message n'existe pas
    public SmsMessage createMessage(SmsMessage message) {
        // Vérifier si le message existe déjà (texte exact)
        Optional<SmsMessage> existing = repository.findByTexte(message.getTexte());
        
        if (existing.isPresent()) {
            // Retourne le message existant sans en créer un nouveau
            return existing.get();
        }
        
        // Crée un nouveau message seulement s'il n'existe pas
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
            return repository.save(message);
        }).orElseThrow(() -> new RuntimeException("Message non trouvé"));
    }

    // DELETE
    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }
}