package sms.back_end.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.InfobipInfoDTO;
import sms.back_end.entity.InfobipInfo;
import sms.back_end.service.InfobipInfoService;

@RestController
@RequestMapping("/api/infobip")
public class InfobipInfoController {

    private final InfobipInfoService service;

    public InfobipInfoController(InfobipInfoService service) {
        this.service = service;
    }

    @PostMapping
    public InfobipInfoDTO create(@RequestBody InfobipInfo info) {
        InfobipInfo saved = service.createInfobipInfo(info);
        return new InfobipInfoDTO(saved);
    }

    @GetMapping
    public List<InfobipInfoDTO> getAll() {
        return service.getAll().stream()
                      .map(InfobipInfoDTO::new)
                      .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public InfobipInfoDTO getById(@PathVariable Long id) {
        return new InfobipInfoDTO(service.getById(id));
    }

    @PutMapping("/{id}")
    public InfobipInfoDTO update(@PathVariable Long id, @RequestBody InfobipInfo updated) {
        InfobipInfo saved = service.update(id, updated);
        return new InfobipInfoDTO(saved);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "InfobipInfo supprimé avec succès";
    }
}
