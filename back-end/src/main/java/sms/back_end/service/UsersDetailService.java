package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.dto.UsersDetailDTO;
import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.UsersDetail;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.InfobipInfoRepository;
import sms.back_end.repository.UsersDetailRepository;
import sms.back_end.security.JwtUtils;

@Service
public class UsersDetailService {

    private final UsersDetailRepository usersDetailRepository;
    private final InfobipInfoRepository infobipInfoRepository;
    private final JwtUtils jwtUtils;

    public UsersDetailService(UsersDetailRepository usersDetailRepository,
                              InfobipInfoRepository infobipInfoRepository,
                              JwtUtils jwtUtils) {
        this.usersDetailRepository = usersDetailRepository;
        this.infobipInfoRepository = infobipInfoRepository;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UsersDetail createUserDetail(String jwtToken, Long idInfobip) {
        // Récupérer l'ID utilisateur depuis le token
        Long idUtilisateur = jwtUtils.getUserIdFromJwt(jwtToken);
        if (idUtilisateur == null) {
            throw new IllegalArgumentException("Token invalide ou utilisateur introuvable");
        }

        // Vérifier que l'InfobipInfo existe
        InfobipInfo infobipInfo = infobipInfoRepository.findById(idInfobip)
                .orElseThrow(() -> new IllegalArgumentException("InfobipInfo introuvable pour l'id: " + idInfobip));

        // Créer l'objet UsersDetail
        UsersDetail usersDetail = new UsersDetail();
        usersDetail.setIdUtilisateur(idUtilisateur);
        usersDetail.setIdInfobip(idInfobip);
        usersDetail.setInfobipInfo(infobipInfo);
        usersDetail.setDateCreation(LocalDateTime.now());

        // Sauvegarder dans la base
        return usersDetailRepository.save(usersDetail);
    }
    
 
 @Transactional
    public List<UsersDetailDTO> getAllUsersDetailForUser(String jwtToken) {
        Long userId = jwtUtils.getUserIdFromJwt(jwtToken);
        if (userId == null) {
            throw new IllegalArgumentException("Token invalide ou utilisateur introuvable");
        }

        // Récupérer la liste de UsersDetail
        List<UsersDetail> details = usersDetailRepository.findByUserId(userId);

        // Convertir chaque UsersDetail en DTO
        return details.stream()
                      .map(UsersDetailDTO::fromEntity)
                      .collect(Collectors.toList());
    }
    public UsersDetail getUsersDetailByNumero(Long idNumero) {
    return usersDetailRepository.findByInfobipInfo_NumeroExpediteur_Id(idNumero)
            .orElseThrow(() -> new NotFoundException("Utilisateur non trouvé pour idNumero=" + idNumero));
}

}
