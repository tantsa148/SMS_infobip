package sms.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.entity.SmsMessage;
import sms.back_end.service.SmsMessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final SmsMessageService service;

    public MessageController(SmsMessageService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public SmsMessage createMessage(@RequestBody SmsMessage message) {
        return service.createMessage(message);
    }

    // READ ALL
    @GetMapping
    public List<SmsMessage> getAllMessages() {
        return service.getAllMessages();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public SmsMessage getMessageById(@PathVariable Long id) {
        return service.getMessageById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));
    }

    // UPDATE
    @PutMapping("/{id}")
    public SmsMessage updateMessage(@PathVariable Long id, @RequestBody SmsMessage message) {
        return service.updateMessage(id, message);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable Long id) {
        service.deleteMessage(id);
    }
}
