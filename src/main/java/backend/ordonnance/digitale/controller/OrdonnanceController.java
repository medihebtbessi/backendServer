package backend.ordonnance.digitale.controller;

import backend.ordonnance.digitale.dto.MedecienDto;
import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.sercives.implementation.OrdonnancePdfExporter;
import backend.ordonnance.digitale.sercives.interfa.IOrdonnanceService;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:8090")
//@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class OrdonnanceController {
    @Autowired
    private final IOrdonnanceService ordonnanceService;

    @Autowired
    private final OrdonnancePdfExporter ordonnancePdfExporter;
    @Autowired
    private HttpServletResponse response ;

    @GetMapping(value = "/exporterPdf/{reference}")
    public void getFormPDf(@PathVariable("reference") String reference) throws IOException {
        ordonnancePdfExporter.exportForPhar(response,reference);
    }

    @PostMapping("/add")
    public void addOrdonnance(@RequestBody Ordonnance ordonnance) throws IOException, WriterException, MessagingException {
        ordonnanceService.addOrdonnance(ordonnance);
    }
    @PutMapping("/update")
    public @ResponseBody Ordonnance updateOrdonnance(@RequestBody Ordonnance ordonnance) throws IOException {

        return ordonnanceService.updateOrdonnance(ordonnance);
    }
    @DeleteMapping("/delete/{reference}")
    public void deleteOrdonnance(@PathVariable("reference") String reference) {
        this.ordonnanceService.deleteOrdonnance(reference);
    }



    @GetMapping("/getByName/{nomPatient}")
    public @ResponseBody List<OrdonnanceDto> getOrdonnanceByName(@PathVariable("nomPatient") String nomPatient) {
        return ordonnanceService.getOrdonnanceByName(nomPatient);
    }

    @GetMapping("/All")
    public @ResponseBody List<OrdonnanceDto> getAllOrdonnance(){
        return ordonnanceService.getAllOrdonnance();
    }

    @GetMapping("/getByDate/{date}")
    public @ResponseBody List<Ordonnance> getOrdonnanceByDateConsultation(@PathVariable("date") Date date){
        return ordonnanceService.getOrdonnanceByDateConsultation(date);
    }
    @GetMapping("/getInfoMed")
    public @ResponseBody MedecienDto getInfoMed(){
        return ordonnanceService.getInfoMed();
    }


}
