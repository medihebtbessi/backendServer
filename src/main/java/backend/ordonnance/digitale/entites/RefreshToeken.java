package backend.ordonnance.digitale.entites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
@Table(name = "refresh_token")
public class RefreshToeken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    boolean expire;
    String valeur;
    Instant creation;
    Instant expiration;
}
