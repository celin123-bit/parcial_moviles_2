package pe.edu.moivo.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TourPackage.class, Client.class, Reservation.class}, version = 1, exportSchema = true)
public abstract class MoivoDatabase extends RoomDatabase {
    public abstract MoivoDao dao();
}
