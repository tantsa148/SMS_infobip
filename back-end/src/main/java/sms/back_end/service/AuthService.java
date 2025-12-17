package sms.back_end.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sms.back_end.entity.Role;
import sms.back_end.entity.User;
import sms.back_end.repository.RoleRepository;
import sms.back_end.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String password, String roleName) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }

        // 🔎 Recherche du rôle en base
        Role role = roleRepository.findByRole(roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Rôle introuvable : " + roleName));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // mot de passe crypté
        user.setRole(role);

        return userRepository.save(user);
    }
}
