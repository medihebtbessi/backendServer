package backend.ordonnance.digitale.sercives.implementation;

import backend.ordonnance.digitale.entites.Medicament;
import backend.ordonnance.digitale.entites.Ordonnance;
import backend.ordonnance.digitale.repositories.IOrdonnanceRepositorie;

import com.lowagie.text.*;
import com.lowagie.text.Font;

import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.FileSystemResource;

import org.springframework.stereotype.Service;

import com.lowagie.text.Image;

import java.awt.*;


import java.io.File;
import java.io.IOException;

import java.util.List;

@Service
public class OrdonnancePdfExporter {
    @Autowired
    private IOrdonnanceRepositorie ordonnanceRepositorie;


    public void export(HttpServletResponse response, Ordonnance ordonnance) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ordonnance.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(25);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Ordonnance Digitale", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        Font fontcontent = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(20);
        font.setColor(Color.BLACK);


        Paragraph p1 = new Paragraph(
                ordonnance.getMedecin().getNom() + " " + ordonnance.getMedecin().getPrenom() + "                                                                     "   + ordonnance.getDateConsultation() +"\n"+
                        ordonnance.getMedecin().getSpecialite() + "                                                                                                         " +
                        ordonnance.getMedecin().getAdresse() + "\n" +
                        "\n                                                                                              Réference : " +
                        ordonnance.getReference()
        );


        document.add(p1);

        Paragraph p2 = new Paragraph(
                ordonnance.getNomPatient() + "\n" +
                        "Poids : " + ordonnance.getPoidsPatient() + "   Age : " + ordonnance.getAge()
        ,fontcontent);
        p2.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p2);

        List<Medicament> medicament = ordonnance.getMedications();
        for (int i = 0; i < medicament.size(); i++) {
            Paragraph p5 = new Paragraph("=> Nom medicament : "+
                    medicament.get(i).getNom() + ", Type : " +
                            medicament.get(i).getType() + ", Dosage : " +
                            medicament.get(i).getDosage() + ", Période du Traitement : " +
                            medicament.get(i).getPeriodeDuTraitement()
            ,fontcontent);

            document.add(p5);
        }
        Paragraph p6=new Paragraph(ordonnance.getDescription(),fontcontent);
        p6.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p6);
        String photoName =  ordonnance.getReference();
        String path = "C:\\Users\\21655\\Desktop\\codeQr\\" + photoName + ".png";


        FileSystemResource file = new FileSystemResource(new File(path));
        Image qrCodeImage = Image.getInstance(file.getFile().getAbsolutePath());
        qrCodeImage.setAlignment(Image.ALIGN_CENTER);

        document.add(qrCodeImage);



        document.close();
    }


    public void exportForPhar(HttpServletResponse response, String reference) throws DocumentException, IOException {
        Ordonnance ordonnance=ordonnanceRepositorie.findByReference(reference).orElseThrow(() -> new RuntimeException("Ordonnance not found"));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ordonnance.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(25);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("Ordonnance Digitale", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p);

        Font fontcontent = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontcontent.setSize(20);
        fontcontent.setColor(Color.BLACK);

        Font fontcontentMed = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontcontentMed.setSize(20);



        Paragraph p1 = new Paragraph(
                ordonnance.getMedecin().getNom() + " " + ordonnance.getMedecin().getPrenom() + "                                                                     "   + ordonnance.getDateConsultation() +"\n"+
                        ordonnance.getMedecin().getSpecialite() + "                                                                                                         " +
                        ordonnance.getMedecin().getAdresse() + "\n" +
                        "\n                                                                                              Réference : " +
                        ordonnance.getReference()
        );


        document.add(p1);

        Paragraph p2 = new Paragraph(
                ordonnance.getNomPatient() + "\n" +
                        "Poids : " + ordonnance.getPoidsPatient() + "   Age : " + ordonnance.getAge()
                ,fontcontent);
        p2.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p2);

        List<Medicament> medicament = ordonnance.getMedications();
        for (int i = 0; i < medicament.size(); i++) {
            if (medicament.get(i).getLv()){
                fontcontentMed.setColor(Color.red);
            }else
                fontcontentMed.setColor(Color.BLACK);

            Paragraph p5 = new Paragraph("=> Nom medicament : "+
                    medicament.get(i).getNom() + ", Type : " +
                    medicament.get(i).getType() + ", Dosage : " +
                    medicament.get(i).getDosage() + ", Période du Traitement : " +
                    medicament.get(i).getPeriodeDuTraitement()
                    ,fontcontentMed);


            document.add(p5);
        }
        Paragraph p6=new Paragraph(ordonnance.getDescription(),fontcontent);
        p6.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(p6);
        String photoName = ordonnance.getReference();
        String path = "C:\\Users\\21655\\Desktop\\codeQr\\" + photoName + ".png";


        FileSystemResource file = new FileSystemResource(new File(path));
        Image qrCodeImage = Image.getInstance(file.getFile().getAbsolutePath());
        qrCodeImage.setAlignment(Image.ALIGN_CENTER);

        document.add(qrCodeImage);



        document.close();
    }


}
