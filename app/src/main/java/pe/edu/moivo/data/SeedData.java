package pe.edu.moivo.data;

import java.util.Arrays;
import java.util.List;

public final class SeedData {
    private SeedData() { }
    public static List<TourPackage> packages() {
        return Arrays.asList(
            tour(1, "Cusco esencial", "Cusco", "1 día · 08:00 a 17:00", 18000,
                "Recorre el centro histórico y descubre Sacsayhuamán junto a un guía local. Caminata de dificultad moderada; lleva agua, abrigo y protección solar.",
                "Guía en español, movilidad local y entradas del recorrido. No incluye alimentación ni alojamiento.",
                "Plaza de Armas de Cusco, junto a la fuente central", -13.5164, -71.9785),
            tour(2, "Paracas y Huacachina", "Ica", "1 día · 06:00 a 20:00", 24000,
                "Una jornada entre costa, desierto y oasis. Paseo por la bahía de Paracas y visita al oasis de Huacachina. Actividades sujetas al clima.",
                "Transporte desde el punto de encuentro, guía y paseo por la bahía. No incluye almuerzo ni actividades de aventura.",
                "Plaza de Armas de Ica, frente a la municipalidad", -14.0678, -75.7286),
            tour(3, "Lima histórica", "Lima", "Medio día · 09:00 a 13:00", 9500,
                "Descubre plazas, balcones y calles del centro histórico en una visita guiada a pie. Una experiencia cultural de dificultad baja.",
                "Guía en español y entrada a un museo del recorrido. No incluye transporte hasta el punto de encuentro ni comidas.",
                "Plaza San Martín, monumento central", -12.0517, -77.0348));
    }
    private static TourPackage tour(long id, String name, String destination, String duration, long price,
            String description, String includes, String meeting, double lat, double lng) {
        TourPackage p = new TourPackage();
        p.id = id; p.name = name; p.destination = destination; p.duration = duration;
        p.priceCents = price; p.description = description; p.includes = includes;
        p.meetingPoint = meeting; p.latitude = lat; p.longitude = lng;
        return p;
    }
}
