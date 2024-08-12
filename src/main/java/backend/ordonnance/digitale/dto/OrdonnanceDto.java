package backend.ordonnance.digitale.dto;


import lombok.Builder;

import java.util.Date;
import java.util.List;
@Builder
public record OrdonnanceDto(String reference,
                            String nomCompletMedecin,
                            String specialite,
                            Date dateConsultation,
                            String nomPatient,
                            Integer poidsPatient,
                            Short age,
                            List<MedicamentDto> medicaments,
                            String description) {
}
