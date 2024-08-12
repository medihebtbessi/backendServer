package backend.ordonnance.digitale.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer code;
    String nom;
    String type ;
    String dosage;
    String periodeDuTraitement;
    Boolean lv=false;
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "ordonnance_id")
    Ordonnance ordonnance;

}
