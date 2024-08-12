package backend.ordonnance.digitale.controller;

import backend.ordonnance.digitale.dto.OrdonnanceDto;
import backend.ordonnance.digitale.dto.OrdonnanceDtoWithLv;
import backend.ordonnance.digitale.entites.ChoixPhar;
import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.sercives.implementation.PharmacienService;
import com.google.zxing.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin("http://localhost:8090")
@RequiredArgsConstructor
//@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class PharmacienController {
    @Autowired
    private PharmacienService pharmacienService;

    @GetMapping( "/pharmacien/getByRef/{reference}")
    public @ResponseBody OrdonnanceDtoWithLv getOrdonnanceByRe(@PathVariable("reference") String reference) {
        System.out.println(reference);
        return this.pharmacienService.getOrdonnanceByRef(reference);
    }

    @PostMapping("/pharmacien/get")
    public @ResponseBody String getOrdonnance(@RequestParam("file") MultipartFile file) throws NotFoundException, IOException
    { return pharmacienService.getRefFromCodeQr(file); }


    @PutMapping("/enregistrer/{choixPhar}")
    public void enregistrerOrdonnance(@RequestBody Ordonnance ordonnance ,@PathVariable("choixPhar") ChoixPhar choixPhar) throws IOException {
         pharmacienService.enregistrerOrdonnance(ordonnance,choixPhar);
    }

    @GetMapping( "/pharmacien/getReffinal/{reference}")
    public @ResponseBody String getRefFinal(@PathVariable("reference") String reference) {

        return this.pharmacienService.getReffinal(reference);
    }


}
