package backend.ordonnance.digitale.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
public  class Medecin extends Utilisateur{
    @Column(name = "Specialite")
    String specialite;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "medecin")
    List<Ordonnance> ordonnance;
}
