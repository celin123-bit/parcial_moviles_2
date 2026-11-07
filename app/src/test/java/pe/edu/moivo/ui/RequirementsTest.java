package pe.edu.moivo.ui;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import java.util.concurrent.TimeUnit;
import pe.edu.moivo.R;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class RequirementsTest {
    @Before public void resetSession() {
        Session.set(ApplicationProvider.getApplicationContext(), false);
    }
    private void select(MainActivity activity, int id) {
        ((NavigationView) activity.findViewById(R.id.drawer_navigation)).getMenu().performIdentifierAction(id, 0);
    }
    private void edit(MainActivity activity, int id, String text) {
        ((EditText) activity.findViewById(id)).setText(text);
    }
    private void idleUntil(long target) {
        long pending = target - android.os.SystemClock.uptimeMillis();
        assertTrue("El reloj no debe superar el plazo", pending >= 0);
        shadowOf(Looper.getMainLooper()).idleFor(pending, TimeUnit.MILLISECONDS);
    }
    @Test public void splashWaitsFiveSecondsThenFinishes() {
        try (ActivityController<SplashActivity> c = Robolectric.buildActivity(SplashActivity.class).create().start()) {
            long started = android.os.SystemClock.uptimeMillis();
            c.resume();
            idleUntil(started + 4999);
            assertNull(shadowOf(c.get()).getNextStartedActivity());
            shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.MILLISECONDS);
            assertEquals(MainActivity.class.getName(), shadowOf(c.get()).getNextStartedActivity().getComponent().getClassName());
            assertTrue(c.get().isFinishing());
        }
    }
    @Test public void splashDoesNotNavigateWhilePausedAndResumesRemainingTime() {
        try (ActivityController<SplashActivity> c = Robolectric.buildActivity(SplashActivity.class).create().start()) {
            long started = android.os.SystemClock.uptimeMillis();
            c.resume();
            idleUntil(started + 2000);
            c.pause();
            shadowOf(Looper.getMainLooper()).idleFor(10000, TimeUnit.MILLISECONDS);
            assertNull(shadowOf(c.get()).getNextStartedActivity());
            long resumed = android.os.SystemClock.uptimeMillis();
            c.resume();
            idleUntil(resumed + 2999);
            assertNull(shadowOf(c.get()).getNextStartedActivity());
            shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.MILLISECONDS);
            assertNotNull(shadowOf(c.get()).getNextStartedActivity());
        }
    }
    @Test public void splashRecreationKeepsElapsedTime() {
        try (ActivityController<SplashActivity> c = Robolectric.buildActivity(SplashActivity.class).create().start()) {
            long started = android.os.SystemClock.uptimeMillis();
            c.resume();
            idleUntil(started + 2000);
            c.recreate();
            idleUntil(started + 4999);
            assertNull(shadowOf(c.get()).getNextStartedActivity());
            shadowOf(Looper.getMainLooper()).idleFor(1, TimeUnit.MILLISECONDS);
            assertNotNull(shadowOf(c.get()).getNextStartedActivity());
        }
    }
    @Test public void drawerUsesFourFragmentsAndPreservesContactDraftAcrossRecreation() {
        try (ActivityController<MainActivity> c = Robolectric.buildActivity(MainActivity.class).setup().visible()) {
            assertTrue(c.get().getSupportFragmentManager().findFragmentById(R.id.section_container) instanceof HomeFragment);
            select(c.get(), R.id.nav_contact);
            edit(c.get(), R.id.contact_name, "Ana Quispe");
            edit(c.get(), R.id.contact_message, "Quiero visitar Cusco.");
            select(c.get(), R.id.nav_about);
            assertFalse(c.get().getSupportFragmentManager().findFragmentByTag("drawer:" + R.id.nav_about).isHidden());
            select(c.get(), R.id.nav_login);
            assertFalse(c.get().getSupportFragmentManager().findFragmentByTag("drawer:" + R.id.nav_login).isHidden());
            select(c.get(), R.id.nav_contact);
            assertEquals("Ana Quispe", ((EditText)c.get().findViewById(R.id.contact_name)).getText().toString());
            c.recreate();
            assertEquals(R.id.nav_contact, ((NavigationView)c.get().findViewById(R.id.drawer_navigation)).getCheckedItem().getItemId());
            assertEquals("Quiero visitar Cusco.", ((EditText)c.get().findViewById(R.id.contact_message)).getText().toString());
            c.get().getOnBackPressedDispatcher().onBackPressed();
            assertEquals(R.id.nav_home, ((NavigationView)c.get().findViewById(R.id.drawer_navigation)).getCheckedItem().getItemId());
        }
    }
    @Test public void contactRejectsInvalidDataThenPreparesEncodedEmailWithoutSendingIt() {
        try (ActivityController<MainActivity> c = Robolectric.buildActivity(MainActivity.class).setup().visible()) {
            select(c.get(), R.id.nav_contact);
            c.get().findViewById(R.id.send_contact).performClick();
            assertNotNull(((TextInputLayout)c.get().findViewById(R.id.contact_name_layout)).getError());
            assertNull(shadowOf(c.get()).getNextStartedActivity());
            edit(c.get(), R.id.contact_name, "Ana Quispe");
            edit(c.get(), R.id.contact_email, "ana@example.com");
            edit(c.get(), R.id.contact_phone, "987654321");
            edit(c.get(), R.id.contact_message, "Viaje a Perú & Cusco + dos personas?");
            c.get().findViewById(R.id.send_contact).performClick();
            Intent intent = shadowOf(c.get()).getNextStartedActivity();
            assertEquals(Intent.ACTION_SENDTO, intent.getAction());
            assertEquals("mailto", intent.getData().getScheme());
            assertTrue(Uri.decode(intent.getDataString()).contains("Viaje a Perú & Cusco + dos personas?"));
            assertEquals("ana@example.com", ((EditText)c.get().findViewById(R.id.contact_email)).getText().toString());
        }
    }
    @Test public void salaryRequiresSessionAndLoginReturnsToSalaryActivity() {
        try (ActivityController<SalaryActivity> c = Robolectric.buildActivity(SalaryActivity.class).create()) {
            Intent login = shadowOf(c.get()).getNextStartedActivity();
            assertEquals(LoginActivity.class.getName(), login.getComponent().getClassName());
            assertEquals(SalaryActivity.class.getName(), login.getStringExtra("destination"));
            Session.set(ApplicationProvider.getApplicationContext(), true);
            try (ActivityController<LoginActivity> l = Robolectric.buildActivity(LoginActivity.class, login).create()) {
                assertEquals(SalaryActivity.class.getName(), shadowOf(l.get()).getNextStartedActivity().getComponent().getClassName());
            }
        }
    }
    @Test public void salaryValidatesSelectionHidesStaleResultsAndRestoresConsultation() {
        Session.set(ApplicationProvider.getApplicationContext(), true);
        try (ActivityController<SalaryActivity> c = Robolectric.buildActivity(SalaryActivity.class).setup().visible()) {
            c.get().findViewById(R.id.consult_salary).performClick();
            assertEquals(View.VISIBLE, c.get().findViewById(R.id.salary_error).getVisibility());
            ((Spinner)c.get().findViewById(R.id.employee_spinner)).setSelection(1);
            shadowOf(Looper.getMainLooper()).idle();
            c.get().findViewById(R.id.consult_salary).performClick();
            assertTrue(((TextView)c.get().findViewById(R.id.salary_result)).getText().toString().contains("S/ 2500.00"));
            ((Spinner)c.get().findViewById(R.id.employee_spinner)).setSelection(3);
            shadowOf(Looper.getMainLooper()).idle();
            assertEquals(View.GONE, c.get().findViewById(R.id.salary_result).getVisibility());
            c.get().findViewById(R.id.consult_salary).performClick();
            c.recreate();
            assertTrue(((TextView)c.get().findViewById(R.id.salary_result)).getText().toString().contains("Rosa Flores"));
            assertEquals(View.VISIBLE, c.get().findViewById(R.id.salary_result).getVisibility());
        }
    }
}
