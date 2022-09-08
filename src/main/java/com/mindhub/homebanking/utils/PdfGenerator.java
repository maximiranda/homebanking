package com.mindhub.homebanking.utils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Header;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.models.Transaction;

import javax.servlet.http.HttpServletResponse;

public class PdfGenerator {
    public static void getPdf(HttpServletResponse response, List<Transaction> transactions) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Header header = new Header("maxi", "hola como estas");
            PdfPTable pdfPTable = new PdfPTable(4);
            PdfPCell pdfPCell1 = new PdfPCell(new Paragraph("Descripcion"));
            PdfPCell pdfPCell2 = new PdfPCell(new Paragraph("Fecha"));
            PdfPCell pdfPCell3 = new PdfPCell(new Paragraph("Tipo"));
            PdfPCell pdfPCell4 = new PdfPCell(new Paragraph("Monto"));
            pdfPTable.addCell(pdfPCell1);
            pdfPTable.addCell(pdfPCell2);
            pdfPTable.addCell(pdfPCell3);
            pdfPTable.addCell(pdfPCell4);
            transactions.forEach(transaction -> {

                PdfPCell pdfPCell5 = new PdfPCell(new Paragraph(transaction.getDescription()));
                PdfPCell pdfPCell6 = new PdfPCell(new Paragraph(String.valueOf(transaction.getDate())));
                PdfPCell pdfPCell7 = new PdfPCell(new Paragraph(String.valueOf(transaction.getType())));
                PdfPCell pdfPCell8 = new PdfPCell(new Paragraph(String.valueOf(transaction.getAmount())));
                pdfPTable.addCell(pdfPCell5);
                pdfPTable.addCell(pdfPCell6);
                pdfPTable.addCell(pdfPCell7);
                pdfPTable.addCell(pdfPCell8);
            });

            document.add(header);
            document.add(pdfPTable);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
