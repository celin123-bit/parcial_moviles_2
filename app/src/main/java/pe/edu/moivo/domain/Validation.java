package pe.edu.moivo.domain;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Reglas puras, reutilizadas por formularios, repositorio y pruebas. */
public final class Validation {
    private Validation() { }
    public static final String DEMO_EMAIL = "demo@kusitours.pe";
    public static final String DEMO_PASSWORD = "Kusi2026!";
    public static boolean email(String value) {
        return value != null && value.length() <= 120 && value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }
    public static boolean login(String email, String password) {
        return DEMO_EMAIL.equals(email.trim().toLowerCase(Locale.ROOT)) && DEMO_PASSWORD.equals(password);
    }
    public static void client(String name, String document, String email, String phone) {
        if (name.trim().length() < 3 || name.trim().length() > 80) throw new IllegalArgumentException("El nombre debe tener entre 3 y 80 caracteres.");
        if (!document.matches("[0-9]{8}")) throw new IllegalArgumentException("El DNI debe contener 8 dígitos.");
        if (!email(email)) throw new IllegalArgumentException("Ingresa un correo válido.");
        if (!phone.matches("[0-9]{9}")) throw new IllegalArgumentException("El teléfono debe contener 9 dígitos.");
    }
    public static void reservation(long clientId, long packageId, String date, int people, LocalDate today) {
        if (clientId <= 0) throw new IllegalArgumentException("Selecciona un cliente.");
        if (packageId <= 0) throw new IllegalArgumentException("Selecciona un paquete.");
        try {
            LocalDate selected = LocalDate.parse(date);
            if (selected.isBefore(today)) throw new IllegalArgumentException("La fecha no puede estar en el pasado.");
        } catch (DateTimeParseException e) { throw new IllegalArgumentException("Selecciona una fecha válida."); }
        if (people < 1 || people > 30) throw new IllegalArgumentException("La cantidad debe estar entre 1 y 30 personas.");
    }
    public static String nextStatus(String current) {
        if ("PENDIENTE".equals(current)) return "CONFIRMADO";
        if ("CONFIRMADO".equals(current)) return "FINALIZADO";
        throw new IllegalArgumentException("Esta reserva ya está finalizada o su estado no es válido.");
    }
}
