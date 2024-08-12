package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Medicament;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IMedicamentRepositorie extends CrudRepository<Medicament,Integer> {
    @Query("delete from Medicament m where m.code=:code")
    @Transactional
    @Modifying
    void deleteByCode(Integer code);
}
