package backend.ordonnance.digitale.sercives.interfa;

import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.dto.OrdonnanceDtoWithLv;
import backend.ordonnance.digitale.entites.ChoixPhar;
import backend.ordonnance.digitale.entites.Ordonnance;
import com.google.zxing.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPharmacienService {
    //OrdonnanceDto getOrdonnanceBycodeQr(MultipartFile multipartFile) throws NotFoundException, IOException;
    OrdonnanceDtoWithLv getOrdonnanceByRef(String reference);
    void enregistrerOrdonnance(Ordonnance ordonnance, ChoixPhar choixPhar) throws IOException;
    public String getRefFromCodeQr(MultipartFile file) throws NotFoundException, IOException;

}
