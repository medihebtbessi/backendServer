package backend.ordonnance.digitale.sercives.implementation;
import backend.ordonnance.digitale.dto.MedicamentDto;
import backend.ordonnance.digitale.dto.MedicamentLvDto;
import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.dto.OrdonnanceDtoWithLv;
import backend.ordonnance.digitale.entites.ChoixPhar;
import backend.ordonnance.digitale.entites.Medicament;
import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.repositories.IMedicamentRepositorie;
import backend.ordonnance.digitale.repositories.IOrdonnanceRepositorie;
import backend.ordonnance.digitale.repositories.IPharmacienRepositorie;
import backend.ordonnance.digitale.sercives.codeQr.MyQr;
import backend.ordonnance.digitale.sercives.interfa.IPharmacienService;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@AllArgsConstructor
@Builder
@Slf4j
public class PharmacienService implements IPharmacienService {
    @Autowired
    private IPharmacienRepositorie pharmacienRepositorie;
    @Autowired
    private IOrdonnanceRepositorie ordonnanceRepositorie;
    @Autowired
    private IMedicamentRepositorie medicamentRepositorie;
    @Autowired
    private HttpServletResponse response ;

    @Autowired
    private OrdonnancePdfExporter ordonnancePdfExporter;
    @Override
    public String getRefFromCodeQr(MultipartFile file) throws NotFoundException, IOException {
        MyQr myQr = new MyQr();
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        String ref = myQr.readQR(bufferedImage, charset, hashMap);
        if(ref==null){
            throw new RuntimeException("Code Qr not exist");
        }
            return ref;

    }

    @Override
    @Transactional
    public void enregistrerOrdonnance(Ordonnance ordonnance , ChoixPhar choixPhar) throws IOException {

        Ordonnance ordonnanceFounded = this.ordonnanceRepositorie.findByReference(ordonnance.getReference())
                .orElseThrow(() -> new RuntimeException("Ordonnance not found"));


        List<Medicament> medicaments = ordonnance.getMedications();
        List<Medicament> medicamentsFounded = ordonnanceFounded.getMedications();


        for (int i = 0; i < medicaments.size(); i++) {
            Medicament incomingMedicament = medicaments.get(i);
            Medicament existingMedicament = medicamentsFounded.stream()
                    .filter(m -> m.getCode().equals(incomingMedicament.getCode()))
                    .findFirst()
                    .orElse(null);


            if (existingMedicament != null) {
                if (incomingMedicament.getLv() && !existingMedicament.getLv()) {
                    existingMedicament.setLv(true);
                }
            }
        }


        ordonnanceFounded.setMedications(medicamentsFounded);
        this.ordonnanceRepositorie.save(ordonnanceFounded);
        if (choixPhar.equals(ChoixPhar.OUI)){
            String ref=ordonnanceFounded.getReference();
            ordonnancePdfExporter.exportForPhar(response,ref);
        }
    }


    private List<MedicamentLvDto> convertMedicamentToMedicamentDto(List<Medicament> medicament){
        List<MedicamentLvDto>  medicamentDtoList=new ArrayList<>();
        for (Medicament med:medicament){
            MedicamentLvDto medicamentLVDto=new MedicamentLvDto(med.getCode(),med.getNom(),med.getType(),med.getDosage(),med.getPeriodeDuTraitement(),med.getLv());
            medicamentDtoList.add(medicamentLVDto);
        }
        return medicamentDtoList;
    }
    @Override
    public OrdonnanceDtoWithLv getOrdonnanceByRef(String reference) {
        Ordonnance ordonnance = this.ordonnanceRepositorie.findByReference(reference).orElseThrow(()->new RuntimeException("erreur "));
        return OrdonnanceDtoWithLv.builder()
                .reference(ordonnance.getReference())
                .nomCompletMedecin(ordonnance.getNomCompletMedecin())
                .specialite(ordonnance.getSpecialite())
                .dateConsultation(ordonnance.getDateConsultation())
                .nomPatient(ordonnance.getNomPatient())
                .poidsPatient(ordonnance.getPoidsPatient())
                .age(ordonnance.getAge())
                .medicaments(convertMedicamentToMedicamentDto(ordonnance.getMedications()))
                .description(ordonnance.getDescription())
                .build();
    }

    public String getReffinal(String ref){
        Ordonnance ordonnance=ordonnanceRepositorie.findByReference(ref).orElseThrow(()->new RuntimeException("Pas d'ordonnance disponible"));
        if (ordonnance.equals(null)){
            return null;
        }else {
            return ref;
        }
    }


}
