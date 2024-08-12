package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.entites.Pharmacien;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPharmacienRepositorie extends JpaRepository<Pharmacien,Integer> {

}
