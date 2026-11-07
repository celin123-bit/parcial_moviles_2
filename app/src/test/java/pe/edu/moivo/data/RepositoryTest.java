package pe.edu.moivo.data;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.time.LocalDate;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class RepositoryTest {
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
    private MoivoDatabase db;
    private Repository repo;
    private Context context;
    @Before public void setup() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("test-persistence.db"); open(); repo.seed();
    }
    private void open() {
        db = Room.databaseBuilder(context, MoivoDatabase.class, "test-persistence.db").allowMainThreadQueries().build();
        repo = new Repository(db);
    }
    @After public void close() { db.close(); context.deleteDatabase("test-persistence.db"); }
    @Test public void seedDoesNotDuplicateOrOverwriteExistingPackage() {
        db.getOpenHelper().getWritableDatabase().execSQL("UPDATE packages SET name = 'Nombre conservado' WHERE id = 1");
        repo.seed(); repo.seed();
        assertEquals(3, repo.dao.packageCount());
        assertEquals("Nombre conservado", repo.dao.findPackage(1).name);
    }
    @Test public void duplicateDniIsRejectedWithoutDeletingClient() {
        long id = repo.saveClient("Ana Ruiz", "12345678", "ana@example.com", "987654321");
        assertThrows(IllegalArgumentException.class, () -> repo.saveClient("Otra Ana", "12345678", "otra@example.com", "987654321"));
        assertEquals("Ana Ruiz", repo.dao.findClient(id).name);
        assertEquals(1, repo.dao.documentCount("12345678"));
    }
    @Test public void reservationAndBothStatesSurviveClosingAndReopeningDatabase() {
        long client = repo.saveClient("Ana Ruiz", "12345678", "ana@example.com", "987654321");
        long id = repo.reserve(client, 1, LocalDate.now().plusDays(2).toString(), 3);
        assertEquals("PENDIENTE", repo.dao.findReservation(id).reservation.status);
        assertEquals(54000, repo.dao.findReservation(id).reservation.unitPriceCents * 3);
        repo.advance(id, "PENDIENTE");
        db.close(); open(); repo.seed();
        assertEquals("CONFIRMADO", repo.dao.findReservation(id).reservation.status);
        assertEquals("Ana Ruiz", repo.dao.findReservation(id).client.name);
        repo.advance(id, "CONFIRMADO");
        db.close(); open(); repo.seed();
        assertEquals("FINALIZADO", repo.dao.findReservation(id).reservation.status);
        assertEquals(1, repo.dao.reservationCount());
        assertEquals(3, repo.dao.packageCount());
        assertThrows(IllegalArgumentException.class, () -> repo.advance(id, "FINALIZADO"));
    }
    @Test public void reservationRejectsMissingForeignKeysAndStaleStateChanges() {
        assertThrows(IllegalArgumentException.class, () -> repo.reserve(999, 1, LocalDate.now().toString(), 2));
        long client = repo.saveClient("Ana Ruiz", "12345678", "ana@example.com", "987654321");
        assertThrows(IllegalArgumentException.class, () -> repo.reserve(client, 999, LocalDate.now().toString(), 2));
        long id = repo.reserve(client, 1, LocalDate.now().toString(), 2);
        repo.advance(id, "PENDIENTE");
        assertThrows(IllegalArgumentException.class, () -> repo.advance(id, "PENDIENTE"));
        assertEquals("CONFIRMADO", repo.dao.findReservation(id).reservation.status);
    }
    @Test public void priceSnapshotDoesNotChangeWithCatalogPrice() {
        long client = repo.saveClient("Ana Ruiz", "12345678", "ana@example.com", "987654321");
        long id = repo.reserve(client, 1, LocalDate.now().toString(), 2);
        db.getOpenHelper().getWritableDatabase().execSQL("UPDATE packages SET priceCents = 99900 WHERE id = 1");
        assertEquals(18000, repo.dao.findReservation(id).reservation.unitPriceCents);
    }
}
