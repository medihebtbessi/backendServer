package backend.ordonnance.digitale.securite;

import backend.ordonnance.digitale.entites.Jwt;
import backend.ordonnance.digitale.sercives.implementation.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
@Service
public class JwtFilter extends OncePerRequestFilter {
    private UtilisateurService utilisateurService;
    private JwtService jwtService;
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtFilter(UtilisateurService utilisateurService,JwtService jwtService,HandlerExceptionResolver handlerExceptionResolver) {
        this.utilisateurService = utilisateurService;
        this.jwtService=jwtService;
        this.handlerExceptionResolver=handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token= null;
        Jwt tokenDansLaBDD = null;
        String email=null;
        boolean isTokenExpired=true;
        try {
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer")) {
                token = authorization.substring(7);
                tokenDansLaBDD = this.jwtService.findByValue(token);
                isTokenExpired = jwtService.isTokenExpired(token);
                email = jwtService.extractEmail(token);

            }
            if (!isTokenExpired && email != null && tokenDansLaBDD.getUtilisateur().getEmail().equals(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = utilisateurService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
            filterChain.doFilter(request, response);
        }catch (final Exception exception){
            this.handlerExceptionResolver.resolveException(request,response,null,exception);
        }

    }
}
