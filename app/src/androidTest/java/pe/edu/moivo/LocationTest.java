package pe.edu.moivo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import pe.edu.moivo.ui.MapActivity;
import pe.edu.moivo.ui.Session;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/** El emulador debe tener una posición enviada con adb emu geo fix, o un GPS disponible. */
@RunWith(AndroidJUnit4.class)
public class LocationTest {
    @Test public void grantedPermissionShowsProviderLocation() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        MoivoApp app = (MoivoApp) context.getApplicationContext();
        app.io.submit(() -> app.repository().seed()).get(10, TimeUnit.SECONDS);
        Session.set(context, true);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().grantRuntimePermission(context.getPackageName(), Manifest.permission.ACCESS_COARSE_LOCATION);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().grantRuntimePermission(context.getPackageName(), Manifest.permission.ACCESS_FINE_LOCATION);
        context.startActivity(new Intent(context, MapActivity.class).putExtra("package_id", 1L)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        assertTrue(device.wait(Until.hasObject(By.res("pe.edu.moivo", "locate")), 15000));
        onView(withId(R.id.locate)).perform(scrollTo(), click());
        assertTrue("El proveedor debe entregar una posición; inyectar GPS en el emulador antes de probar.",
            device.wait(Until.hasObject(By.textContains("Ubicación:")), 25000));
        device.takeScreenshot(new java.io.File(context.getExternalFilesDir(null), "location-granted.png"));
    }
}
