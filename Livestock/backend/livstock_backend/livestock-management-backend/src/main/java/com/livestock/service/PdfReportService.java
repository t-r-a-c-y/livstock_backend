// src/main/java/com/livestock/service/PdfReportService.java
package com.livestock.service;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    public ByteArrayInputStream generateFinancialReport(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal profit) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
        Paragraph title = new Paragraph("Financial Summary Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        // Generation Date
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12);
        Paragraph date = new Paragraph("Generated on: " + LocalDate.now(), dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(40);
        document.add(date);

        // Summary Table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setSpacingBefore(20);

        float[] columnWidths = {2f, 1f};
        table.setWidths(columnWidths);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 14);

        addSummaryRow(table, "Total Income", totalIncome.toString(), labelFont, valueFont, new Color(0, 128, 0));   // Green
        addSummaryRow(table, "Total Expenses", totalExpense.toString(), labelFont, valueFont, new Color(255, 0, 0)); // Red
        addSummaryRow(table, "Net Profit", profit.toString(), labelFont, valueFont, new Color(0, 0, 255));         // Blue

        document.add(table);

        // Profit Highlight
        Font profitFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph profitText = new Paragraph("\nNet " + (profit.compareTo(BigDecimal.ZERO) >= 0 ? "Profit" : "Loss") + ": " + profit.abs() + " KES",
                profitFont);
        profitText.setAlignment(Element.ALIGN_CENTER);
        profitText.setSpacingBefore(40);
        document.add(profitText);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, Color valueColor) {
        // Label cell
        Phrase labelPhrase = new Phrase(label, labelFont);
        PdfPCell labelCell = new PdfPCell(labelPhrase);
        labelCell.setPadding(10);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        // Value cell with colored text
        Phrase valuePhrase = new Phrase("KES " + value, valueFont);
        valuePhrase.setFont(valueFont); // Ensure font is applied
        PdfPCell valueCell = new PdfPCell(valuePhrase);
        valueCell.setPadding(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPhrase(valuePhrase); // Important
        valueCell.setCellEvent(new CellBackgroundEvent(valueColor)); // We'll add this below
        table.addCell(valueCell);
    }




    private static class CellBackgroundEvent implements PdfPCellEvent {
        private Color color;

        public CellBackgroundEvent(Color color) {
            this.color = color;
        }

        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setColorFill(color);
            cb.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            cb.fill();
            cb.restoreState();
        }
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