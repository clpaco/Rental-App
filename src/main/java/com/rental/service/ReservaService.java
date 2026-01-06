package com.rental.service;

import com.mongodb.client.model.Filters;
import com.rental.model.Reserva;
import com.rental.model.enums.EstadoReserva;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaService extends BaseService<Reserva> {

    public ReservaService() {
        super("reservas", Reserva.class);
    }

    @Override
    public void create(Reserva reserva) {
        super.create(reserva);
        LogService.getInstance().log("CREATE", "Reserva", reserva.getId() != null ? reserva.getId().toString() : "N/A",
                "Reserva creada");
    }

    @Override
    public void update(ObjectId id, Reserva reserva) {
        super.update(id, reserva);
        LogService.getInstance().log("UPDATE", "Reserva", id.toString(), "Reserva actualizada: " + reserva.getEstado());
    }

    @Override
    public void delete(ObjectId id) {
        super.delete(id);
        LogService.getInstance().log("DELETE", "Reserva", id.toString(), "Reserva cancelada/eliminada");
    }

    /**
     * Checks if an equipment is available between startDate and endDate.
     * Returns true if NO overlapping reservations exist for this equipment.
     */
    public boolean isDisponible(ObjectId equipoId, LocalDate start, LocalDate end) {
        // Filter:
        // 1. Status is NOT CANCELADA
        // 2. equipoIds contains equipoId
        // 3. Date ranges overlap: (StartA <= EndB) and (EndA >= StartB)

        // Constructing complex date query with Mongo Driver can be verbose.
        // We will fetch active reservations for the equipment and filtering in memory
        // (simpler for this constrained env).
        // For production with many records, do this DB side.

        List<Reserva> relevant = findByFilter(Filters.and(
                Filters.in("equipoIds", equipoId),
                Filters.ne("estado", EstadoReserva.CANCELADA)));

        for (Reserva r : relevant) {
            // Check overlap
            // Overlap if (StartA <= EndB) and (EndA >= StartB)
            // Here: (r.Start <= end) and (r.End >= start)

            boolean overlap = !r.getFechaInicio().isAfter(end) && !r.getFechaFin().isBefore(start);
            if (overlap) {
                return false; // Found a collision
            }
        }

        return true;
    }

    public List<Reserva> listarPorFecha(LocalDate start, LocalDate end) {
        // Naive implementation: fetch all and filter or use basic date filter
        // Ideally use $gte and $lte on ranges
        List<Reserva> all = findAll();
        List<Reserva> filtered = new ArrayList<>();
        for (Reserva r : all) {
            // Check if reservation intersects with the view window
            boolean intersects = !r.getFechaInicio().isAfter(end) && !r.getFechaFin().isBefore(start);
            if (intersects) {
                filtered.add(r);
            }
        }
        return filtered;
    }
}
