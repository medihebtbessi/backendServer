package backend.ordonnance.digitale.controller;

import backend.ordonnance.digitale.dto.AuthentificationDTO;
import backend.ordonnance.digitale.dto.InscriptionRequest;
import backend.ordonnance.digitale.dto.UtilisateurDto;
import backend.ordonnance.digitale.entites.Role;
import backend.ordonnance.digitale.entites.TypeDeRole;
import backend.ordonnance.digitale.entites.Utilisateur;
import backend.ordonnance.digitale.securite.JwtService;
import backend.ordonnance.digitale.sercives.implementation.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin("http://localhost:8090")
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private JwtService jwtService;
    @PostMapping(path = "inscription",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void inscription(@ModelAttribute InscriptionRequest inscriptionRequest) throws IOException {

        this.utilisateurService.inscription(inscriptionRequest);
    }
    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String,String> activation){
        this.utilisateurService.activation(activation);
    }

    @PostMapping(path = "refresh-token")
    public @ResponseBody Map<String,String> refreshToken(@RequestBody Map<String,String> refreshTokenRequest){
        return this.jwtService.refreshToken(refreshTokenRequest);
    }


    @PostMapping(path = "modifierMotDePasse")
    public void modifierMotDePasse(@RequestBody Map<String,String> activation){
        this.utilisateurService.modifierMotDePasse(activation);
    }

    @PostMapping(path = "nouveaumotdepasse")
    public void nouveauMotDePasse(@RequestBody Map<String,String> activation){
        this.utilisateurService.nouveauMotDePasse(activation);
    }

    @PostMapping(path = "deconnexion")
    public void deconnexion(){
        this.jwtService.deconnexion();
    }

    @PostMapping(path = "connexion")
    public Map<String,String> connexion(@RequestBody AuthentificationDTO authentificationDTO){
        final   Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.email1(), authentificationDTO.password1())
        );
      if (authenticate.isAuthenticated()){
         return this.jwtService.generate(authentificationDTO.email1());
      }
        return null;
    }

    @GetMapping( path = "/getUserRole/{email1}")
    public @ResponseBody TypeDeRole getRoleByEmail(@PathVariable("email1") String email1){
        System.out.println(  email1);
        return utilisateurService.findRole(email1);
    }


    @GetMapping(path = "/getInfoforProfil")
    public UtilisateurDto findUtilisateurInfo(){

        return utilisateurService.findUtilisateurInfo();
    }





    //////////////////////a rempl/////////////////

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATEUR")
    public List<Utilisateur> liste(){
        return this.utilisateurService.listeUtilisateur();
    }


    @PutMapping(path = "setInfoUtili")
    public void setInfoUtilisateur(@RequestBody Utilisateur utilisateur){
        utilisateurService.setInfoUtilisateur(utilisateur);
    }

}
