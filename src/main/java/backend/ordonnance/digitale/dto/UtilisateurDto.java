package backend.ordonnance.digitale.dto;

import lombok.Builder;

@Builder
public record UtilisateurDto(String nom,String prenom,String email,int telephone,int numCin) {
}
