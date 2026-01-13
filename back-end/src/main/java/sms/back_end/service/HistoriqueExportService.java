package sms.back_end.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;

import sms.back_end.entity.Historique;

@Service
public class HistoriqueExportService {

    public ByteArrayInputStream exportToCsv(List<Historique> historiques) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        try (CSVWriter csvWriter = new CSVWriter(writer,
                ';',                     // séparateur CSV
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            // 🔹 En-têtes CSV
            String[] header = {
                "ID Envoi",
                "Utilisateur",
                "Expéditeur",
                "Numéro Expéditeur",
                "Numéro Destinataire",
                "Message",
                "Plateforme",
                "Infobip Message ID",
                "Date Envoi"
            };
            csvWriter.writeNext(header);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 🔹 Lignes
            for (Historique h : historiques) {
                String[] row = {
                    String.valueOf(h.getIdEnvoi()),
                    String.valueOf(h.getIdUtilisateur()),
                    h.getExpediteur(),
                    h.getNumeroExpediteur(),
                    h.getNumeroDestinataire(),
                    h.getMessage(),
                    h.getPlateforme(),
                    h.getInfobipMessageId(),
                    h.getDateEnvoi() != null ? h.getDateEnvoi().format(formatter) : ""
                };
                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export CSV", e);
        }
    }
}
