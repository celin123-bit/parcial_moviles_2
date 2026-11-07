package pe.edu.moivo;

import android.content.Context;
import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;
import androidx.test.espresso.contrib.RecyclerViewActions;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import pe.edu.moivo.ui.LoginActivity;
import pe.edu.moivo.ui.Session;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.junit.Assert.*;

/** UI real. Borra solo los datos de la instalación de prueba antes de comenzar. */
@RunWith(AndroidJUnit4.class)
public class FullFlowTest {
    private UiDevice device;
    private MoivoApp app;
    @Before public void setup() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        app = (MoivoApp) context.getApplicationContext();
        app.io.submit(() -> { app.repository().db.clearAllTables(); app.repository().seed(); }).get(15, TimeUnit.SECONDS);
        Session.set(context, false);
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        waitText("Login");
    }
    private UiObject2 waitResource(String id) {
        UiObject2 object = device.wait(Until.findObject(By.res("pe.edu.moivo", id)), 15000);
        assertNotNull("No apareció el control " + id, object); return object;
    }
    private void waitText(String text) { assertTrue("No apareció: " + text, device.wait(Until.hasObject(By.textContains(text)), 15000)); }
    private void edit(int id, String value) { onView(withId(id)).perform(scrollTo(), replaceText(value), closeSoftKeyboard()); }
    private void tap(int id) { onView(withId(id)).perform(scrollTo(), click()); }
    @Test public void loginClientReservationStatesMapDenialAndRelaunch() throws Exception {
        tap(R.id.login); waitText("Ingresa un correo válido.");
        edit(R.id.email, "demo@kusitours.pe"); edit(R.id.password, "incorrecta"); tap(R.id.login);
        waitText("Credenciales incorrectas");
        edit(R.id.password, "Kusi2026!"); tap(R.id.login);
        waitText("Cusco esencial");
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        waitText("Detalle del paquete");
        tap(R.id.reserve); waitResource("client_spinner");
        tap(R.id.save); waitText("Selecciona un cliente.");
        tap(R.id.new_client); waitResource("document");
        tap(R.id.save); waitText("El DNI debe contener 8 dígitos.");
        edit(R.id.name, "Ana Estudiante"); edit(R.id.document, "12345678");
        edit(R.id.email, "ana@example.com"); edit(R.id.phone, "987654321"); tap(R.id.save);
        waitResource("client_spinner"); waitText("Ana Estudiante");
        device.setOrientationLeft(); device.waitForIdle(); device.setOrientationNatural(); device.waitForIdle();
        waitText("Ana Estudiante");
        tap(R.id.date); onView(withId(android.R.id.button1)).perform(click());
        edit(R.id.people, "0"); tap(R.id.save); waitText("Ingresa entre 1 y 30 personas.");
        edit(R.id.people, "2"); tap(R.id.save);
        waitText("PENDIENTE"); waitText("S/ 360.00");
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        waitResource("status"); onView(withId(R.id.status)).check(matches(withText("PENDIENTE")));
        tap(R.id.advance); onView(withId(android.R.id.button1)).perform(click()); onView(withId(R.id.status)).perform(scrollTo()); waitText("CONFIRMADO");
        tap(R.id.advance); onView(withId(android.R.id.button1)).perform(click()); onView(withId(R.id.status)).perform(scrollTo()); waitText("FINALIZADO");
        onView(withId(R.id.advance)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        tap(R.id.map); waitText("Plaza de Armas de Cusco");
        tap(R.id.locate);
        UiObject2 deny = device.wait(Until.findObject(By.res("com.android.permissioncontroller", "permission_deny_button")), 10000);
        assertNotNull("Debe solicitar permiso de ubicación", deny); deny.click();
        onView(withId(R.id.message)).perform(scrollTo()); waitText("Permiso denegado");
        device.pressBack(); onView(withId(R.id.status)).perform(scrollTo()); waitText("FINALIZADO"); device.pressBack(); waitText("Tus reservas");
        device.pressBack();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        waitText("Cusco esencial"); waitResource("nav_reservations").click(); waitText("FINALIZADO");
        assertEquals(1, (int) app.io.submit(() -> app.repository().dao.reservationCount()).get(10, TimeUnit.SECONDS));
        device.takeScreenshot(new java.io.File(context.getExternalFilesDir(null), "flow-finalizado.png"));
        device.unfreezeRotation();
    }
}
