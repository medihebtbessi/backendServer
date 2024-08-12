package backend.ordonnance.digitale.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
@Table(name = "jwt")
public class Jwt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String value;
    boolean desactive;
    boolean expire;
    @OneToOne(cascade ={CascadeType.PERSIST,CascadeType.REMOVE} )
    RefreshToeken refreshToken;
    @ManyToOne(cascade ={CascadeType.DETACH,CascadeType.MERGE})
    @JoinColumn(name = "utilisateur_id")
    Utilisateur utilisateur;


}
