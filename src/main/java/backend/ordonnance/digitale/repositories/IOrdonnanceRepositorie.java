package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Medecin;
import backend.ordonnance.digitale.entites.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Repository
public interface IOrdonnanceRepositorie extends JpaRepository<Ordonnance,Integer> {


    @Modifying
    @Transactional
    @Query("delete Ordonnance o where o.reference=:reference")
    void deleteByReference(String reference);
    @Transactional
    @Query("from Ordonnance o where o.reference=:reference")
    Optional<Ordonnance> findByReference(@Param("reference") String reference);
    @Query("from Ordonnance o where o.nomPatient=:nomPatient")
    List<Ordonnance> getBynomPatient(@Param("nomPatient") String nomPtient);

    List<Ordonnance> findAllByMedecin(Medecin medecin);

    @Query("from Ordonnance o where o.dateConsultation=:dateConsultation")
    List<Ordonnance> findAllByDate(@Param("dateConsultation") Date dateConsultation);
}
