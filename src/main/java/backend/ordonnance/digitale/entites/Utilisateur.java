package backend.ordonnance.digitale.entites;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Blob;
import java.util.Collection;
import java.util.Collections;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "Nom")
    private String nom;
    @Column(name = "Prenom")
    private String prenom;
    @Column(name = "Email")
    private String email;

    private int numCin;
    @Column(name = "Telephone")
    private int telephone;
    @Column(name = "Mot_de_passe")
    private String password;
    @Column(name = "Adresse")
    private String adresse;
    @Lob
    private byte[] copieCIN;
    @Lob
    private byte[] copieCartePro;
    @Column(name = "Status")
    private boolean actif=false;
    @OneToOne(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    private Role role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+this.role.getLibelle()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.nom+" "+this.prenom;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.actif;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.actif;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.actif;
    }

    @Override
    public boolean isEnabled() {
        return this.actif;
    }



}
