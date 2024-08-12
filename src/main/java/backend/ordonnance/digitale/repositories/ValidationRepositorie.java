package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Validation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
@Repository
public interface ValidationRepositorie extends CrudRepository<Validation,Integer> {

    Optional<Validation> findByCode(String code);
    void  deleteAllByexpireBefore(Instant now);
}
