package backend.ordonnance.digitale.dto;

import lombok.Builder;

import java.util.Date;
import java.util.List;
@Builder
public record OrdonnanceDtoWithLv(String reference,
                                  String nomCompletMedecin,
                                  String specialite,
                                  Date dateConsultation,
                                  String nomPatient,
                                  Integer poidsPatient,
                                  Short age,
                                  List<MedicamentLvDto> medicaments,
                                  String description) {
}
