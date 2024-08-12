package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Medecin;
import backend.ordonnance.digitale.entites.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UtilisateurRepositorie extends CrudRepository<Utilisateur,Integer> {
    @Query("From Utilisateur u where u.email=:email ")
    Optional<Utilisateur> findByEmail(String email);




}
