package backend.ordonnance.digitale.repositories;

import backend.ordonnance.digitale.entites.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;
@Repository
public interface JwtRepositorie extends CrudRepository<Jwt,Integer> {
        Optional<Jwt> findByValue(String value);

    Optional<Jwt> findByValueAndDesactiveAndExpire(String value,boolean desactive,boolean expire);
    @Query("From Jwt j where j.expire=:expire AND j.desactive=:desactive AND j.utilisateur.email=:email")
    Optional<Jwt> findUtilisateurValidToken(String email,boolean desactive,boolean expire);


    @Query("From Jwt j where  j.utilisateur.email=:email")
    Stream<Jwt> findUtilisateur(String email);

    @Query("From Jwt j where  j.refreshToken.valeur=:valeur")
    Optional<Jwt> findByRefreshToken(String valeur);

    void  deleteAllByExpireAndDesactive(boolean expire, boolean desactive);
}
