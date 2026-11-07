package pe.edu.moivo.data;

import android.database.sqlite.SQLiteConstraintException;
import java.time.LocalDate;
import pe.edu.moivo.domain.Validation;

/** Las escrituras se ejecutan fuera del hilo de UI desde TaskViewModel. */
public class Repository {
    public final MoivoDatabase db;
    public final MoivoDao dao;
    public Repository(MoivoDatabase db) { this.db = db; dao = db.dao(); }
    public void seed() { db.runInTransaction(() -> dao.seed(SeedData.packages())); }
    public long saveClient(String name, String document, String email, String phone) {
        Validation.client(name, document, email, phone);
        Client client = new Client();
        client.name = name.trim(); client.document = document; client.email = email.trim(); client.phone = phone;
        try { return dao.insertClient(client); }
        catch (SQLiteConstraintException e) { throw new IllegalArgumentException("Ya existe un cliente con ese DNI."); }
    }
    public long reserve(long clientId, long packageId, String date, int people) {
        Validation.reservation(clientId, packageId, date, people, LocalDate.now());
        return db.runInTransaction(() -> {
            if (dao.findClient(clientId) == null) throw new IllegalArgumentException("El cliente seleccionado ya no existe.");
            TourPackage tour = dao.findPackage(packageId);
            if (tour == null) throw new IllegalArgumentException("El paquete seleccionado ya no existe.");
            Reservation r = new Reservation();
            r.clientId = clientId; r.packageId = packageId; r.date = date; r.people = people;
            r.unitPriceCents = tour.priceCents; r.createdAt = System.currentTimeMillis();
            return dao.insertReservation(r);
        });
    }
    public long advance(long id, String expected) {
        String next = Validation.nextStatus(expected);
        if (dao.transition(id, expected, next) != 1) throw new IllegalArgumentException("La reserva cambió. Revisa su estado e inténtalo otra vez.");
        return id;
    }
}
