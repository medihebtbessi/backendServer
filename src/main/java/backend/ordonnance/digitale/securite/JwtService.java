package backend.ordonnance.digitale.securite;

import backend.ordonnance.digitale.entites.Jwt;
import backend.ordonnance.digitale.entites.RefreshToeken;
import backend.ordonnance.digitale.entites.Utilisateur;
import backend.ordonnance.digitale.repositories.JwtRepositorie;
import backend.ordonnance.digitale.sercives.implementation.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class JwtService {
    public static final String REFRESH = "refresh";
    public static final String TOKEN_INVALIDE = "Token invalide";
    private final String ENCRIPTION_KEY="7X3ecWsAdWqsMqKggJO0Js1Lk7lx6B34njqsnjdlef4f455ef4efnejndeldfkdknkdfl";
    private UtilisateurService utilisateurService;
    private JwtRepositorie jwtRepositorie;
    public Map<String,String> generate(String email){
        Utilisateur utilisateur=this.utilisateurService.loadUserByUsername(email);
        disableTokens(utilisateur);
        Map<String, String> jwtMap = new HashMap<>(this.generateJwt(utilisateur));
        RefreshToeken refreshToeken=RefreshToeken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(30*60*1000*24))
                .build();
        final Jwt jwt = Jwt
                .builder()
                .value(jwtMap.get("bearer"))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToeken)
                .build();
        this.jwtRepositorie.save(jwt);
        jwtMap.put(REFRESH, refreshToeken.getValeur());
        return jwtMap;
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime =currentTime+60*1000*30*24 ;
        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                "email", utilisateur.getEmail(),
                Claims.EXPIRATION,new Date(expirationTime),
                Claims.SUBJECT,utilisateur.getEmail()
        );

        final String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer",bearer);
    }
    private Key getKey(){
       final byte[] decoder = Decoders.BASE64.decode(ENCRIPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public String extractEmail(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate=this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    private <T> T getClaim(String token, Function<Claims,T> function) {
        Claims claims=getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Jwt findByValue(String value) {
       return this.jwtRepositorie.findByValue(value).orElseThrow(()->new RuntimeException("Token inconnu"));
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt= this.jwtRepositorie.findUtilisateurValidToken
                        (utilisateur.getEmail()
                        ,false
                        ,false)
                .orElseThrow(()->new RuntimeException(TOKEN_INVALIDE));
        jwt.setExpire(true);
        jwt.setDesactive(true);
        this.jwtRepositorie.save(jwt);
    }

    public Jwt toeknByValue(String value){
        return this.jwtRepositorie.findByValueAndDesactiveAndExpire(value,false,false).orElseThrow(()->new RuntimeException("token invalide ou inconnu"));
    }
    private void disableTokens(Utilisateur utilisateur){
        final List<Jwt> jwtList =this.jwtRepositorie.findUtilisateur(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).collect(Collectors.toList());
        this.jwtRepositorie.saveAll(jwtList);
    }

    @Scheduled(cron = "@daily")
    public void  removeUseLessJwt(){
        this.jwtRepositorie.deleteAllByExpireAndDesactive(true,true);
    }


    public Map<String,String> refreshToken(Map<String, String> refreshTokenRequest) {
       final Jwt jwt= this.jwtRepositorie.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(()->new RuntimeException("token invalide ou inconnu"));
       if (jwt.getRefreshToken().isExpire()||jwt.getRefreshToken().getExpiration().isBefore(Instant.now())){
           throw new RuntimeException(TOKEN_INVALIDE);
       }
       this.disableTokens(jwt.getUtilisateur());
     return this.generate(jwt.getUtilisateur().getEmail());
    }
}
