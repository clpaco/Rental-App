package com.rental.service;

import com.rental.model.Equipo;
import com.rental.model.Mantenimiento;
import com.rental.model.enums.EstadoEquipo;
import com.rental.model.enums.EstadoMantenimiento;

import org.bson.types.ObjectId;

public class MantenimientoService extends BaseService<Mantenimiento> {

    private final EquipoService equipoService = new EquipoService();

    public MantenimientoService() {
        super("mantenimientos", Mantenimiento.class);
    }

    @Override
    public void create(Mantenimiento mantenimiento) {
        super.create(mantenimiento);
        updateEquipoStatus(mantenimiento);
    }

    @Override
    public void update(ObjectId id, Mantenimiento mantenimiento) {
        super.update(id, mantenimiento);
        updateEquipoStatus(mantenimiento);
    }

    private void updateEquipoStatus(Mantenimiento m) {
        if (m.getEquipoId() == null)
            return;

        Equipo equipo = equipoService.findById(m.getEquipoId());
        if (equipo == null)
            return;

        boolean changed = false;

        // Logic:
        // If Maintenance is IN_PROGRESS -> Equipment = EN_MANTENIMIENTO
        // If Maintenance is FINISHED -> Equipment = DISPONIBLE (only if it was
        // IN_MAINTENANCE)
        // If Maintenance is PENDING -> Equipment = DISPONIBLE (or whatever it was,
        // usually we don't block unless immediate)

        if (m.getEstado() == EstadoMantenimiento.EN_PROGRESO) {
            if (equipo.getEstado() != EstadoEquipo.EN_MANTENIMIENTO) {
                equipo.setEstado(EstadoEquipo.EN_MANTENIMIENTO);
                changed = true;
            }
        } else if (m.getEstado() == EstadoMantenimiento.FINALIZADO || m.getEstado() == EstadoMantenimiento.CANCELADO) {
            // When finishing, we free the equipment.
            // NOTE: Ideally verify if there isn't ANOTHER active maintenance. For
            // simplicity, we free it.
            if (equipo.getEstado() == EstadoEquipo.EN_MANTENIMIENTO) {
                equipo.setEstado(EstadoEquipo.DISPONIBLE);
                changed = true;
            }
        }

        if (changed) {
            equipoService.update(equipo.getId(), equipo);
        }
    }
}
