package pe.edu.moivo.ui;

import android.content.Context;

/** Solo una bandera local para el login académico, nunca un mecanismo de seguridad. */
public final class Session {
    private Session() { }
    public static boolean active(Context context) { return context.getSharedPreferences("demo_session", 0).getBoolean("active", false); }
    public static void set(Context context, boolean active) { context.getSharedPreferences("demo_session", 0).edit().putBoolean("active", active).apply(); }
}
