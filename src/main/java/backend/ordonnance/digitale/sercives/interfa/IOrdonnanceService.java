package backend.ordonnance.digitale.sercives.interfa;

import backend.ordonnance.digitale.dto.MedecienDto;
import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.entites.Ordonnance;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface IOrdonnanceService {
    public void addOrdonnance(Ordonnance ordonnance) throws IOException, WriterException, MessagingException;
    public Ordonnance updateOrdonnance(Ordonnance ordonnance) throws IOException;
    public void deleteOrdonnance(String reference);


    public List<OrdonnanceDto> getOrdonnanceByName(String nomPtient);
    public List<OrdonnanceDto> getAllOrdonnance();

    public MedecienDto getInfoMed();
    public List<Ordonnance> getOrdonnanceByDateConsultation(Date date);
}
