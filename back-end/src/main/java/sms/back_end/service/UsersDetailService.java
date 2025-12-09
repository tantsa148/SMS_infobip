package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.dto.UsersDetailDTO;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.entity.UsersDetail;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroExpediteurRepository;
import sms.back_end.repository.UsersDetailRepository;
import sms.back_end.security.JwtUtils;

@Service
public class UsersDetailService {

    private final UsersDetailRepository usersDetailRepository;
    private final NumeroExpediteurRepository numeroExpediteurRepository;
    private final JwtUtils jwtUtils;

    public UsersDetailService(UsersDetailRepository usersDetailRepository,
                              NumeroExpediteurRepository numeroExpediteurRepository,
                              JwtUtils jwtUtils) {
        this.usersDetailRepository = usersDetailRepository;
        this.numeroExpediteurRepository = numeroExpediteurRepository;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UsersDetail createUserDetail(String jwtToken, Long idNumero) {
        Long idUtilisateur = jwtUtils.getUserIdFromJwt(jwtToken);
        if (idUtilisateur == null) {
            throw new IllegalArgumentException("Token invalide ou utilisateur introuvable");
        }

        NumeroExpediteur numeroExpediteur = numeroExpediteurRepository.findById(idNumero)
                .orElseThrow(() -> new IllegalArgumentException("NumeroExpediteur introuvable pour l'id: " + idNumero));

        UsersDetail usersDetail = new UsersDetail();
        usersDetail.setIdUtilisateur(idUtilisateur);
        usersDetail.setIdNumero(idNumero);
        usersDetail.setNumeroExpediteur(numeroExpediteur);
        usersDetail.setDateCreation(LocalDateTime.now());

        return usersDetailRepository.save(usersDetail);
    }

    @Transactional
    public List<UsersDetailDTO> getAllUsersDetailForUser(String jwtToken) {
        Long idUtilisateur = jwtUtils.getUserIdFromJwt(jwtToken);
        if (idUtilisateur == null) {
            throw new IllegalArgumentException("Token invalide ou utilisateur introuvable");
        }

        List<UsersDetail> details = usersDetailRepository.findByIdUtilisateur(idUtilisateur);

        return details.stream()
                      .map(UsersDetailDTO::fromEntity)
                      .collect(Collectors.toList());
    }

    public UsersDetail getUsersDetailByNumero(Long idNumero) {
        return usersDetailRepository.findByNumeroExpediteur_Id(idNumero)
                .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé pour idNumero=" + idNumero));
    }
}
