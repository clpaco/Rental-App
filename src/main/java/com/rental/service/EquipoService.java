package com.rental.service;

import com.mongodb.client.model.Filters;
import com.rental.model.Equipo;
import org.bson.types.ObjectId;

import java.util.List;

public class EquipoService extends BaseService<Equipo> {

    public EquipoService() {
        super("equipos", Equipo.class);
    }

    private final OdooSyncService odooSync = new OdooSyncService();

    public Equipo findByCodigo(String codigo) {
        return collection.find(Filters.eq("codigo", codigo)).first();
    }

    @Override
    public void create(Equipo equipo) {
        super.create(equipo);
        LogService.getInstance().log("CREATE", "Equipo", equipo.getCodigo(), "Equipo creado: " + equipo.getNombre());
        odooSync.syncProduct(equipo);
    }

    @Override
    public void update(ObjectId id, Equipo equipo) {
        super.update(id, equipo);
        LogService.getInstance().log("UPDATE", "Equipo", equipo.getCodigo(),
                "Equipo actualizado: " + equipo.getNombre());
        odooSync.syncProduct(equipo);
    }

    @Override
    public void delete(ObjectId id) {
        super.delete(id);
        LogService.getInstance().log("DELETE", "Equipo", id.toString(), "Equipo eliminado");
    }

    public List<Equipo> findByCategoria(String categoria) {
        return findByFilter(Filters.eq("categoria", categoria));
    }
}
