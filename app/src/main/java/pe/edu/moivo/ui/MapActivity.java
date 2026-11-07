package pe.edu.moivo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Locale;
import pe.edu.moivo.BuildConfig;
import pe.edu.moivo.R;
import pe.edu.moivo.data.TourPackage;
import pe.edu.moivo.databinding.ActivityMapBinding;

public class MapActivity extends BaseActivity {
    private ActivityMapBinding b;
    private GoogleMap map;
    private TourPackage tour;
    private Marker positionMarker;
    private CancellationTokenSource locationToken;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean mapRequested, tilesLoaded;
    private String mapNotice = "";
    private final Runnable mapTimeout = () -> {
        if (!tilesLoaded && !isFinishing()) showMessage("El mapa tarda en cargar. Comprueba Internet, la API key y sus restricciones. Puedes abrir el encuentro en Google Maps.");
    };
    private final ActivityResultLauncher<String[]> permission = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), granted -> {
        if (hasLocation()) locate();
        else {
            showMessage("Permiso denegado. El punto de encuentro y las reservas siguen disponibles.");
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new MaterialAlertDialogBuilder(this).setTitle("Permiso de ubicación desactivado")
                    .setMessage("Puedes habilitarlo en los ajustes de la aplicación cuando quieras mostrar tu ubicación.")
                    .setPositiveButton("Abrir ajustes", (d, w) -> launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()))))
                    .setNegativeButton("Continuar sin ubicación", null).show();
            }
        }
    });
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); if (isFinishing()) return;
        b = ActivityMapBinding.inflate(getLayoutInflater()); screen(b.getRoot(), "Punto de encuentro", true);
        b.center.setEnabled(false); b.external.setEnabled(false);
        b.locate.setOnClickListener(v -> {
            if (hasLocation()) locate();
            else permission.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        });
        b.center.setOnClickListener(v -> { if (map != null && tour != null) map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(tour.latitude, tour.longitude), 16f)); });
        b.external.setOnClickListener(v -> {
            if (tour != null) launch(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US,
                "https://www.google.com/maps/search/?api=1&query=%.6f,%.6f", tour.latitude, tour.longitude))));
        });
        b.settings.setOnClickListener(v -> launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        vm.repo.dao.observePackage(getIntent().getLongExtra("package_id", -1)).observe(this, value -> {
            if (value == null) { missing("No se encontró el punto de encuentro."); return; }
            tour = value; b.meeting.setText(tour.name + "\n" + tour.meetingPoint + String.format(Locale.US, "\n%.6f, %.6f", tour.latitude, tour.longitude));
            b.external.setEnabled(true); prepareMap();
        });
    }
    private void prepareMap() {
        if (mapRequested) return;
        if (!BuildConfig.MAPS_CONFIGURED) {
            b.mapContainer.setVisibility(View.GONE); b.center.setVisibility(View.GONE);
            mapNotice = "Falta configurar MAPS_API_KEY en local.properties y recompilar. El mapa integrado requiere una clave de Google Cloud. ";
            showMessage("El botón Abrir en Google Maps muestra este punto mediante la aplicación o el navegador."); return;
        }
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (availability != ConnectionResult.SUCCESS) {
            b.mapContainer.setVisibility(View.GONE); b.center.setVisibility(View.GONE);
            mapNotice = "Google Play Services no está disponible o necesita actualizarse. ";
            showMessage("Puedes abrir el punto en Google Maps con el navegador.");
            if (GoogleApiAvailability.getInstance().isUserResolvableError(availability)) {
                android.app.Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, availability, 10);
                if (dialog != null) dialog.show();
            }
            return;
        }
        mapRequested = true;
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("meeting_map");
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map_container, fragment, "meeting_map").commit();
        }
        fragment.getMapAsync(googleMap -> {
            map = googleMap;
            LatLng meeting = new LatLng(tour.latitude, tour.longitude);
            map.addMarker(new MarkerOptions().position(meeting).title(tour.name).snippet(tour.meetingPoint)).showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(meeting, 16f));
            map.getUiSettings().setZoomControlsEnabled(true);
            b.center.setEnabled(true);
            map.setOnMapLoadedCallback(() -> { tilesLoaded = true; handler.removeCallbacks(mapTimeout); showMessage(getString(R.string.map_hint)); });
            handler.postDelayed(mapTimeout, 15000);
            enableLocationLayer();
        });
    }
    private boolean hasLocation() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @SuppressLint("MissingPermission")
    private void enableLocationLayer() {
        if (map != null && hasLocation()) {
            try { map.setMyLocationEnabled(true); map.getUiSettings().setMyLocationButtonEnabled(false); }
            catch (SecurityException e) { showMessage("El permiso cambió. Solicita nuevamente la ubicación."); }
        }
    }
    @SuppressLint("MissingPermission")
    private void locate() {
        if (!hasLocation()) return;
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            showMessage("Para obtener la ubicación actual se requiere Google Play Services disponible."); return;
        }
        enableLocationLayer();
        if (locationToken != null) locationToken.cancel();
        locationToken = new CancellationTokenSource();
        b.locate.setEnabled(false); showMessage("Buscando ubicación… Puede tardar hasta 20 segundos.");
        int priority = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ? Priority.PRIORITY_HIGH_ACCURACY : Priority.PRIORITY_BALANCED_POWER_ACCURACY;
        CurrentLocationRequest request = new CurrentLocationRequest.Builder().setPriority(priority).setDurationMillis(20000).setMaxUpdateAgeMillis(60000).build();
        try {
            LocationServices.getFusedLocationProviderClient(this).getCurrentLocation(request, locationToken.getToken())
                .addOnSuccessListener(this, location -> {
                    b.locate.setEnabled(true);
                    if (location == null) showMessage("No se pudo obtener la ubicación. Activa la ubicación del dispositivo y vuelve a intentarlo. Las reservas siguen disponibles.");
                    else showLocation(location);
                }).addOnFailureListener(this, error -> { b.locate.setEnabled(true); showMessage("No se pudo obtener la ubicación. Comprueba los ajustes y vuelve a intentarlo."); })
                .addOnCanceledListener(this, () -> b.locate.setEnabled(true));
        } catch (SecurityException e) { b.locate.setEnabled(true); showMessage("El permiso fue revocado. Solicita la ubicación nuevamente."); }
    }
    private void showLocation(Location location) {
        long age = (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / 1000000000L;
        if (age > 120) { showMessage("La última posición es antigua. Vuelve a intentar con la ubicación activada."); return; }
        LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        if (map != null) {
            if (positionMarker != null) positionMarker.remove();
            positionMarker = map.addMarker(new MarkerOptions().position(point).title("Mi ubicación").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15f));
        }
        showMessage(String.format(Locale.US, "Ubicación: %.6f, %.6f · precisión aproximada: %.0f m.", point.latitude, point.longitude, location.getAccuracy()));
    }
    private void showMessage(String text) { if (b != null) b.message.setText(mapNotice + text); }
    private void launch(Intent intent) {
        try { startActivity(intent); }
        catch (ActivityNotFoundException e) { showMessage("No hay una aplicación disponible para abrir esta acción."); }
    }
    @Override protected void onStop() {
        super.onStop(); if (locationToken != null) locationToken.cancel();
        if (b != null) b.locate.setEnabled(true);
        handler.removeCallbacks(mapTimeout);
    }
}
