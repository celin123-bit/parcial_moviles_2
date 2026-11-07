package pe.edu.moivo.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import pe.edu.moivo.R;
import pe.edu.moivo.data.Client;
import pe.edu.moivo.data.TourPackage;
import pe.edu.moivo.databinding.ActivityReservationFormBinding;
import pe.edu.moivo.domain.Validation;

public class ReservationFormActivity extends BaseActivity {
    private ActivityReservationFormBinding b;
    private List<Client> clients = new ArrayList<>();
    private List<TourPackage> packages = new ArrayList<>();
    private long selectedClient, selectedPackage;
    private String date = "";
    private final ActivityResultLauncher<Intent> createClient = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            selectedClient = result.getData().getLongExtra("client_id", 0);
            renderClients();
        }
    });
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (isFinishing()) return;
        b = ActivityReservationFormBinding.inflate(getLayoutInflater()); screen(b.getRoot(), "Reservas", true);
        selectedClient = state == null ? getIntent().getLongExtra("client_id", 0) : state.getLong("client_id");
        selectedPackage = state == null ? getIntent().getLongExtra("package_id", 0) : state.getLong("package_id");
        if (state != null) date = state.getString("date", "");
        renderDate();
        b.newClient.setOnClickListener(v -> createClient.launch(new Intent(this, ClientFormActivity.class)));
        b.date.setOnClickListener(v -> chooseDate());
        b.save.setOnClickListener(v -> save());
        b.people.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Se calcula después del cambio. */ }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { total(); }
            @Override public void afterTextChanged(Editable s) { /* No se modifica el texto ingresado. */ }
        });
        vm.repo.dao.clients().observe(this, value -> { clients = value; renderClients(); });
        vm.repo.dao.packages().observe(this, value -> { packages = value; renderPackages(); });
        vm.busy.observe(this, busy -> {
            b.save.setEnabled(!busy); b.newClient.setEnabled(!busy);
            b.save.setText(busy ? "Guardando…" : getString(R.string.save_reservation));
        });
        vm.result.observe(this, event -> {
            if (!event.consume()) return;
            if (event.error != null) { b.error.setText(event.error); return; }
            message("Reserva #" + event.id + " guardada en estado PENDIENTE.");
            startActivity(new Intent(this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP).putExtra("show_reservations", true));
            finish();
        });
    }
    private void renderClients() {
        if (b == null) return;
        List<String> labels = new ArrayList<>();
        labels.add(clients.isEmpty() ? "Registra primero un cliente" : "Selecciona un cliente");
        int selection = 0;
        for (int i = 0; i < clients.size(); i++) {
            Client c = clients.get(i); labels.add(c.name + " · DNI " + c.document);
            if (c.id == selectedClient) selection = i + 1;
        }
        bindSpinner(b.clientSpinner, labels, selection, position -> {
            // Un cliente recién creado puede llegar en la siguiente emisión de Room.
            if (position > 0) selectedClient = clients.get(position - 1).id;
            else if (clients.stream().anyMatch(c -> c.id == selectedClient)) selectedClient = 0;
        });
    }
    private void renderPackages() {
        List<String> labels = new ArrayList<>(); labels.add("Selecciona un paquete");
        int selection = 0;
        for (int i = 0; i < packages.size(); i++) {
            TourPackage p = packages.get(i); labels.add(p.name + " · " + Formats.money(p.priceCents));
            if (p.id == selectedPackage) selection = i + 1;
        }
        bindSpinner(b.packageSpinner, labels, selection, position -> { selectedPackage = position == 0 ? 0 : packages.get(position - 1).id; total(); });
        total();
    }
    private interface Selection { void selected(int position); }
    private void bindSpinner(Spinner spinner, List<String> labels, int selection, Selection callback) {
        spinner.setOnItemSelectedListener(null);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, labels);
        adapter.setDropDownViewResource(R.layout.spinner_text);
        spinner.setAdapter(adapter); spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { callback.selected(position); }
            @Override public void onNothingSelected(AdapterView<?> parent) { spinner.setSelection(0); }
        });
    }
    private void chooseDate() {
        LocalDate today = LocalDate.now();
        LocalDate initial = date.isEmpty() ? today : LocalDate.parse(date);
        if (initial.isBefore(today)) initial = today;
        DatePickerDialog dialog = new DatePickerDialog(this, (picker, y, m, d) -> {
            date = LocalDate.of(y, m + 1, d).toString(); renderDate();
        }, initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth());
        dialog.getDatePicker().setMinDate(today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        dialog.show();
    }
    private void renderDate() { b.date.setText(date.isEmpty() ? getString(R.string.select_date) : Formats.date(date)); }
    private int people() {
        try { return Integer.parseInt(b.people.getText().toString()); }
        catch (NumberFormatException e) { return 0; }
    }
    private void total() {
        for (TourPackage p : packages) if (p.id == selectedPackage && people() >= 1 && people() <= 30) {
            b.total.setText("Total: " + Formats.money(p.priceCents * people()) + "\n" + Formats.money(p.priceCents) + " por persona"); return;
        }
        b.total.setText("Selecciona un paquete y una cantidad válida para calcular el total.");
    }
    private void save() {
        b.error.setText(""); b.peopleLayout.setError(null);
        int count = people();
        if (count < 1 || count > 30) b.peopleLayout.setError("Ingresa entre 1 y 30 personas.");
        try { Validation.reservation(selectedClient, selectedPackage, date, count, LocalDate.now()); }
        catch (IllegalArgumentException e) { b.error.setText(e.getMessage()); return; }
        // Captura inmutable: cambios en los campos no alteran una escritura ya iniciada.
        final long clientId = selectedClient, packageId = selectedPackage;
        final String selectedDate = date;
        vm.run(() -> vm.repo.reserve(clientId, packageId, selectedDate, count));
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle state) {
        state.putLong("client_id", selectedClient); state.putLong("package_id", selectedPackage); state.putString("date", date);
        super.onSaveInstanceState(state);
    }
}
