package com.rental.db;

import com.rental.model.*;
import com.rental.model.enums.*;
import com.rental.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.util.*;

public class DataSeeder {

        private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
        private static final UsuarioService usuarioService = new UsuarioService();
        private static final EquipoService equipoService = new EquipoService();
        private static final ReservaService reservaService = new ReservaService();
        private static final FacturaService facturaService = new FacturaService();
        private static final GarantiaService garantiaService = new GarantiaService();
        private static final MantenimientoService mantenimientoService = new MantenimientoService();

        public static void seed() {
                logger.info("⚡ INICIANDO RESET COMPLETO DE BASE DE DATOS (MODO FIJO) ⚡");

                // 0. CHECK IF DATA EXISTS TO AVOID WIPING
                if (!usuarioService.findAll().isEmpty()) {
                        logger.info("ℹ️ Datos detectados. OMITIENDO RESET (Persistencia asegurada).");
                        return;
                }

                // 1. HARD DELETE ALL
                deleteAllData();

                // 2. CREATE FIXED USERS
                List<Usuario> users = createFixedUsers();

                // 3. CREATE FIXED EQUIPMENT
                List<Equipo> equipment = createFixedEquipment();

                // 4. CREATE FIXED TRANSACTIONS (Reservas & Facturas)
                createFixedTransactions(users, equipment);

                logger.info("✅ Data Seeding FIJO completado con éxito.");
        }

        private static void deleteAllData() {
                try {
                        facturaService.findAll().forEach(x -> facturaService.delete(x.getId()));
                        reservaService.findAll().forEach(x -> reservaService.delete(x.getId()));
                        garantiaService.findAll().forEach(x -> garantiaService.delete(x.getId()));
                        mantenimientoService.findAll().forEach(x -> mantenimientoService.delete(x.getId()));
                        // We need to be careful deleting users if auth depends on them, but for reset
                        // it's fine.
                        usuarioService.findAll().forEach(x -> usuarioService.delete(x.getId()));
                        equipoService.findAll().forEach(x -> equipoService.delete(x.getId()));
                        logger.info("Base de datos limpiada.");
                } catch (Exception e) {
                        logger.error("Error al limpiar datos: " + e.getMessage());
                }
        }

        private static List<Usuario> createFixedUsers() {
                List<Usuario> list = new ArrayList<>();
                list.add(createU("Juan Admin", "admin@rental.com", "admin123", TipoUsuario.ADMIN));
                list.add(createU("Pedro Tecnico", "tecnico@rental.com", "tech123", TipoUsuario.TECNICO));
                list.add(createU("Ana Productora", "ana@cine.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Carlos Director", "carlos@film.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Lucia Eventos", "lucia@events.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Miguel Fotografo", "miguel@photo.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Sofia Indie", "sofia@indie.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Javier Drone", "javier@drone.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Elena TV", "elena@tv.com", "123", TipoUsuario.CLIENTE));
                list.add(createU("Pablo Sonido", "pablo@sound.com", "123", TipoUsuario.CLIENTE));
                return list;
        }

        private static List<Equipo> createFixedEquipment() {
                List<Equipo> list = new ArrayList<>();
                list.add(createE("EQ-100", "Sony A7S III", "Video", 3500.0, 120.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-101", "Canon R5", "Fotografia", 3800.0, 100.0, EstadoEquipo.ALQUILADO));
                list.add(createE("EQ-102", "Blackmagic 6K Pro", "Video", 2500.0, 90.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-103", "Red Komodo 6K", "Video", 6000.0, 200.0, EstadoEquipo.EN_MANTENIMIENTO));
                list.add(createE("EQ-104", "Arri Alexa Mini", "Cine", 35000.0, 1500.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-200", "Aputure 1200d", "Iluminacion", 2800.0, 150.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-201", "Nanlite Forza 720", "Iluminacion", 1800.0, 90.0, EstadoEquipo.ALQUILADO));
                list.add(createE("EQ-202", "Astera Titan Tube Set", "Iluminacion", 5000.0, 250.0,
                                EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-300", "Sennheiser MKH 416", "Audio", 900.0, 35.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-301", "Zoom F8n Pro", "Audio", 1100.0, 50.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-302", "DPA 4060 Kit", "Audio", 600.0, 25.0, EstadoEquipo.ALQUILADO));
                list.add(createE("EQ-400", "DJI Mavic 3 Pro", "Drones", 2200.0, 150.0, EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-401", "DJI Inspire 3", "Drones", 12000.0, 600.0, EstadoEquipo.EN_MANTENIMIENTO));
                list.add(createE("EQ-500", "Teradek Bolt 4K", "Video Wireless", 4000.0, 180.0,
                                EstadoEquipo.DISPONIBLE));
                list.add(createE("EQ-501", "SmallHD 703 Ultra", "Monitores", 2500.0, 100.0, EstadoEquipo.ALQUILADO));
                return list;
        }

        private static void createFixedTransactions(List<Usuario> users, List<Equipo> items) {
                LocalDate today = LocalDate.now();

                // Fixed scenario 1: High value rental last month
                createReservaFactura(users.get(2), items.subList(4, 5), today.minusMonths(1), 5,
                                EstadoReserva.FINALIZADA, EstadoFactura.PAGADA);

                // Fixed scenario 2: Active rental
                createReservaFactura(users.get(3), items.subList(0, 3), today.minusDays(2), 7, EstadoReserva.EN_CURSO,
                                EstadoFactura.PENDIENTE);

                // Fixed scenario 3: Future booking
                createReservaFactura(users.get(4), items.subList(11, 13), today.plusDays(5), 3,
                                EstadoReserva.CONFIRMADA, EstadoFactura.PENDIENTE);

                // Loop for volume (Fixed pattern)
                for (int i = 0; i < 15; i++) {
                        Usuario u = users.get((i % (users.size() - 2)) + 2);
                        Equipo e = items.get(i % items.size());
                        LocalDate date = today.minusMonths(2).plusDays(i * 3);
                        createReservaFactura(u, Collections.singletonList(e), date, 2, EstadoReserva.FINALIZADA,
                                        EstadoFactura.PAGADA);
                }

                // Warranties
                crearGarantia(items.get(4), "Arri Care", today.minusMonths(6), today.plusMonths(6), "Premium Coverage");
                crearGarantia(items.get(12), "DJI Care Refresh", today.minusMonths(1), today.plusMonths(11),
                                "Crash Replacement");
        }

        // --- HELPER CREATORS ---

        private static Usuario createU(String n, String e, String p, TipoUsuario t) {
                Usuario u = new Usuario();
                u.setNombre(n);
                u.setEmail(e);
                u.setPassword(p);
                u.setRol(t);
                usuarioService.create(u);
                return u;
        }

        private static Equipo createE(String c, String n, String cat, Double v, Double d, EstadoEquipo s) {
                Equipo e = new Equipo();
                e.setCodigo(c);
                e.setNombre(n);
                e.setCategoria(cat);
                e.setValorCompra(v);
                e.setTarifaDiaria(d);
                e.setEstado(s);
                e.setFotoUrl("https://via.placeholder.com/150");
                equipoService.create(e);
                return e;
        }

        private static void createReservaFactura(Usuario u, List<Equipo> eqs, LocalDate start, int days,
                        EstadoReserva er, EstadoFactura ef) {
                Reserva r = new Reserva();
                r.asignarCliente(u);
                r.setFechaInicio(start);
                r.setFechaFin(start.plusDays(days));
                eqs.forEach(r::agregarEquipo);
                r.setEstado(er);
                reservaService.create(r);

                Factura f = new Factura();
                f.asignarCliente(u);
                f.asignarFechaEmision(start);
                f.setEstado(ef);
                for (Equipo e : eqs) {
                        com.rental.model.LineaFactura lf = new com.rental.model.LineaFactura();
                        lf.setConcepto("Alquiler " + e.getNombre());
                        lf.setCantidad(1);
                        lf.setDias(days);
                        lf.setPrecioDia(e.getTarifaDiaria());
                        f.agregarItem(lf);
                }
                facturaService.create(f);
        }

        private static void crearGarantia(Equipo e, String prov, LocalDate in, LocalDate out, String cob) {
                Garantia g = new Garantia();
                g.asignarEquipo(e);
                g.setProveedor(prov);
                g.asignarFechaInicio(in);
                g.setFechaFin(out);
                g.setCobertura(cob);
                garantiaService.create(g);
        }
}
