package backend.ordonnance.digitale.dto;

import java.util.Date;

public record MedicamentDto(Integer code, String nom, String type, String dosage, String periodeDuTraitement) {
}
