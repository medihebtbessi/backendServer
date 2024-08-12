package backend.ordonnance.digitale.entites;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ordonnance  {
    @Id
    @Column(name = "Référence",nullable = false)
    String reference;
    @Column(name = "Nom_Prenom")
    String nomCompletMedecin;
    @Column(name = "Spécialite")
    String specialite;
    @Column(name = "adresse_Cabinet")
    String addCabinet;
    @Column(name = "date_Consultation")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    Date dateConsultation;
    @Column(name = "Nom_Patient")
    String nomPatient;
    @Column(name = "Poids_Patient")
    Integer poidsPatient;
    @Column(name = "Age")
    Short age;
    @Column(name = "Description")
    String description;
    @Enumerated(EnumType.STRING)
    TypeOrdonnance typeOrdonnance;
    String emailPatient;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    Medecin medecin;
    @OneToMany(mappedBy = "ordonnance", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Medicament> medications=new ArrayList<>();

    public void removeMedicament(Medicament medicament) {
        this.medications.remove(medicament);
        medicament.setOrdonnance(null);
    }


}
