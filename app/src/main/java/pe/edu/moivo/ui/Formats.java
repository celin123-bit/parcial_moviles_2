package pe.edu.moivo.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formats {
    private Formats() { }
    public static String money(long cents) { return "S/ " + String.format(Locale.US, "%.2f", BigDecimal.valueOf(cents, 2)); }
    public static String date(String iso) { return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
}
