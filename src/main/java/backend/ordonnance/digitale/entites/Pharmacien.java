package backend.ordonnance.digitale.entites;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE )
public class Pharmacien extends Utilisateur{


}
