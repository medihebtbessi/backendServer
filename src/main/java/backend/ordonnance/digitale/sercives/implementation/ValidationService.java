package backend.ordonnance.digitale.sercives.implementation;

import backend.ordonnance.digitale.entites.Utilisateur;
import backend.ordonnance.digitale.entites.Validation;
import backend.ordonnance.digitale.repositories.ValidationRepositorie;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Random;

import static java.time.temporal.ChronoUnit.MINUTES;
@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class ValidationService {
    private ValidationRepositorie validationRepositorie;
    private NotificationService notificationService;
    public void enregistrer(Utilisateur utilisateur){
        Validation validation=new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation=Instant.now();
        validation.setCreation(creation);
        Instant expiration =creation.plus(10,MINUTES);
        validation.setExpire(expiration);
        Random random=new Random();
        int randomInteger= random.nextInt(999999);
        String code=String.format("%06d",randomInteger);
        validation.setCode(code);
        this.validationRepositorie.save(validation);
        this.notificationService.envoyer(validation);
    }

    public Validation lireEnFonctionDucode(String code){
         return    this.validationRepositorie.findByCode(code).orElseThrow(()->new RuntimeException("Votre code est valide"));
    }
    @Scheduled(cron = "0 */1 * * * *")
    public void nettoyerTable(){
        this.validationRepositorie.deleteAllByexpireBefore(Instant.now());
    }
}
