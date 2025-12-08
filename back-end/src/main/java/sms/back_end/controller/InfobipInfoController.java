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

import sms.back_end.dto.InfobipInfoDTO;
import sms.back_end.dto.InfobipInfoRequest;
import sms.back_end.entity.InfobipInfo;
import sms.back_end.service.InfobipInfoService;

@RestController
@RequestMapping("/api/infobip")
public class InfobipInfoController {

    private final InfobipInfoService service;

    public InfobipInfoController(InfobipInfoService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public InfobipInfoDTO create(@RequestBody InfobipInfoRequest request) {
        InfobipInfo info = new InfobipInfo();
        info.setApiKey(request.getApiKey());
        info.setBaseUrl(request.getBaseUrl());
        InfobipInfo saved = service.createInfobipInfo(info, request.getIdNumero());
        return new InfobipInfoDTO(saved);
    }

    // READ ALL
    @GetMapping
    public List<InfobipInfoDTO> getAll() {
        return service.getAll().stream()
                      .map(InfobipInfoDTO::new)
                      .toList();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public InfobipInfoDTO getById(@PathVariable Long id) {
        return new InfobipInfoDTO(service.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public InfobipInfoDTO update(@PathVariable Long id, @RequestBody InfobipInfoRequest request) {
        InfobipInfo info = new InfobipInfo();
        info.setApiKey(request.getApiKey());
        info.setBaseUrl(request.getBaseUrl());
        InfobipInfo updated = service.update(id, info, request.getIdNumero());
        return new InfobipInfoDTO(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "InfobipInfo supprimé avec succès";
    }
}
