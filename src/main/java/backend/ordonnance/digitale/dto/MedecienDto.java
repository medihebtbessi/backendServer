package backend.ordonnance.digitale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder

public record MedecienDto(String nomComplet ,String specialite,String adresse) {
}
