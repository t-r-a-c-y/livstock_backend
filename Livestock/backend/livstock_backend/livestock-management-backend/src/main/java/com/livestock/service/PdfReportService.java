// src/main/java/com/livestock/service/PdfReportService.java
package com.livestock.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfReportService {

    public ByteArrayInputStream generateAnimalReport(List<Object[]> animals) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate()); // Landscape for more columns
        PdfWriter.getInstance(document, out);

        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Animal Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(8); // 8 columns
        table.setWidthPercentage(100);
        table.setWidths(new int[]{15, 20, 15, 15, 10, 15, 10, 15});

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        addTableHeader(table, "Tag ID", headFont);
        addTableHeader(table, "Type", headFont);
        addTableHeader(table, "Breed", headFont);
        addTableHeader(table, "Gender", headFont);
        addTableHeader(table, "DOB", headFont);
        addTableHeader(table, "Owner", headFont);
        addTableHeader(table, "Status", headFont);
        addTableHeader(table, "Milk (L)", headFont);

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        for (Object[] animal : animals) {
            addCell(table, String.valueOf(animal[0]), cellFont); // tagId
            addCell(table, String.valueOf(animal[1]), cellFont); // type
            addCell(table, String.valueOf(animal[2]), cellFont); // breed
            addCell(table, String.valueOf(animal[3]), cellFont); // gender
            addCell(table, animal[4].toString(), cellFont);     // dateOfBirth
            addCell(table, String.valueOf(animal[5]), cellFont); // owner name
            addCell(table, String.valueOf(animal[6]), cellFont); // status
            addCell(table, animal[7] != null ? animal[7].toString() : "0", cellFont); // milk
        }

        document.add(table);

        // Footer
        Paragraph footer = new Paragraph("Generated on: " + new java.util.Date(),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(new Color(0, 102, 204));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8);
        header.setPhrase(new Phrase(headerTitle, font));
        header.setBorderColor(new Color(255, 255, 255));
        table.addCell(header);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}