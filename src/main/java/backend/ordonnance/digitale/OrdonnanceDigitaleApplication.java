package backend.ordonnance.digitale;

import backend.ordonnance.digitale.entites.Role;
import backend.ordonnance.digitale.entites.TypeDeRole;
import backend.ordonnance.digitale.entites.Utilisateur;
import backend.ordonnance.digitale.repositories.UtilisateurRepositorie;
import lombok.Builder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@Builder
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class OrdonnanceDigitaleApplication implements CommandLineRunner {
	UtilisateurRepositorie utilisateurRepositorie;
	PasswordEncoder passwordEncoder;
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(OrdonnanceDigitaleApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Utilisateur admin=Utilisateur.builder()
				.actif(true)
				.nom("admin")
				.prenom("admin")
				.password(passwordEncoder.encode("admin"))
				.email("admin@gmail.com")
				.adresse("pas d'adresse")
				.telephone(55801940)
				.numCin(12345678)
				.role(Role.builder()
						.libelle(TypeDeRole.ADMINISTRATEUR)
						.build())
				.build();
		admin=this.utilisateurRepositorie.findByEmail("admin@gmail.com").orElse(admin);
		this.utilisateurRepositorie.save(admin);
	}
	@Bean
	public ApplicationRunner applicationRunner() {
		return args -> {
			jdbcTemplate.execute("ALTER TABLE utilisateur MODIFY copiecin LONGBLOB;");
			jdbcTemplate.execute("ALTER TABLE utilisateur MODIFY copie_carte_pro LONGBLOB;");
		};
	}
}
