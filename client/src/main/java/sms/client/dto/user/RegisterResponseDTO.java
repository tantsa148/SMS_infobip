package sms.client.dto.user;

public class RegisterResponseDTO {

    private boolean success;
    private String message;
    private UserDTO user;
    private Long idMessageEnvoye;

    // getters & setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public Long getIdMessageEnvoye() { return idMessageEnvoye; }
    public void setIdMessageEnvoye(Long idMessageEnvoye) { this.idMessageEnvoye = idMessageEnvoye; }

    // DTO interne pour l'utilisateur
    public static class UserDTO {
        private Long id;        // 🔹 important pour le service NumeroDestinataire
        private String username;
        private String role;

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
