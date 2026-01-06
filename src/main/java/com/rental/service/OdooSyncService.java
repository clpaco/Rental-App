package com.rental.service;

import com.rental.model.Factura;
import com.rental.model.Usuario;
import com.rental.model.Equipo;
import com.rental.model.LineaFactura;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import javafx.scene.control.Alert;
import javafx.application.Platform;

import java.net.URL;
import java.util.*;

public class OdooSyncService {

    private String odooUrl = "http://localhost:8069"; // Local Docker
    private String database = "odoo_db";
    private String username; // Removed hardcoded default
    private String password;
    private int uid = -1;

    // Use full service to look up users by ID
    // private final UsuarioService usuarioService = new UsuarioService(); // Local
    // instantiation used instead

    private static OdooSyncService instance;

    public static OdooSyncService getInstance() {
        if (instance == null) {
            instance = new OdooSyncService();
        }
        return instance;
    }

    public OdooSyncService() {
        // Private or Public (Public for now but usage should prefer singleton)
    }

    public boolean login(String user, String pass) {
        this.username = user;
        this.password = pass;
        this.uid = -1; // Reset UID
        return authenticate();
    }

    public void setConfig(String url, String db, String user, String pass) {
        this.odooUrl = url;
        this.database = db;
        this.username = user;
        this.password = pass;
    }

    private boolean authenticate() {
        if (uid > 0)
            return true;
        try {
            XmlRpcClient client = new XmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(odooUrl + "/xmlrpc/2/common"));
            client.setConfig(config);

            Object id = client.execute("authenticate",
                    Arrays.asList(database, username, password, Collections.emptyMap()));
            if (id instanceof Integer) {
                this.uid = (int) id;
                LogService.getInstance().log("ODOO", "LOGIN", username, "Conexión establecida. UID: " + uid);
                return true;
            }
        } catch (Exception e) {
            logError("Error autenticación: " + e.getMessage());
        }
        return false;
    }

    private Object execute(String model, String method, List<?> params) throws Exception {
        if (!authenticate())
            throw new Exception("No autenticado en Odoo");

        XmlRpcClient client = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(odooUrl + "/xmlrpc/2/object"));
        client.setConfig(config);

        List<Object> args = new ArrayList<>();
        args.add(database);
        args.add(uid);
        args.add(password);
        args.add(model);
        args.add(method);
        args.addAll(params);

        return client.execute("execute_kw", args);
    }

    public boolean syncContacts(Usuario usuario) {
        new Thread(() -> {
            try {
                if (!authenticate()) {
                    showAlert("Error Conexión", "No se pudo conectar a Odoo. Verifique credenciales.");
                    return;
                }

                // Check if exists
                List<Object> condition = new ArrayList<>();
                condition.add("email");
                condition.add("=");
                condition.add(usuario.getEmail());

                List<Object> domain = new ArrayList<>();
                domain.add(condition);

                Object[] result = (Object[]) execute("res.partner", "search",
                        Collections.singletonList(Collections.singletonList(domain)));

                int partnerId;
                if (result.length > 0) {
                    partnerId = (int) result[0];
                    // Update
                    Map<String, Object> values = new HashMap<>();
                    values.put("name", usuario.getNombre());
                    execute("res.partner", "write", Arrays.asList(Arrays.asList(Arrays.asList(partnerId), values)));
                    logSuccess("Usuario actualizado: " + partnerId);
                } else {
                    // Create
                    Map<String, Object> values = new HashMap<>();
                    values.put("name", usuario.getNombre());
                    values.put("email", usuario.getEmail());
                    partnerId = (int) execute("res.partner", "create", Arrays.asList(Arrays.asList(values)));
                    logSuccess("Usuario creado: " + partnerId);
                }

                showAlert("Sincronización Odoo", "Cliente sincronizado OK. ID Odoo: " + partnerId);

            } catch (Exception e) {
                logError("Error sync usuario: " + e.getMessage());
                showAlert("Error Importante", "Fallo al sincronizar con Odoo: " + e.getMessage());
            }
        }).start();
        return true;
    }

    public boolean syncProduct(Equipo equipo) {
        new Thread(() -> {
            try {
                if (!authenticate())
                    return; // Silent fail or log

                // Use 'product.template' (or 'product.product')
                Map<String, Object> values = new HashMap<>();
                values.put("name", equipo.getNombre());
                values.put("default_code", equipo.getCodigo());
                values.put("list_price", equipo.getTarifaDiaria());
                values.put("standard_price", equipo.getValorCompra()); // Cost
                values.put("type", "service"); // Rental is usually service, or conusmable? Service implies no stock
                                               // track.

                // Check exist
                List<Object> condition = new ArrayList<>();
                condition.add("default_code");
                condition.add("=");
                condition.add(equipo.getCodigo());

                List<Object> domain = new ArrayList<>();
                domain.add(condition);

                // execute params: [db, uid, pass, model, method, [domain]]
                // domain itself is [[field, op, val]]
                // so we pass [[[[field, op, val]]]] ? No.
                // execute takes List params -> adds them to args list.
                // if we pass params = [domain] -> args = [..., [domain]]
                // Odoo search(domain). domain = [domain] = [[cond]].
                // CORRECT: Arrays.asList(domain) where domain is [[cond]].

                // Let's stick to the structure that worked for contacts:
                // execute("res.partner", "search", Collections.singletonList(domain));
                // where domain = [[cond]].

                Object[] result = (Object[]) execute("product.template", "search",
                        Collections.singletonList(Collections.singletonList(domain)));

                if (result.length > 0) {
                    execute("product.template", "write",
                            Arrays.asList(Arrays.asList(Arrays.asList(result[0]), values)));
                } else {
                    execute("product.template", "create", Arrays.asList(Arrays.asList(values)));
                }

                Platform.runLater(() -> LogService.getInstance().log("ODOO", "Equipo", equipo.getCodigo(),
                        "Producto sincronizado v16"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return true; // Async
    }

    public boolean exportInvoice(Factura factura) {
        new Thread(() -> {
            try {
                if (!authenticate()) {
                    showAlert("Fallo Auth", "No se pudo conectar a Odoo");
                    return;
                }

                // 1. Find Partner
                if (factura.getClienteId() == null) {
                    throw new Exception("Factura sin cliente asignado");
                }
                // Avoid circular dependency by instantiating locally
                UsuarioService userService = new UsuarioService();
                Usuario u = userService.findById(factura.getClienteId());
                if (u == null) {
                    throw new Exception("Cliente no encontrado en BD local");
                }

                int partnerId = findOrCreatePartner(u);

                // 2. Create Invoice Lines
                List<Object> lines = new ArrayList<>();
                for (LineaFactura item : factura.getItems()) {
                    Map<String, Object> lineDict = new HashMap<>();
                    lineDict.put("name", item.getConcepto());
                    lineDict.put("quantity", (double) (item.getCantidad() * item.getDias()));
                    lineDict.put("price_unit", (double) item.getPrecioDia());
                    // lineDict.put("product_id", ...); // Ideally find product by code

                    // Specific Odoo encoding for One2many: (0, 0, {values})
                    lines.add(Arrays.asList(0, 0, lineDict));
                }

                // 3. Create Account Move (Inventory)
                Map<String, Object> invoiceDict = new HashMap<>(); // RESTORED
                invoiceDict.put("move_type", "out_invoice"); // Customer Invoice
                invoiceDict.put("partner_id", partnerId);
                invoiceDict.put("invoice_line_ids", lines);

                // Set Local Invoice Number as Reference and Name (for Draft)
                if (factura.getNumeroFactura() != null) {
                    invoiceDict.put("ref", factura.getNumeroFactura());
                    invoiceDict.put("name", factura.getNumeroFactura());
                }

                if (factura.getFecha() != null) {
                    invoiceDict.put("invoice_date", factura.getFecha().toString());
                }

                int moveId = (int) execute("account.move", "create", Arrays.asList(Arrays.asList(invoiceDict)));

                // Post invoice if local state implies it
                if (factura.getEstado() == com.rental.model.enums.EstadoFactura.PAGADA) {
                    execute("account.move", "action_post", Arrays.asList(Arrays.asList(moveId)));
                    logSuccess("Factura Publicada (Posted) en Odoo: " + moveId);

                    // Register Payment
                    registerPayment(moveId, partnerId, factura.getTotal(), factura.getFecha(),
                            factura.getNumeroFactura());
                }

                showAlert("Factura Odoo", "Factura creada en Odoo. ID: " + moveId);
                logSuccess("Factura exportada ID: " + moveId);

            } catch (Exception e) {
                logError("Error exportando factura: " + e.getMessage());
                showAlert("Error Factura", "Fallo al crear factura en Odoo: \n" + e.getMessage());
            }
        }).start();
        return true;
    }

    private void registerPayment(int invoiceId, int partnerId, double amount, java.time.LocalDate date, String ref) {
        try {
            // 1. Find Journal (Bank or Cash)
            List<Object> domain = new ArrayList<>();
            List<Object> condition = new ArrayList<>();
            condition.add("type");
            condition.add("in");
            condition.add(Arrays.asList("bank", "cash"));
            domain.add(condition);

            Object[] journals = (Object[]) execute("account.journal", "search",
                    Collections.singletonList(Collections.singletonList(domain)));

            if (journals.length == 0) {
                logError("No se encontró Diario de Banco/Caja para registrar pago.");
                return;
            }
            int journalId = (int) journals[0];

            // 2. Create Payment
            Map<String, Object> paymentDict = new HashMap<>();
            paymentDict.put("payment_type", "inbound");
            paymentDict.put("partner_type", "customer");
            paymentDict.put("partner_id", partnerId);
            paymentDict.put("amount", amount);
            paymentDict.put("date", date != null ? date.toString() : java.time.LocalDate.now().toString());
            paymentDict.put("journal_id", journalId);
            if (ref != null)
                paymentDict.put("ref", ref);

            int paymentId = (int) execute("account.payment", "create", Arrays.asList(Arrays.asList(paymentDict)));

            // 3. Post Payment
            execute("account.payment", "action_post", Arrays.asList(Arrays.asList(paymentId)));
            logSuccess("Pago creado y validado: " + paymentId);

            // 4. Reconcile (The tricky part via XML-RPC without wizard)
            // We need to match liquidity line of payment with receivable line of invoice.

            // Get Invoice Receivable Line
            List<Object> invoiceParams = new ArrayList<>();
            List<Object> invCondition1 = new ArrayList<>();
            invCondition1.add("move_id");
            invCondition1.add("=");
            invCondition1.add(invoiceId);
            List<Object> invCondition2 = new ArrayList<>();
            invCondition2.add("account_type");
            invCondition2.add("=");
            invCondition2.add("asset_receivable");

            List<Object> invDomain = new ArrayList<>();
            invDomain.add(invCondition1);
            invDomain.add(invCondition2);

            Object[] invLines = (Object[]) execute("account.move.line", "search",
                    Collections.singletonList(Collections.singletonList(invDomain)));

            // Get Payment Receivable Line (Counterpart)
            List<Object> payParams = new ArrayList<>();
            List<Object> payCondition1 = new ArrayList<>();
            payCondition1.add("payment_id");
            payCondition1.add("=");
            payCondition1.add(paymentId);
            List<Object> payCondition2 = new ArrayList<>();
            payCondition2.add("account_type");
            payCondition2.add("=");
            payCondition2.add("asset_receivable"); // Must match the account type of invoice

            List<Object> payDomain = new ArrayList<>();
            payDomain.add(payCondition1);
            payDomain.add(payCondition2);

            Object[] payLines = (Object[]) execute("account.move.line", "search",
                    Collections.singletonList(Collections.singletonList(payDomain)));

            if (invLines.length > 0 && payLines.length > 0) {
                List<Integer> linesToReconcile = new ArrayList<>();
                for (Object id : invLines)
                    linesToReconcile.add((int) id);
                for (Object id : payLines)
                    linesToReconcile.add((int) id);

                // call reconcile() on the lines
                // execute_kw(..., 'account.move.line', 'reconcile', [[id1, id2]])
                execute("account.move.line", "reconcile", Arrays.asList(Arrays.asList(linesToReconcile.toArray())));
                logSuccess("Factura Reconciliada (PAGADA) en Odoo!");
            } else {
                logError("No se pudieron encontrar líneas para reconciliar.");
            }

        } catch (Exception e) {
            logError("Error registrando pago: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int findOrCreatePartner(Usuario u) throws Exception {
        if (u == null)
            return 1; // Fallback to generic
        List<Object> condition = new ArrayList<>();
        condition.add("email");
        condition.add("=");
        condition.add(u.getEmail());

        List<Object> domain = new ArrayList<>();
        domain.add(condition);

        Object[] result = (Object[]) execute("res.partner", "search",
                Collections.singletonList(Collections.singletonList(domain)));
        if (result.length > 0)
            return (int) result[0];

        Map<String, Object> val = new HashMap<>();
        val.put("name", u.getNombre());
        val.put("email", u.getEmail());
        return (int) execute("res.partner", "create", Arrays.asList(Arrays.asList(val)));
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.show();
        });
    }

    private void logSuccess(String msg) {
        LogService.getInstance().log("ODOO", "OK", "", msg);
    }

    private void logError(String msg) {
        LogService.getInstance().log("ODOO", "ERROR", "", msg);
    }
}
