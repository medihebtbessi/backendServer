package backend.ordonnance.digitale.dto;

import backend.ordonnance.digitale.entites.Role;
import backend.ordonnance.digitale.entites.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@AllArgsConstructor
public class InscriptionRequest {
    private String nom;
    private String prenom;
    private String email;
    private int numCin;
    private int telephone;
    private String password;
    private String adresse;
    private Role role;
    private String specialite;
    private MultipartFile cin;
    private MultipartFile cartePro;
}
