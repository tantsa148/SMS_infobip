package sms.back_end.entity;

import java.io.Serializable;
import java.util.Objects;

public class UsersDetailId implements Serializable {

    private Long idUtilisateur;
    private Long idInfobip;

    public UsersDetailId() {}

    public UsersDetailId(Long idUtilisateur, Long idInfobip) {
        this.idUtilisateur = idUtilisateur;
        this.idInfobip = idInfobip;
    }

    // equals et hashCode obligatoires
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersDetailId)) return false;
        UsersDetailId that = (UsersDetailId) o;
        return Objects.equals(idUtilisateur, that.idUtilisateur) &&
               Objects.equals(idInfobip, that.idInfobip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur, idInfobip);
    }
}
