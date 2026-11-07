package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import pe.edu.moivo.databinding.ActivitySplashBinding;

/** Presentación de cinco segundos; no bloquea el hilo principal ni navega en segundo plano. */
public class SplashActivity extends BaseActivity {
    static final long DURATION_MS = 5000L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long remainingMs = DURATION_MS;
    private long resumedAt;
    private boolean navigating;
    private final Runnable enter = () -> {
        if (isFinishing() || isDestroyed() || navigating) return;
        navigating = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };
    @Override protected boolean requiresSession() { return false; }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) remainingMs = state.getLong("remaining_ms", DURATION_MS);
        screen(ActivitySplashBinding.inflate(getLayoutInflater()).getRoot(), "", false);
        toolbar.setVisibility(android.view.View.GONE);
    }
    @Override protected void onResume() {
        super.onResume();
        resumedAt = SystemClock.elapsedRealtime();
        handler.postDelayed(enter, remainingMs);
    }
    private void accountVisibleTime() {
        if (resumedAt != 0) {
            remainingMs = Math.max(0, remainingMs - (SystemClock.elapsedRealtime() - resumedAt));
            resumedAt = 0;
        }
    }
    @Override protected void onPause() {
        accountVisibleTime();
        handler.removeCallbacks(enter);
        super.onPause();
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle state) {
        long pending = resumedAt == 0 ? remainingMs :
            Math.max(0, remainingMs - (SystemClock.elapsedRealtime() - resumedAt));
        state.putLong("remaining_ms", pending);
        super.onSaveInstanceState(state);
    }
    @Override protected void onDestroy() { handler.removeCallbacks(enter); super.onDestroy(); }
}
