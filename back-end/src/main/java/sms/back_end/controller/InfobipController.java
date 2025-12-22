package sms.back_end.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.InfobipBalanceDTO;
import sms.back_end.service.InfobipService;

@RestController
public class InfobipController {

    private final InfobipService infobipService;

    public InfobipController(InfobipService infobipService) {
        this.infobipService = infobipService;
    }

    @GetMapping("/api/infobip/balance")
    public InfobipBalanceDTO getBalance(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam Long idNumero
    ) {
        // Retirer "Bearer " si présent
        if (jwtToken.toLowerCase().startsWith("bearer ")) {
            jwtToken = jwtToken.substring(7);
        }
        return infobipService.getBalance(jwtToken, idNumero);
    }
}
