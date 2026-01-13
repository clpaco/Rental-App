# 🏗️ Rental Equipment App

Una aplicación de escritorio completa para la gestión de alquiler de maquinaria y equipos audiovisuales/construcción. Desarrollada en **JavaFX** con arquitectura moderna y sincronización bidireccional con **Odoo ERP**.

## 🚀 Características Principales

### 📦 Gestión de Inventario
- CRUD completo de equipos.
- Control de stock y estados (Disponible, Alquilado, Mantenimiento, Baja).
- Categorización y precios (Coste compra vs Tarifa diaria).

### 👥 Gestión de Clientes (Sincronizado)
- Base de datos de clientes particulares y empresas.
- **Sincronización con Contactos de Odoo**: Los clientes creados en la app se reflejan en Odoo y viceversa.

### 📅 Calendario de Reservas
- Visualización gráfica de reservas en calendario.
- **Detección de Conflictos**: Evita solapamientos de reservas para el mismo equipo.
- **Lista Dual**: Selección intuitiva de múltiples equipos para una sola reserva.
- Editor de reservas con validación de disponibilidad.

### 💰 Facturación Automatizada
- Generación de facturas con cálculo automático de IRPF e IVA.
- **Integración Contable**: Las facturas finalizadas (PAGADA) se exportan automáticamente a Odoo como Asientos Contables (`account.move`).

### 🔧 Mantenimiento y Garantías
- Registro de incidencias y reparaciones.
- Control de garantías vigentes para los equipos.

### 📊 Panel de Control (Dashboard)
- Métricas en tiempo real: Alquileres activos, ingresos mensuales, ocupación.
- Gráficos nativos integrados.

### 🔐 Seguridad y Odoo
- **Login Integrado**: Autenticación directa contra la base de datos de usuarios de Odoo.
- Sincronización mediante XML-RPC.

---

## 🛠️ Requisitos Técnicos

- **Java JDK 17** o superior.
- **Maven** (incluido wrapper o portable).
- **MongoDB**: Base de datos local para la persistencia rápida de la app.
- **Odoo (Docker)**: ERP para la gestión contable y maestra de contactos (ver `docker-compose.yml`).

## ⚙️ Instalación y Ejecución

1. **Levantar Infraestructura (Docker)**
   ```bash
   docker-compose up -d
   ```
   Esto iniciará MongoDB y Odoo 16 localmente.

2. **Ejecutar la Aplicación**
   - **Opción A (Script Rápido):** Doble click en `CLICK_AQUI_PARA_EMPEZAR.bat`.
   - **Opción B (Consola):**
     ```bash
     mvn javafx:run
     ```

## 📂 Estructura del Proyecto

```
com.rental
├── app             # Clase Main y configuración
├── controller      # Lógica de UI (JavaFX Controllers)
├── db              # Conexión MongoDB y Seeder
├── model           # POJOs (Equipo, Usuario, Reserva...)
├── service         # Lógica de negocio y Repositorios
│   ├── OdooSyncService.java  # Cliente XML-RPC para Odoo
│   └── ...
└── resources
    └── fxml        # Vistas de la interfaz (.fxml)
```

---
**Desarrollado para la integración eficiente entre gestión operativa (App Desktop) y gestión financiera (Odoo).**
