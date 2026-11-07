package pe.edu.moivo.ui;

import android.os.Bundle;
import android.content.Intent;
import androidx.fragment.app.FragmentContainerView;
import pe.edu.moivo.R;

/** Entrada al login para las Activities que requieren una sesión de demostración. */
public class LoginActivity extends BaseActivity {
    @Override protected boolean requiresSession() { return false; }
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (Session.active(this)) { enter(); return; }
        FragmentContainerView container = new FragmentContainerView(this);
        container.setId(R.id.login_container);
        screen(container, getString(R.string.nav_login), true);
        if (state == null) getSupportFragmentManager().beginTransaction()
            .replace(R.id.login_container, new LoginFragment()).commit();
    }
    public void enter() {
        Class<?> destination = SalaryActivity.class.getName().equals(getIntent().getStringExtra("destination"))
            ? SalaryActivity.class : HomeActivity.class;
        startActivity(new Intent(this, destination));
        finish();
    }
}
