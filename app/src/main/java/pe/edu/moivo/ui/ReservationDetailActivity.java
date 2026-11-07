package pe.edu.moivo.ui;

import android.os.Bundle;
import android.view.View;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import pe.edu.moivo.data.Reservation;
import pe.edu.moivo.data.ReservationDetails;
import pe.edu.moivo.databinding.ActivityReservationDetailBinding;

public class ReservationDetailActivity extends BaseActivity {
    private ActivityReservationDetailBinding b;
    private ReservationDetails current;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (isFinishing()) return;
        b = ActivityReservationDetailBinding.inflate(getLayoutInflater()); screen(b.getRoot(), "Detalle de reserva", true);
        b.advance.setEnabled(false); b.map.setEnabled(false);
        long id = getIntent().getLongExtra("reservation_id", -1);
        vm.repo.dao.observeReservation(id).observe(this, value -> {
            current = value;
            if (value == null) { missing("La reserva solicitada no existe."); return; }
            render();
        });
        b.map.setOnClickListener(v -> { if (current != null) openMap(current.tour.id); });
        b.advance.setOnClickListener(v -> {
            if (current == null) return;
            final String expected = current.reservation.status;
            final long reservationId = current.reservation.id;
            String action = "PENDIENTE".equals(expected) ? "Confirmar" : "Finalizar";
            new MaterialAlertDialogBuilder(this).setTitle(action + " reserva #" + reservationId)
                .setMessage("El cambio de estado se guardará en este dispositivo.")
                .setPositiveButton(action, (dialog, which) -> vm.run(() -> vm.repo.advance(reservationId, expected)))
                .setNegativeButton("Cancelar", null).show();
        });
        vm.busy.observe(this, busy -> { if (current != null) render(); });
        vm.result.observe(this, event -> {
            if (event.consume()) message(event.error == null ? "Estado actualizado." : event.error);
        });
    }
    private void render() {
        Reservation r = current.reservation;
        b.title.setText("Reserva #" + r.id); b.status.setText(r.status);
        String created = Instant.ofEpochMilli(r.createdAt).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        b.details.setText("CLIENTE\n" + current.client.name + "\nDNI: " + current.client.document + "\nCorreo: " + current.client.email + "\nTeléfono: " + current.client.phone
            + "\n\nPAQUETE\n" + current.tour.name + " · " + current.tour.destination + "\n" + current.tour.duration
            + "\n\nVIAJE\nFecha: " + Formats.date(r.date) + "\nPersonas: " + r.people
            + "\nPrecio por persona: " + Formats.money(r.unitPriceCents) + "\nTotal: " + Formats.money(r.unitPriceCents * r.people)
            + "\n\nPUNTO DE ENCUENTRO\n" + current.tour.meetingPoint
            + "\n\nINCLUYE\n" + current.tour.includes + "\n\nRegistrada: " + created);
        boolean finished = "FINALIZADO".equals(r.status);
        b.advance.setVisibility(finished ? View.GONE : View.VISIBLE);
        b.advance.setEnabled(!Boolean.TRUE.equals(vm.busy.getValue()));
        b.advance.setText("PENDIENTE".equals(r.status) ? "Confirmar reserva" : "Finalizar reserva");
        b.map.setEnabled(true);
    }
}
