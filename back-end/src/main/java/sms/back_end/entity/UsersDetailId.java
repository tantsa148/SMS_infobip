package sms.back_end.entity;

import java.io.Serializable;
import java.util.Objects;

public class UsersDetailId implements Serializable {

    private Long idUtilisateur;
    private Long idNumero;

    public UsersDetailId() {}

    public UsersDetailId(Long idUtilisateur, Long idNumero) {
        this.idUtilisateur = idUtilisateur;
        this.idNumero = idNumero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersDetailId)) return false;
        UsersDetailId that = (UsersDetailId) o;
        return Objects.equals(idUtilisateur, that.idUtilisateur) &&
               Objects.equals(idNumero, that.idNumero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur, idNumero);
    }
}
