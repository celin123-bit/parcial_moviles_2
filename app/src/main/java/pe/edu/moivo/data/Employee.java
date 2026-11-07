package pe.edu.moivo.data;

import java.util.List;

/** Datos ficticios para la consulta académica. Importes enteros en céntimos. */
public final class Employee {
    public final String name;
    public final String role;
    public final long salaryCents;
    public Employee(String name, String role, long salaryCents) {
        this.name = name; this.role = role; this.salaryCents = salaryCents;
    }
    public static List<Employee> examples() {
        return java.util.Collections.unmodifiableList(java.util.Arrays.asList(
            new Employee("Ana Quispe", "Guía de turismo", 250000L),
            new Employee("Luis Mamani", "Asesor de viajes", 220000L),
            new Employee("Rosa Flores", "Coordinadora de operaciones", 320000L),
            new Employee("Diego Torres", "Asistente administrativo", 180000L)
        ));
    }
}
