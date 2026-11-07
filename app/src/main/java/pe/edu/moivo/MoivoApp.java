package pe.edu.moivo;

import android.app.Application;
import androidx.room.Room;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pe.edu.moivo.data.MoivoDatabase;
import pe.edu.moivo.data.Repository;

public class MoivoApp extends Application {
    public final ExecutorService io = Executors.newSingleThreadExecutor();
    private Repository repository;
    @Override public void onCreate() {
        super.onCreate();
        repository = new Repository(Room.databaseBuilder(this, MoivoDatabase.class, "moivo.db").build());
    }
    public Repository repository() { return repository; }
}
