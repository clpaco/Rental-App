package com.rental.app;

import com.rental.model.Equipo;
import com.rental.model.Usuario;
import com.rental.model.enums.EstadoEquipo;
import com.rental.model.enums.TipoUsuario;
import com.rental.service.EquipoService;
import com.rental.service.UsuarioService;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Starting TestRunner...");

        try {
            UsuarioService usuarioService = new UsuarioService();
            EquipoService equipoService = new EquipoService();

            // 1. Crear Usuario
            System.out.println("Creando usuario...");
            Usuario u = new Usuario();
            u.setNombre("Juan Perez");
            u.setEmail("juan@test.com");
            u.setTelefono("123456789");
            u.setRol(TipoUsuario.CLIENTE);
            usuarioService.create(u);
            System.out.println("Usuario creado: " + u.getId());

            // 2. Buscar Usuario
            System.out.println("Buscando usuario...");
            Usuario encontrado = usuarioService.findByEmail("juan@test.com");
            if (encontrado != null) {
                System.out.println("Encontrado: " + encontrado.getNombre());
            }

            // 3. Crear Equipo
            System.out.println("Creando equipo...");
            Equipo e = new Equipo();
            e.setCodigo("CAM-001");
            e.setNombre("Camara Sony A7III");
            e.setCategoria("Camaras");
            e.setEstado(EstadoEquipo.DISPONIBLE);
            e.setValorCompra(2000.0);
            e.setTarifaDiaria(50.0);

            equipoService.create(e);
            System.out.println("Equipo creado: " + e.getId());

            // 4. Buscar Equipo
            System.out.println("Buscando equipo...");
            Equipo foundEquipo = equipoService.findByCodigo("CAM-001");
            if (foundEquipo != null) {
                System.out.println("Equipo encontrado: " + foundEquipo.getNombre());

                // Update status
                foundEquipo.setEstado(EstadoEquipo.ALQUILADO);
                equipoService.update(foundEquipo.getId(), foundEquipo);
                System.out.println("Equipment updated status to: " + foundEquipo.getEstado());

                // Delete
                equipoService.delete(foundEquipo.getId());
                System.out.println("Equipment deleted");
            } else {
                System.err.println("Equipment NOT found!");
            }

            System.out.println("Tests Completed Successfully.");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
