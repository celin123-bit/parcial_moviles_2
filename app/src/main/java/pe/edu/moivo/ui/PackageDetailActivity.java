package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.ActivityPackageDetailBinding;

public class PackageDetailActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (isFinishing()) return;
        ActivityPackageDetailBinding b = ActivityPackageDetailBinding.inflate(getLayoutInflater());
        screen(b.getRoot(), "Detalle del paquete", true);
        b.map.setEnabled(false); b.reserve.setEnabled(false);
        long id = getIntent().getLongExtra("package_id", -1);
        vm.repo.dao.observePackage(id).observe(this, p -> {
            if (p == null) { missing("El paquete solicitado no existe."); return; }
            b.heroImage.setImageResource(destinationImage(p.destination));
            b.name.setText(p.name); b.summary.setText(p.destination + "\n" + p.duration);
            b.price.setText(Formats.money(p.priceCents) + " / persona");
            b.description.setText(p.description); b.includes.setText(p.includes); b.meeting.setText(p.meetingPoint);
            b.map.setEnabled(true); b.reserve.setEnabled(true);
            b.map.setOnClickListener(v -> openMap(p.id));
            b.reserve.setOnClickListener(v -> startActivity(new Intent(this, ReservationFormActivity.class).putExtra("package_id", p.id)));
        });
    }
    private int destinationImage(String destination) {
        if ("Cusco".equals(destination)) return R.drawable.photo_cusco;
        if ("Ica".equals(destination)) return R.drawable.photo_ica;
        if ("Lima".equals(destination)) return R.drawable.photo_lima;
        return R.drawable.hero_andes;
    }
}
