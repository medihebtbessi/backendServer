package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Medecin;
import jakarta.persistence.criteria.From;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IMedecinRepositorie extends JpaRepository<Medecin,Integer> {
    /*@Query("From Utilisateur u join Role r on u.role_id=r.id where r.libelle='MEDECIN'")
    Optional<Object> findMedecinById(int id);*/
}
