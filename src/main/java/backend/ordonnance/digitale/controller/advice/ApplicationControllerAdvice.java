package backend.ordonnance.digitale.controller.advice;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.MalformedInputException;
import java.security.SignatureException;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class ApplicationControllerAdvice {

    @ExceptionHandler(value = BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public @ResponseBody ProblemDetail badCredentialsException(final BadCredentialsException exception){
        log.error(exception.getMessage(),exception);
        ProblemDetail problemDetail= ProblemDetail.forStatusAndDetail(UNAUTHORIZED,"indentifiant invalid");
        problemDetail.setProperty("erreur","nous n'avons pas pu vous identifier");
        return problemDetail;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(UNAUTHORIZED)
    public Map<String,String> exceptionsHandler(){
        return Map.of("erreur","description");
    }


    @ExceptionHandler(value = {SignatureException.class, MalformedJwtException.class})
    @ResponseStatus(UNAUTHORIZED)
    public @ResponseBody ProblemDetail badCredentialsException(final Exception exception){
        ApplicationControllerAdvice.log.error(exception.getMessage(),exception);
        return ProblemDetail.forStatusAndDetail(UNAUTHORIZED,
                "Token invalide"
        );


    }









}
