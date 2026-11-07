package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.List;
import pe.edu.moivo.R;
import pe.edu.moivo.data.Client;
import pe.edu.moivo.data.ReservationDetails;
import pe.edu.moivo.data.TourPackage;
import pe.edu.moivo.databinding.ActivityHomeBinding;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private RowAdapter adapter;
    private List<TourPackage> packages = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<ReservationDetails> reservations = new ArrayList<>();
    private int section = R.id.nav_packages;
    private boolean observing;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (isFinishing()) return;
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        screen(binding.getRoot(), getString(R.string.manage_trips), true);
        if (state != null) section = state.getInt("section", section);
        else if (getIntent().getBooleanExtra("show_reservations", false)) section = R.id.nav_reservations;
        adapter = new RowAdapter(this::openRow);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);
        binding.navigation.setSelectedItemId(section);
        binding.navigation.setOnItemSelectedListener(item -> { section = item.getItemId(); render(); return true; });
        binding.create.setOnClickListener(v -> startActivity(new Intent(this,
            section == R.id.nav_clients ? ClientFormActivity.class : ReservationFormActivity.class)));
        vm.result.observe(this, event -> {
            if (!event.consume()) return;
            if (event.error == null) observeData();
            else new MaterialAlertDialogBuilder(this).setTitle("No se pudo abrir la base de datos")
                .setMessage(event.error).setCancelable(false).setPositiveButton("Reintentar", (d, w) -> seed())
                .setNegativeButton("Cerrar", (d, w) -> finish()).show();
        });
        vm.busy.observe(this, busy -> binding.create.setEnabled(!busy));
        seed();
    }
    private void seed() { vm.run(() -> { vm.repo.seed(); return 0L; }); }
    private void observeData() {
        if (observing) return;
        observing = true;
        vm.repo.dao.packages().observe(this, value -> { packages = value; render(); });
        vm.repo.dao.clients().observe(this, value -> { clients = value; render(); });
        vm.repo.dao.reservations().observe(this, value -> { reservations = value; render(); });
    }
    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent); setIntent(intent);
        if (intent.getBooleanExtra("show_reservations", false)) { section = R.id.nav_reservations; binding.navigation.setSelectedItemId(section); }
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle state) { state.putInt("section", section); super.onSaveInstanceState(state); }
    private void render() {
        List<RowAdapter.Row> rows = new ArrayList<>();
        String empty;
        if (section == R.id.nav_packages) {
            binding.sectionTitle.setText(getString(R.string.packages_count) + " · " + packages.size());
            for (TourPackage p : packages) rows.add(new RowAdapter.Row(p.id, "package", p.destination.toUpperCase(java.util.Locale.ROOT), p.name, p.duration, Formats.money(p.priceCents) + " / persona · Ver detalle →"));
            empty = "No hay paquetes disponibles.";
        } else if (section == R.id.nav_clients) {
            binding.sectionTitle.setText("Clientes registrados · " + clients.size());
            for (Client c : clients) rows.add(new RowAdapter.Row(c.id, "client", "DNI " + c.document, c.name, c.email, c.phone + " · Ver datos →"));
            empty = "Aún no hay clientes. Registra el primero para crear una reserva.";
        } else {
            binding.sectionTitle.setText("Tus reservas · " + reservations.size());
            for (ReservationDetails d : reservations) rows.add(new RowAdapter.Row(d.reservation.id, "reservation", "#" + d.reservation.id + " · " + d.reservation.status, d.tour.name,
                d.client.name + "\n" + Formats.date(d.reservation.date) + " · " + d.reservation.people + " personas",
                Formats.money(d.reservation.unitPriceCents * d.reservation.people) + " · Ver reserva →"));
            empty = "Aún no hay reservas. Selecciona un cliente, un paquete y una fecha para comenzar.";
        }
        adapter.submitList(rows);
        binding.empty.setText(empty); binding.empty.setVisibility(rows.isEmpty() ? View.VISIBLE : View.GONE);
        binding.create.setText(section == R.id.nav_clients ? R.string.new_client : R.string.new_reservation);
    }
    private void openRow(long id) {
        if (section == R.id.nav_packages) startActivity(new Intent(this, PackageDetailActivity.class).putExtra("package_id", id));
        else if (section == R.id.nav_reservations) startActivity(new Intent(this, ReservationDetailActivity.class).putExtra("reservation_id", id));
        else for (Client c : clients) if (c.id == id) new MaterialAlertDialogBuilder(this).setTitle(c.name)
            .setMessage("DNI: " + c.document + "\nCorreo: " + c.email + "\nTeléfono: " + c.phone)
            .setPositiveButton("Crear reserva", (d, w) -> startActivity(new Intent(this, ReservationFormActivity.class).putExtra("client_id", c.id)))
            .setNegativeButton("Cerrar", null).show();
    }
    @Override public boolean onCreateOptionsMenu(Menu menu) { menu.add("Cerrar sesión").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); return true; }
    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        new MaterialAlertDialogBuilder(this).setTitle("Cerrar sesión")
            .setMessage("Los clientes y reservas permanecerán guardados en este dispositivo.")
            .setPositiveButton("Cerrar sesión", (d, w) -> {
                Session.set(this, false);
                startActivity(new Intent(this, MainActivity.class).putExtra("section", R.id.nav_login).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }).setNegativeButton("Cancelar", null).show();
        return true;
    }
}
