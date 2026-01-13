package sms.back_end.service;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

@Service
public class HistoriquePdfExportService {

    // Couleurs
    private static final BaseColor HEADER_BG = new BaseColor(41, 128, 185);
    private static final BaseColor LABEL_COLOR = new BaseColor(52, 73, 94);
    private static final BaseColor BORDER_COLOR = new BaseColor(189, 195, 199);

    public byte[] exportSmsDetailToPdf(JsonNode root) {

        if (root == null || !root.has("results") || !root.get("results").has("result")) {
            throw new RuntimeException("Structure JSON invalide pour le PDF");
        }

        JsonNode sms = root.get("results").get("result");

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // En-tête avec fond coloré
            addHeader(document);
            document.add(new Paragraph(" "));

            // Informations générales dans un tableau
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(10f);

            addTableRow(infoTable, "Message ID", sms.path("messageId").asText(""));
            addTableRow(infoTable, "Destinataire", sms.path("to").asText(""));
            addTableRow(infoTable, "Expéditeur", sms.path("from").asText(""));
            addTableRow(infoTable, "Date d'envoi", formatDate(sms.path("sentAt").asText()));
            addTableRow(infoTable, "Date de livraison", formatDate(sms.path("doneAt").asText()));
            addTableRow(infoTable, "Nombre de SMS", sms.path("smsCount").asText(""));
            addTableRow(infoTable, "MCC / MNC", sms.path("mccMnc").asText(""));

            // Prix
            if (sms.has("price")) {
                JsonNode price = sms.get("price");
                String priceText = price.path("pricePerMessage").asText("") + " " +
                                 price.path("currency").asText("");
                addTableRow(infoTable, "Prix", priceText);
            }

            // Statut avec couleur
            if (sms.has("status")) {
                JsonNode status = sms.get("status");
                String statusText = status.path("name").asText("") +
                                  " - " + status.path("description").asText("");
                addTableRowWithColor(infoTable, "Statut", statusText, getStatusColor(status.path("name").asText()));
            }

            document.add(infoTable);

            // Message dans une boîte
            addMessageBox(document, sms.path("text").asText(""));

            // Pied de page
            addFooter(document);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    // ------------------ Méthodes de mise en forme ------------------

    private void addHeader(Document document) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(HEADER_BG);
        headerCell.setPadding(15);
        headerCell.setBorder(Rectangle.NO_BORDER);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE);
        Paragraph title = new Paragraph("DÉTAIL DU SMS", headerFont);
        title.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(title);

        headerTable.addCell(headerCell);
        document.add(headerTable);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, LABEL_COLOR);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        // Cellule Label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(236, 240, 241));
        labelCell.setPadding(8);
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        // Cellule Value
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private void addTableRowWithColor(PdfPTable table, String label, String value, BaseColor valueColor) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, LABEL_COLOR);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, valueColor);

        // Cellule Label
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(236, 240, 241));
        labelCell.setPadding(8);
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        // Cellule Value
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private void addMessageBox(Document document, String message) throws DocumentException {
        document.add(new Paragraph(" "));

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, LABEL_COLOR);
        Paragraph messageLabel = new Paragraph("Contenu du message :", labelFont);
        document.add(messageLabel);

        document.add(new Paragraph(" "));

        // Boîte pour le message
        PdfPTable messageTable = new PdfPTable(1);
        messageTable.setWidthPercentage(100);

        Font messageFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        PdfPCell messageCell = new PdfPCell(new Phrase(message, messageFont));
        messageCell.setPadding(15);
        messageCell.setBackgroundColor(new BaseColor(250, 250, 250));
        messageCell.setBorderColor(BORDER_COLOR);
        messageCell.setMinimumHeight(80);

        messageTable.addCell(messageCell);
        document.add(messageTable);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph(
            "Document généré le " + formatDate(OffsetDateTime.now().toString()),
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    private BaseColor getStatusColor(String status) {
        if (status == null) return BaseColor.BLACK;
        
        switch (status.toUpperCase()) {
            case "DELIVERED":
            case "LIVRÉ":
                return new BaseColor(39, 174, 96); // Vert
            case "PENDING":
            case "EN ATTENTE":
                return new BaseColor(243, 156, 18); // Orange
            case "FAILED":
            case "ÉCHOUÉ":
                return new BaseColor(231, 76, 60); // Rouge
            default:
                return BaseColor.BLACK;
        }
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) {
            return "Non disponible";
        }

        try {
            DateTimeFormatter parser =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            OffsetDateTime odt = OffsetDateTime.parse(isoDate, parser)
                    .withOffsetSameInstant(ZoneOffset.ofHours(3)); // Madagascar

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm", Locale.FRENCH);

            return odt.format(formatter);

        } catch (Exception e) {
            return isoDate;
        }
    }
}