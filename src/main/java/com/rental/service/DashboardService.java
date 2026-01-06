package com.rental.service;

import com.rental.model.Equipo;
import com.rental.model.Factura;
import com.rental.model.Reserva;
import com.rental.model.enums.EstadoEquipo;
import com.rental.model.enums.EstadoFactura;
import com.rental.model.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private final EquipoService equipoService = new EquipoService();
    private final FacturaService facturaService = new FacturaService();
    private final ReservaService reservaService = new ReservaService();
    private final UsuarioService usuarioService = new UsuarioService();

    public Map<EstadoEquipo, Integer> getEquiposPorEstado() {
        Map<EstadoEquipo, Integer> stats = new HashMap<>();
        List<Equipo> all = equipoService.findAll();
        for (Equipo e : all) {
            stats.put(e.getEstado(), stats.getOrDefault(e.getEstado(), 0) + 1);
        }
        return stats;
    }

    public Map<String, Double> getIngresosPorMes(int year) {
        Map<String, Double> stats = new HashMap<>();
        // Initialize all months
        for (Month m : Month.values()) {
            stats.put(m.name(), 0.0);
        }

        List<Factura> facturas = facturaService.findAll();
        for (Factura f : facturas) {
            if (f.getFecha() != null && f.getFecha().getYear() == year) {
                // Only count paid or pending? Let's count all non-cancelled for "Revenue" or
                // just PAID.
                // Let's assume TOTAL revenue regardless of status for this simple chart, or
                // filter by PAID.
                if (f.getEstado() != EstadoFactura.CANCELADA) {
                    // Safety check: Ignore future dates for revenue
                    if (f.getFecha().isAfter(LocalDate.now())) {
                        continue;
                    }
                    String monthName = f.getFecha().getMonth().name();
                    stats.put(monthName, stats.get(monthName) + f.getTotal());
                }
            }
        }
        return stats;
    }

    public long countActiveRentals() {
        // Reservas active currently
        LocalDate now = LocalDate.now();
        List<Reserva> reservas = reservaService.findAll();
        return reservas.stream()
                .filter(r -> !r.getFechaInicio().isAfter(now) && !r.getFechaFin().isBefore(now))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.EN_CURSO
                        || r.getEstado() == EstadoReserva.ACTIVA)
                .count();
    }

    public long countClients() {
        return usuarioService.listarClientes().size();
    }
}
