package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import pe.edu.moivo.R;

public abstract class BaseActivity extends AppCompatActivity {
    protected TaskViewModel vm;
    protected MaterialToolbar toolbar;
    protected boolean requiresSession() { return true; }
    @Override protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        vm = new ViewModelProvider(this).get(TaskViewModel.class);
        if (requiresSession() && !Session.active(this)) {
            startActivity(new Intent(this, LoginActivity.class).putExtra("destination", getClass().getName()));
            finish();
        }
    }
    protected void screen(View content, String title, boolean back) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightNavigationBars(true);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getColor(R.color.background));
        toolbar = new MaterialToolbar(this);
        toolbar.setTitle(title);
        toolbar.setTitleTextAppearance(this, R.style.TextAppearance_Moivo_Title);
        toolbar.setBackgroundColor(getColor(R.color.background));
        toolbar.setTitleTextColor(getColor(R.color.ink));
        toolbar.setContentInsetsRelative(20, 20);
        root.addView(toolbar, new LinearLayout.LayoutParams(-1, -2));
        root.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));
        Typography.apply(root);
        setContentView(root);
        setSupportActionBar(toolbar);
        if (back) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationIconTint(getColor(R.color.primary_dark));
            toolbar.setNavigationContentDescription(R.string.back);
            toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets system = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(system.left, system.top, system.right, Math.max(system.bottom, keyboard.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(root);
    }
    protected void message(String text) { Toast.makeText(this, text, Toast.LENGTH_LONG).show(); }
    protected void missing(String text) {
        new MaterialAlertDialogBuilder(this).setTitle("Información no disponible").setMessage(text)
            .setCancelable(false).setPositiveButton("Volver", (dialog, which) -> finish()).show();
    }
    protected void openMap(long packageId) { startActivity(new Intent(this, MapActivity.class).putExtra("package_id", packageId)); }
}
