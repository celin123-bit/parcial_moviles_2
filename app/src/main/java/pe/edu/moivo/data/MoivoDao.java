package pe.edu.moivo.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.List;

@Dao
public interface MoivoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) void seed(List<TourPackage> packages);
    @Query("SELECT * FROM packages ORDER BY id") LiveData<List<TourPackage>> packages();
    @Query("SELECT * FROM packages WHERE id = :id") LiveData<TourPackage> observePackage(long id);
    @Query("SELECT * FROM packages WHERE id = :id") TourPackage findPackage(long id);
    @Query("SELECT COUNT(*) FROM packages") int packageCount();
    @Insert long insertClient(Client client);
    @Query("SELECT * FROM clients ORDER BY name COLLATE NOCASE, id") LiveData<List<Client>> clients();
    @Query("SELECT * FROM clients WHERE id = :id") Client findClient(long id);
    @Query("SELECT COUNT(*) FROM clients WHERE document = :document") int documentCount(String document);
    @Insert long insertReservation(Reservation reservation);
    @Transaction @Query("SELECT * FROM reservations ORDER BY id DESC") LiveData<List<ReservationDetails>> reservations();
    @Transaction @Query("SELECT * FROM reservations WHERE id = :id") LiveData<ReservationDetails> observeReservation(long id);
    @Transaction @Query("SELECT * FROM reservations WHERE id = :id") ReservationDetails findReservation(long id);
    @Query("SELECT COUNT(*) FROM reservations") int reservationCount();
    @Query("UPDATE reservations SET status = :next WHERE id = :id AND status = :expected")
    int transition(long id, String expected, String next);
}
