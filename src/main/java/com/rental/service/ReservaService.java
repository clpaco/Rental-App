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
    /**
     * Checks if an equipment is available using specific dates.
     */
    public boolean isDisponible(ObjectId equipoId, LocalDate start, LocalDate end) {
        return isDisponibleExcluding(equipoId, start, end, null);
    }

    /**
     * Checks availability excluding a specific reservation ID (useful for updates).
     */
    public boolean isDisponibleExcluding(ObjectId equipoId, LocalDate start, LocalDate end, ObjectId excludeReservaId) {
        List<Reserva> relevant = findByFilter(Filters.and(
                Filters.eq("equipoIds", equipoId),
                Filters.ne("estado", EstadoReserva.CANCELADA)));

        for (Reserva r : relevant) {
            // Skip the reservation we are currently editing
            if (excludeReservaId != null && excludeReservaId.equals(r.getId())) {
                continue;
            }

            // Overlap check
            boolean overlap = !r.getFechaInicio().isAfter(end) && !r.getFechaFin().isBefore(start);
            if (overlap) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns future confirmed/active reservations for a specific equipment.
     */
    public List<Reserva> getFuturasPorEquipo(ObjectId equipoId) {
        List<Reserva> all = findByFilter(Filters.and(
                Filters.eq("equipoIds", equipoId),
                Filters.ne("estado", EstadoReserva.CANCELADA)));

        List<Reserva> future = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Reserva r : all) {
            // If end date is in the future (or today), it's relevant
            if (!r.getFechaFin().isBefore(today)) {
                future.add(r);
            }
        }
        // Sort by start date
        future.sort((r1, r2) -> r1.getFechaInicio().compareTo(r2.getFechaInicio()));
        return future;
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
