package backend.ordonnance.digitale.sercives.implementation;

import backend.ordonnance.digitale.dto.InscriptionRequest;
import backend.ordonnance.digitale.dto.MedecienDto;
import backend.ordonnance.digitale.dto.UtilisateurDto;
import backend.ordonnance.digitale.entites.*;

import backend.ordonnance.digitale.repositories.IMedecinRepositorie;
import backend.ordonnance.digitale.repositories.IPharmacienRepositorie;
import backend.ordonnance.digitale.repositories.UtilisateurRepositorie;
import lombok.AllArgsConstructor;

import lombok.Builder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.time.Instant;
import java.util.*;
@Service
@AllArgsConstructor
@Builder
public class UtilisateurService implements UserDetailsService {
    private UtilisateurRepositorie utilisateurRepositorie;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;
    private IMedecinRepositorie medecinRepositorie;
    private IPharmacienRepositorie pharmacienRepositorie;
    @Transactional
    public void inscription(InscriptionRequest inscriptionRequest) throws IOException {
        Utilisateur utilisateur=new Utilisateur();
        utilisateur.setNom(inscriptionRequest.getNom());
        utilisateur.setPrenom(inscriptionRequest.getPrenom());
        utilisateur.setEmail(inscriptionRequest.getEmail());
        utilisateur.setTelephone(inscriptionRequest.getTelephone());
        utilisateur.setAdresse(inscriptionRequest.getAdresse());
        utilisateur.setPassword(inscriptionRequest.getPassword());
        utilisateur.setNumCin(inscriptionRequest.getNumCin());

        if (!utilisateur.getEmail().contains("@")){
            throw new RuntimeException("Votre email invalide");
        }
        if (!utilisateur.getEmail().contains(".")){
            throw new RuntimeException("Votre email invalide");
        }
        Optional<Utilisateur> utilisateurOptional =this.utilisateurRepositorie.findByEmail(utilisateur.getEmail());
        if (utilisateurOptional.isPresent()){
            throw new RuntimeException("Votre email est deja utilisé");
        }
        String mdpCrypte= this.passwordEncoder.encode(utilisateur.getPassword());
        Role roleUtilisateur=new Role();
        roleUtilisateur.setLibelle(inscriptionRequest.getRole().getLibelle());
        utilisateur.setRole(roleUtilisateur);
        if((inscriptionRequest.getCin()!=null && !inscriptionRequest.getCin().isEmpty())||(inscriptionRequest.getCartePro()!=null && !inscriptionRequest.getCartePro().isEmpty())) {
            byte[] cinBytes = inscriptionRequest.getCin().getBytes();
            utilisateur.setCopieCIN(cinBytes);
            byte[] carteProBytes = inscriptionRequest.getCartePro().getBytes();
            utilisateur.setCopieCartePro(carteProBytes);
        }else{
            throw new RuntimeException("insert your picture");
        }
        if (utilisateur.getRole().getLibelle().equals(TypeDeRole.MEDECIN)){
            Medecin medecin=new Medecin();
            medecin.setId(utilisateur.getId());
            medecin.setNom(utilisateur.getNom());
            medecin.setPrenom(utilisateur.getPrenom());
            medecin.setAdresse(utilisateur.getAdresse());
            medecin.setEmail(utilisateur.getEmail());
            medecin.setCopieCIN(utilisateur.getCopieCIN());
            medecin.setCopieCartePro(utilisateur.getCopieCartePro());
            medecin.setRole(roleUtilisateur);
            medecin.setPassword(mdpCrypte);
            medecin.setTelephone(utilisateur.getTelephone());
            medecin.setNumCin(utilisateur.getNumCin());
            medecin.setSpecialite(inscriptionRequest.getSpecialite());
            utilisateur= this.medecinRepositorie.save(medecin);
        }else if (utilisateur.getRole().getLibelle().equals(TypeDeRole.PHARMACIEN)){
            Pharmacien pharmacien=new Pharmacien();
            pharmacien.setId(utilisateur.getId());
            pharmacien.setNom(utilisateur.getNom());
            pharmacien.setPrenom(utilisateur.getPrenom());
            pharmacien.setAdresse(utilisateur.getAdresse());
            pharmacien.setEmail(utilisateur.getEmail());
            pharmacien.setCopieCIN(utilisateur.getCopieCIN());
            pharmacien.setCopieCartePro(utilisateur.getCopieCartePro());
            pharmacien.setRole(roleUtilisateur);
            pharmacien.setTelephone(utilisateur.getTelephone());
            pharmacien.setNumCin(utilisateur.getNumCin());
            pharmacien.setPassword(mdpCrypte);
            utilisateur= this.pharmacienRepositorie.save(pharmacien);
        }else {
            utilisateur.setPassword(mdpCrypte);
            utilisateur= this.utilisateurRepositorie.save(utilisateur);
        }
       this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation= this.validationService.lireEnFonctionDucode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpire())){
            throw new RuntimeException("votre code a expiré");
        }
       Utilisateur utilisateurActiver= this.utilisateurRepositorie.findById(validation.getUtilisateur().getId()).orElseThrow(()->new RuntimeException("utilisateur inconnu"));
        utilisateurActiver.setActif(true);
        utilisateurRepositorie.save(utilisateurActiver);
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepositorie.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Aucun utilisateur correspond a cet identifiant "));

    }

    public void modifierMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur=
                this.loadUserByUsername
                        (parametres.get("email"));
        this.validationService
                .enregistrer(utilisateur);

    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur=this.loadUserByUsername(parametres.get("email"));
        final  Validation validation=validationService.lireEnFonctionDucode(parametres.get("code"));

        if (validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())) {
            String mdpCrypte = this.passwordEncoder.encode(parametres.get("password"));
            utilisateur.setPassword(mdpCrypte);
            this.utilisateurRepositorie.save(utilisateur);
        }
    }

    public List<Utilisateur> listeUtilisateur() {
        final Iterable<Utilisateur> utilisateurs=this.utilisateurRepositorie.findAll();
        List<Utilisateur> utilisateurs1=new ArrayList<>();
        for (Utilisateur utilisateur:utilisateurs){
            utilisateurs1.add(utilisateur);
        }
        return utilisateurs1;
    }

    public List<Utilisateur> findAllMedecin(){
        List<Utilisateur> users = listeUtilisateur();
        return users.stream().filter(utilisateur -> utilisateur.getRole().getLibelle().equals(TypeDeRole.MEDECIN)).toList();
    }


    public TypeDeRole findRole(String email){
        System.out.println(email);
        Utilisateur utilisateur=utilisateurRepositorie.findByEmail(email).orElseThrow(()->new RuntimeException("email not found"));
        return utilisateur.getRole().getLibelle();
    }


    public MedecienDto getInfoMed(){
        Object medecin= SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return MedecienDto.builder()
                .nomComplet(((Medecin)medecin).getNom()+" "+((Medecin) medecin).getPrenom())
                .specialite(((Medecin) medecin).getSpecialite())
                .adresse(((Medecin) medecin).getAdresse())
                .build();
    }

    public UtilisateurDto findUtilisateurInfo(){

        Utilisateur utilisateur= (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UtilisateurDto.builder()
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .numCin(utilisateur.getNumCin())
                .build();
    }


    public void setInfoUtilisateur(Utilisateur utilisateur){
        System.out.println(utilisateur.getTelephone());
        Utilisateur utilisateurFounded= (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        utilisateurFounded.setNom(utilisateur.getNom());
        utilisateurFounded.setPrenom(utilisateur.getPrenom());
        utilisateurFounded.setEmail(utilisateur.getEmail());
        utilisateurFounded.setNumCin(utilisateur.getNumCin());
        utilisateurFounded.setTelephone(utilisateur.getTelephone());
        utilisateurRepositorie.save(utilisateurFounded);
    }



}
