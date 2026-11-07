package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.ActivityClientFormBinding;
import pe.edu.moivo.domain.Validation;

public class ClientFormActivity extends BaseActivity {
    private ActivityClientFormBinding b;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (isFinishing()) return;
        b = ActivityClientFormBinding.inflate(getLayoutInflater()); screen(b.getRoot(), "Clientes", true);
        b.save.setOnClickListener(v -> save());
        vm.busy.observe(this, busy -> { b.save.setEnabled(!busy); b.save.setText(busy ? "Guardando…" : getString(R.string.save_client)); });
        vm.result.observe(this, event -> {
            if (!event.consume()) return;
            if (event.error != null) { message(event.error); return; }
            setResult(RESULT_OK, new Intent().putExtra("client_id", event.id));
            message("Cliente guardado."); finish();
        });
    }
    private void save() {
        String name = b.name.getText().toString().trim(), document = b.document.getText().toString().trim();
        String email = b.email.getText().toString().trim(), phone = b.phone.getText().toString().trim();
        b.nameLayout.setError(null); b.documentLayout.setError(null); b.emailLayout.setError(null); b.phoneLayout.setError(null);
        boolean valid = true;
        if (name.length() < 3) { b.nameLayout.setError("Ingresa al menos 3 caracteres."); valid = false; }
        if (!document.matches("[0-9]{8}")) { b.documentLayout.setError("El DNI debe contener 8 dígitos."); valid = false; }
        if (!Validation.email(email)) { b.emailLayout.setError("Ingresa un correo válido."); valid = false; }
        if (!phone.matches("[0-9]{9}")) { b.phoneLayout.setError("El teléfono debe contener 9 dígitos."); valid = false; }
        if (valid) vm.run(() -> vm.repo.saveClient(name, document, email, phone));
    }
}
