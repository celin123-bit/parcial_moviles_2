package pe.edu.moivo.ui;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.concurrent.Callable;
import pe.edu.moivo.MoivoApp;
import pe.edu.moivo.data.Repository;

/** Conserva una escritura en curso al rotar y evita dobles envíos. */
public class TaskViewModel extends AndroidViewModel {
    public final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);
    public final MutableLiveData<Event> result = new MutableLiveData<>();
    public final Repository repo;
    public TaskViewModel(@NonNull Application app) { super(app); repo = ((MoivoApp) app).repository(); }
    public void run(Callable<Long> operation) {
        if (Boolean.TRUE.equals(busy.getValue())) return;
        busy.setValue(true);
        ((MoivoApp) getApplication()).io.execute(() -> {
            Event event;
            try { event = new Event(operation.call(), null); }
            catch (Exception e) {
                Log.e("Moivo", "No se pudo completar la operación", e);
                event = new Event(0, e instanceof IllegalArgumentException ? e.getMessage() : "No se pudo guardar o leer la información. Inténtalo nuevamente.");
            }
            busy.postValue(false);
            result.postValue(event);
        });
    }
    public static final class Event {
        public final long id;
        public final String error;
        private boolean consumed;
        Event(long id, String error) { this.id = id; this.error = error; }
        public boolean consume() { if (consumed) return false; consumed = true; return true; }
    }
}
