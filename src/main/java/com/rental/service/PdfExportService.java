package com.rental.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.rental.model.Factura;
import com.rental.model.LineaFactura;
import com.rental.model.Usuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;

public class PdfExportService {

    private final UsuarioService usuarioService = new UsuarioService();

    public void exportarFactura(Factura factura, File dest) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph("RENTAL EQUIPMENT APP")
                .setBold().setFontSize(20));
        document.add(new Paragraph(
                "Factura #" + (factura.getNumeroFactura() != null ? factura.getNumeroFactura() : "BORRADOR")));
        document.add(new Paragraph("Fecha: " + factura.getFecha().format(DateTimeFormatter.ISO_DATE)));

        Usuario cliente = null;
        if (factura.getClienteId() != null) {
            cliente = usuarioService.findById(factura.getClienteId());
        }

        document.add(new Paragraph("\nCliente:"));
        document.add(new Paragraph(cliente != null ? cliente.getNombre() : factura.getNombreClienteSnapshot()));
        if (cliente != null) {
            document.add(new Paragraph("Email: " + cliente.getEmail()));
            document.add(new Paragraph("Dirección: " + cliente.getDireccion()));
        }

        document.add(new Paragraph("\n"));

        // Table
        Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 1, 1, 2, 2 }));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell("Concepto");
        table.addHeaderCell("Cant");
        table.addHeaderCell("Días");
        table.addHeaderCell("Precio/Día");
        table.addHeaderCell("Total");

        for (LineaFactura item : factura.getItems()) {
            table.addCell(item.getConcepto());
            table.addCell(String.valueOf(item.getCantidad()));
            table.addCell(String.valueOf(item.getDias()));
            table.addCell(String.format("%.2f", item.getPrecioDia()));
            table.addCell(String.format("%.2f", item.getTotalLinea()));
        }

        document.add(table);

        // Totals
        document.add(new Paragraph("\n"));
        document.add(new Paragraph(String.format("Subtotal: %.2f", factura.getSubtotal())));
        document.add(new Paragraph(String.format("IVA (%.0f%%): %.2f", factura.getTasaIva() * 100, factura.getIva())));
        document.add(new Paragraph(String.format("TOTAL: %.2f", factura.getTotal())).setBold().setFontSize(14));

        document.close();
    }
}
