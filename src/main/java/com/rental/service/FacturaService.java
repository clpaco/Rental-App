package com.rental.service;

import com.mongodb.client.model.Sorts;
import com.rental.model.Factura;
import org.bson.types.ObjectId;
import java.time.LocalDate;

public class FacturaService extends BaseService<Factura> {

    public FacturaService() {
        super("facturas", Factura.class);
    }

    private final OdooSyncService odooSync = new OdooSyncService();

    @Override
    public void create(Factura factura) {
        if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isEmpty()) {
            factura.setNumeroFactura(generarNuevoNumero(factura.getFecha())); // Changed to call existing method with
                                                                              // Date
        }
        super.create(factura);
        LogService.getInstance().log("CREATE", "Factura", factura.getNumeroFactura(),
                "Factura creada: " + factura.getTotal());
        odooSync.exportInvoice(factura);
    }

    @Override
    public void update(ObjectId id, Factura factura) {
        super.update(id, factura);
        LogService.getInstance().log("UPDATE", "Factura", factura.getNumeroFactura(), "Factura actualizada");
    }

    public String generarNuevoNumero(LocalDate fecha) {
        int year = (fecha != null) ? fecha.getYear() : LocalDate.now().getYear();

        // Find last invoice for *this specific year* to ensure correct sequence
        // Pattern: FACT-YYYY-
        String prefix = "FACT-" + year + "-";

        // We filter by regex (starts with prefix) and sort descending to get the last
        // one
        Factura last = collection.find(com.mongodb.client.model.Filters.regex("numeroFactura", "^" + prefix))
                .sort(Sorts.descending("numeroFactura"))
                .first();

        int nextSeq = 1;

        if (last != null && last.getNumeroFactura() != null) {
            String[] parts = last.getNumeroFactura().split("-");
            // Expecting: FACT, YYYY, XXXX
            if (parts.length == 3) {
                try {
                    nextSeq = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        return String.format("FACT-%d-%04d", year, nextSeq);
    }
}
