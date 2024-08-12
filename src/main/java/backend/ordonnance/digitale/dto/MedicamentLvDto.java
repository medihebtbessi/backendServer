package backend.ordonnance.digitale.dto;

import lombok.Builder;


public record MedicamentLvDto(Integer code, String nom, String type, String dosage, String periodeDuTraitement,boolean lv) {

}
