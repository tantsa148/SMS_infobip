package sms.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.UsersDetailDTO;
import sms.back_end.dto.UsersDetailRequest;
import sms.back_end.entity.UsersDetail;
import sms.back_end.service.UsersDetailService;

@RestController
@RequestMapping("/api/users-detail")
public class UsersDetailController {

    private final UsersDetailService usersDetailService;

    public UsersDetailController(UsersDetailService usersDetailService) {
        this.usersDetailService = usersDetailService;
    }

    // CREATE
    @PostMapping
    public UsersDetailDTO createUsersDetail(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UsersDetailRequest request) {

        String token = authHeader.replace("Bearer ", "");
        UsersDetail created = usersDetailService.createUserDetail(token, request.getIdNumero());
        return UsersDetailDTO.fromEntity(created);
    }

    // READ ALL pour l'utilisateur connecté
    @GetMapping
    public List<UsersDetailDTO> getAllUsersDetail(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return usersDetailService.getAllUsersDetailForUser(token);
    }
}
