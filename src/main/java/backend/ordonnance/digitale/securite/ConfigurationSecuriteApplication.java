package backend.ordonnance.digitale.securite;
import backend.ordonnance.digitale.entites.Role;
import backend.ordonnance.digitale.entites.TypeDeRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class ConfigurationSecuriteApplication  {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtFilter jwtFilter;


    public ConfigurationSecuriteApplication(JwtFilter jwtFilter,BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtFilter = jwtFilter;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorize->authorize.requestMatchers(POST,"/inscription").permitAll()
                                .requestMatchers(POST,"/activation").permitAll()
                                .requestMatchers(POST,"/nouveaumotdepasse").permitAll()
                                .requestMatchers(POST,"/refresh-token").permitAll()
                                .requestMatchers(GET,"/getUserRole/{email1}").permitAll()
                                .requestMatchers(GET,"/getInfoMed").hasRole("MEDECIN")
                                .requestMatchers(GET,"/getInfoforProfil").hasRole("MEDECIN")

                                .requestMatchers(GET,"/exporterPdf/{reference}").hasRole("MEDECIN")

                                .requestMatchers(POST,"/pharmacien/get").hasRole("PHARMACIEN")
                                .requestMatchers(GET,"/pharmacien/getReffinal/{reference}").hasRole("PHARMACIEN")
                                .requestMatchers(PUT,"/enregistrer/{choixPhar}").hasRole("PHARMACIEN")
                                .requestMatchers(PUT,"setInfoUtili").hasRole("MEDECIN")
                                .requestMatchers(POST,"/connexion").permitAll()
                                .requestMatchers(POST,"/modifierMotDePasse").permitAll()
                                .requestMatchers(POST,"/ordonnance/add").hasRole("MEDECIN")
                                .requestMatchers(GET,"/ordonnance/get").hasRole("ADMINISTRATEUR")
                                .requestMatchers(DELETE,"/ordonnance/delete").hasRole("ADMINISTRATEUR")
                                .requestMatchers(POST,"/add").hasRole("MEDECIN")
                                .requestMatchers(GET,"/pharmacien/getByRef/{reference}").hasRole("PHARMACIEN")
                                .requestMatchers(GET,"/ordonnance/getInfoMed").hasRole("MEDECIN")
                                .requestMatchers(GET,"/ordonnance/getByName/{nomPatient}").hasRole("MEDECIN")
                                .requestMatchers(GET,"/ordonnance/All").hasRole("MEDECIN")
                                .requestMatchers(GET,"/ordonnance/getByDate/{date}").hasRole("MEDECIN")
                                .requestMatchers(PUT,"/update").hasRole("MEDECIN")
                                .requestMatchers(DELETE,"/delete/{reference}").hasRole("MEDECIN")
                                .anyRequest().authenticated()


                )
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }




    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return daoAuthenticationProvider;
    }
}
