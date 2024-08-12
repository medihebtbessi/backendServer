package backend.ordonnance.digitale.sercives.implementation;
import backend.ordonnance.digitale.dto.MedecienDto;
import backend.ordonnance.digitale.dto.MedicamentDto;
import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.entites.*;
import backend.ordonnance.digitale.repositories.IMedecinRepositorie;
import backend.ordonnance.digitale.repositories.IMedicamentRepositorie;
import backend.ordonnance.digitale.repositories.IOrdonnanceRepositorie;
import backend.ordonnance.digitale.repositories.UtilisateurRepositorie;
import backend.ordonnance.digitale.sercives.codeQr.MyQr;
import backend.ordonnance.digitale.sercives.interfa.IOrdonnanceService;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@AllArgsConstructor
@Builder
public class OrdonnanceService implements IOrdonnanceService {
    @Autowired
    private final IMedecinRepositorie medecinRepositorie;
    @Autowired
    private final IOrdonnanceRepositorie ordonnanceRepositorie;
    @Autowired
    private final OrdonnancePdfExporter ordonnancePdfExporter;
    @Autowired
    private  final IMedicamentRepositorie medicamentRepositorie;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private HttpServletResponse response ;

    @Override
    public void addOrdonnance(Ordonnance ordonnance) throws IOException, WriterException, MessagingException {
        Object medecin= SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (medecin instanceof Medecin) {
            ordonnance.setMedecin((Medecin) medecin);
            if (ordonnance.getReference() == null) {
                ordonnance.setReference(genererReference(ordonnance.getMedecin().getNom(), ordonnance.getMedecin().getPrenom(), ordonnance.getNomPatient()));
                ordonnance.setNomCompletMedecin(((Medecin) medecin).getNom() + " " + ((Medecin) medecin).getPrenom());
                ordonnance.setAddCabinet(((Medecin) medecin).getAdresse());
                ordonnance.setDateConsultation(Date.from(Instant.now()));
                ordonnance.setSpecialite(((Medecin) medecin).getSpecialite());
                ordonnance.setAddCabinet(((Medecin) medecin).getAdresse());
                if (ordonnance.getTypeOrdonnance().equals(TypeOrdonnance.PDF)){
                    ordonnance.setEmailPatient("il a exporter en format PDF");
                }
                addMedicament( ordonnance);

                ordonnanceRepositorie.save(ordonnance);

            }
            Map<EncodeHintType, ErrorCorrectionLevel> hashMap
                    = new HashMap<EncodeHintType,
                    ErrorCorrectionLevel>();
            hashMap.put(EncodeHintType.ERROR_CORRECTION,
                    ErrorCorrectionLevel.L);
            String photoName = ordonnance.getReference();
            String path = "C:\\Users\\21655\\Desktop\\codeQr\\" + photoName + ".png";

            MyQr qr = new MyQr();
            qr.createQR(ordonnance.getReference(), path, "UTF-8", hashMap, 250, 250);
            if (ordonnance.getTypeOrdonnance().equals(TypeOrdonnance.PAREMAIL)) {
                if (!ordonnance.getEmailPatient().contains("@")||!ordonnance.getEmailPatient().contains(".")){
                    throw new RuntimeException("Email patient invalide");
                }
                String emailPatient = ordonnance.getEmailPatient();
                envoyerCodeQr(path, ordonnance, emailPatient);
            } else if (ordonnance.getTypeOrdonnance().equals(TypeOrdonnance.PDF)) {

                ordonnancePdfExporter.export(response, ordonnance);
            }
        }
    }
    @Override
    public Ordonnance updateOrdonnance(Ordonnance ordonnance) throws IOException {
        Object medecin = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (((Medecin) medecin).getRole().getLibelle().equals(TypeDeRole.MEDECIN)) {
            Ordonnance existingOrdonnance = ordonnanceRepositorie.findByReference(ordonnance.getReference())
                    .orElseThrow(() -> new RuntimeException("Ordonnance not found"));

            existingOrdonnance.setNomPatient(ordonnance.getNomPatient());
            existingOrdonnance.setAge(ordonnance.getAge());
            existingOrdonnance.setPoidsPatient(ordonnance.getPoidsPatient());
            existingOrdonnance.setDescription(ordonnance.getDescription());
            existingOrdonnance.getMedications().clear();
            existingOrdonnance.getMedications().addAll(ordonnance.getMedications());
            addMedicament(existingOrdonnance);

            return ordonnanceRepositorie.save(existingOrdonnance);
        } else {
            throw new RuntimeException("Unauthorized access");
        }
    }


    @Override
    public MedecienDto getInfoMed(){
        Object medecin= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       if (((Utilisateur)medecin).getRole().getLibelle().equals(TypeDeRole.MEDECIN)){
        return MedecienDto.builder()
                .nomComplet(((Medecin)medecin).getNom()+" "+((Medecin) medecin).getPrenom())
                .specialite(((Medecin) medecin).getSpecialite())
                .adresse(((Medecin) medecin).getAdresse())
                .build();}
       else
           throw new RuntimeException("probleme");
    }

    @Override
    public void deleteOrdonnance(String reference) {
        Ordonnance ordonnance = ordonnanceRepositorie.findByReference(reference)
                .orElseThrow(() -> new EntityNotFoundException("Ordonnance not found"));
            OrdonnanceDto ordonnanceDto=new OrdonnanceDto(ordonnance.getReference(),ordonnance.getNomCompletMedecin(),ordonnance.getSpecialite(),ordonnance.getDateConsultation(),ordonnance.getNomPatient(),ordonnance.getPoidsPatient(),ordonnance.getAge(),convertMedicamentToMedicamentDto(ordonnance.getMedications()),ordonnance.getDescription());
        List<Integer> codes =ordonnanceDto.medicaments().stream().map(MedicamentDto::code).toList();
        for (Integer code : codes) {
            this.medicamentRepositorie.deleteByCode(code);
        }
        this.ordonnanceRepositorie.deleteByReference(ordonnance.getReference());
    }


    @Override
    public List<OrdonnanceDto> getOrdonnanceByName(String nomPtient) {
        return ordonnanceRepositorie.getBynomPatient(nomPtient).stream().map(ordonnance -> new OrdonnanceDto(
                ordonnance.getReference(),
                ordonnance.getNomCompletMedecin(),
                ordonnance.getSpecialite(),
                ordonnance.getDateConsultation(),
                ordonnance.getNomPatient(),
                ordonnance.getPoidsPatient(),
                ordonnance.getAge(),
               convertMedicamentToMedicamentDto(ordonnance.getMedications()),
                ordonnance.getDescription())
        ).toList();

    }
    @Override
    public List<Ordonnance> getOrdonnanceByDateConsultation(Date date){
        try {
            return this.ordonnanceRepositorie.findAllByDate(date);
        }catch (Exception e){
            throw new RuntimeException("famech consultation enhar atheka" );
        }
    }
    @Override
    @Transactional
    public List<OrdonnanceDto> getAllOrdonnance(){
        Object medecinConnect= SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (medecinConnect instanceof Medecin) {
            Medecin medecin = medecinRepositorie.findById(((Medecin) medecinConnect).getId()).orElseThrow(() -> new RuntimeException("Medecin not found"));
            return  ordonnanceRepositorie.findAllByMedecin(medecin).stream()
                    .map(ordonnance->new OrdonnanceDto(ordonnance.getReference(),
                            ordonnance.getNomCompletMedecin(),
                            ordonnance.getSpecialite(),
                            ordonnance.getDateConsultation(),
                            ordonnance.getNomPatient(),
                            ordonnance.getPoidsPatient(),
                            ordonnance.getAge(),
                            convertMedicamentToMedicamentDto(ordonnance.getMedications()),
                            ordonnance.getDescription()
                            )).toList();
        }
        return new ArrayList<>();
    }

/////////////// lezem folder wa7adhom //////////////////////////////////////


    private String genererReference(String nom, String prenom, String nomPatient) {
        String s = Instant.now().getNano()+"" + nomPatient.charAt(0);
        String ref=nom.charAt(0)+""+prenom.charAt(0)+"" +s;
        return ref.toUpperCase();
    }

    private void addMedicament(Ordonnance ordonnance){
        for (Medicament medicament:ordonnance.getMedications()){
            medicament.setOrdonnance(ordonnance);
        }



    }

    private List<MedicamentDto> convertMedicamentToMedicamentDto(List<Medicament> medicament){
        List<MedicamentDto>  medicamentDtoList=new ArrayList<>();
        for (Medicament med:medicament){
            MedicamentDto medicamentDto=new MedicamentDto(med.getCode(),med.getNom(),med.getType(),med.getDosage(),med.getPeriodeDuTraitement());
            medicamentDtoList.add(medicamentDto);
        }
       return medicamentDtoList;
    }

    private void envoyerCodeQr(String path, Ordonnance ordonnance,String to) throws MessagingException {
        MimeMessage message=javaMailSender.createMimeMessage();
        MimeMessageHelper helper =new MimeMessageHelper(message,true);
        helper.setFrom(ordonnance.getMedecin().getEmail());
        helper.setTo(to);
        helper.setSubject("code QR :");
        helper.setText(String.format("Bonjour %s, Votre reference est :%s;\n A bientot",ordonnance.getNomPatient(),
                ordonnance.getReference()));
        FileSystemResource file=new FileSystemResource(new File(path));
        helper.addAttachment("Photo.png",file,"image/png");
        javaMailSender.send(message);
    }



}
