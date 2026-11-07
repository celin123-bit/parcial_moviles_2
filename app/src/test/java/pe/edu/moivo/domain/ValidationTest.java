package pe.edu.moivo.domain;

import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class ValidationTest {
    @Test public void demoCredentialsAreExplicitAndRejectInvalidPassword() {
        assertTrue(Validation.login(" DEMO@KUSITOURS.PE ", "Kusi2026!"));
        assertFalse(Validation.login("demo@kusitours.pe", "wrong"));
        assertFalse(Validation.email("correo@"));
    }
    @Test public void clientsRequireValidFields() {
        Validation.client("Ana Ruiz", "12345678", "ana@example.com", "987654321");
        assertThrows(IllegalArgumentException.class, () -> Validation.client("A", "12345678", "ana@example.com", "987654321"));
        assertThrows(IllegalArgumentException.class, () -> Validation.client("Ana Ruiz", "123", "ana@example.com", "987654321"));
        assertThrows(IllegalArgumentException.class, () -> Validation.client("Ana Ruiz", "12345678", "inválido", "987654321"));
        assertThrows(IllegalArgumentException.class, () -> Validation.client("Ana Ruiz", "12345678", "ana@example.com", "abc"));
    }
    @Test public void reservationsRejectMissingSelectionsPastDatesAndInvalidHeadcounts() {
        LocalDate today = LocalDate.of(2026, 10, 9);
        Validation.reservation(1, 1, "2026-10-09", 1, today);
        Validation.reservation(1, 1, "2026-10-10", 30, today);
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(0, 1, "2026-10-09", 1, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 0, "2026-10-09", 1, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 1, "2026-10-08", 1, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 1, "", 1, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 1, "2026-02-30", 1, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 1, "2026-10-09", 0, today));
        assertThrows(IllegalArgumentException.class, () -> Validation.reservation(1, 1, "2026-10-09", 31, today));
    }
    @Test public void stateMachineHasExactlyTwoForwardTransitions() {
        assertEquals("CONFIRMADO", Validation.nextStatus("PENDIENTE"));
        assertEquals("FINALIZADO", Validation.nextStatus("CONFIRMADO"));
        assertThrows(IllegalArgumentException.class, () -> Validation.nextStatus("FINALIZADO"));
        assertThrows(IllegalArgumentException.class, () -> Validation.nextStatus("UNKNOWN"));
    }
}
