package backend.ordonnance.digitale.sercives.implementation;

import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.entites.Validation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.nio.file.FileSystem;

@Service
@AllArgsConstructor
public class NotificationService {

    @Autowired
    JavaMailSender javaMailSender;

    public void envoyer(Validation validation){
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        mailMessage.setFrom("ihebtbessi37@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("votre code d'activation ");
        String text= String.format("Bonjour %s, \n Votre code d'activation est %s; A bientot",validation.getUtilisateur().getNom(),
                validation.getCode());
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }


}
