package sms.back_end.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.SendSmsByIdNumeroDTO;
import sms.back_end.dto.SmsResponseDTO;
import sms.back_end.service.SmsSenderService;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsSenderService smsSenderService;

    public SmsController(SmsSenderService smsSenderService) {
        this.smsSenderService = smsSenderService;
    }

    @PostMapping("/sendByIdNumero")
    public SmsResponseDTO sendSms(@RequestBody SendSmsByIdNumeroDTO dto) {
        return smsSenderService.sendSmsByDestinataireId(dto);
    }
}
