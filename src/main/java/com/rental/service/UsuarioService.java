package com.rental.service;

import com.mongodb.client.model.Filters;
import com.rental.model.Usuario;
import com.rental.model.enums.TipoUsuario;
import org.bson.types.ObjectId;
import java.util.List;

public class UsuarioService extends BaseService<Usuario> {

    public UsuarioService() {
        super("usuarios", Usuario.class);
    }

    private final OdooSyncService odooSync = new OdooSyncService();

    @Override
    public void create(Usuario usuario) {
        super.create(usuario);
        LogService.getInstance().log("CREATE", "Usuario", usuario.getEmail(), "Usuario creado: " + usuario.getNombre());
        odooSync.syncContacts(usuario);
    }

    @Override
    public void update(ObjectId id, Usuario usuario) {
        super.update(id, usuario);
        LogService.getInstance().log("UPDATE", "Usuario", usuario.getEmail(),
                "Usuario actualizado: " + usuario.getNombre());
        odooSync.syncContacts(usuario);
    }

    public Usuario findByEmail(String email) {
        return collection.find(Filters.eq("email", email)).first();
    }

    public List<Usuario> listarClientes() {
        return findByFilter(Filters.eq("rol", TipoUsuario.CLIENTE));
    }
}
