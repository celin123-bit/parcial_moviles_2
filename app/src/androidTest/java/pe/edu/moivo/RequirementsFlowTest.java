package pe.edu.moivo;

import android.content.Context;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.uiautomator.UiDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import pe.edu.moivo.ui.MainActivity;
import pe.edu.moivo.ui.Session;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RequirementsFlowTest {
    private void settleUi() {
        onView(isRoot()).perform(new androidx.test.espresso.ViewAction() {
            public org.hamcrest.Matcher<android.view.View> getConstraints() { return isRoot(); }
            public String getDescription() { return "Esperar a que termine la transicion de interfaz"; }
            public void perform(androidx.test.espresso.UiController controller, android.view.View view) {
                controller.loopMainThreadForAtLeast(400);
            }
        });
    }
    private void section(int id) {
        settleUi();
        onView(withId(R.id.drawer)).perform(DrawerActions.open());
        settleUi();
        onView(withId(R.id.drawer_navigation)).perform(NavigationViewActions.navigateTo(id));
    }
    private void edit(int id, String value) {
        onView(withId(id)).perform(scrollTo(), replaceText(value), closeSoftKeyboard());
    }
    private void tap(int id) { onView(withId(id)).perform(scrollTo(), click()); }
    private void screenshot(String name) {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .takeScreenshot(new java.io.File(c.getExternalFilesDir(null), name + ".png"));
    }
    @Test public void drawerContactLoginSalaryAndPortrait() {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Session.set(c, false);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            screenshot("requirements-inicio");
            onView(withId(R.id.drawer)).perform(DrawerActions.open());
            screenshot("requirements-drawer");
            onView(withId(R.id.drawer_navigation)).perform(NavigationViewActions.navigateTo(R.id.nav_about));
            onView(withText(R.string.company_mission_title)).perform(scrollTo()).check(matches(isDisplayed()));
            screenshot("requirements-nosotros");
            section(R.id.nav_contact);
            edit(R.id.contact_name, "Ana Quispe");
            edit(R.id.contact_email, "ana@example.com");
            edit(R.id.contact_phone, "987654321");
            edit(R.id.contact_message, "Deseo información del viaje a Cusco.");
            tap(R.id.copy_contact);
            onView(withId(R.id.contact_status)).perform(scrollTo()).check(matches(withText(R.string.contact_copied)));
            screenshot("requirements-contactos");
            section(R.id.nav_home);
            section(R.id.nav_contact);
            onView(withId(R.id.contact_name)).perform(scrollTo()).check(matches(withText("Ana Quispe")));
            scenario.recreate();
            onView(withId(R.id.contact_message)).perform(scrollTo()).check(matches(withText("Deseo información del viaje a Cusco.")));
            section(R.id.nav_login);
            edit(R.id.email, "demo@kusitours.pe");
            edit(R.id.password, "Kusi2026!");
            screenshot("requirements-login-form");
            tap(R.id.login);
            settleUi();
            screenshot("requirements-login-result");
            onView(withText(R.string.session_active)).perform(scrollTo()).check(matches(isDisplayed()));
            screenshot("requirements-login");
            tap(R.id.session_salaries);
            tap(R.id.consult_salary);
            onView(withId(R.id.salary_error)).check(matches(isDisplayed()));
            onView(withId(R.id.employee_spinner)).perform(scrollTo(), click());
            onData(equalTo("Ana Quispe")).perform(click());
            tap(R.id.consult_salary);
            onView(withId(R.id.salary_result)).perform(scrollTo()).check(matches(withText(containsString("S/ 2500.00"))));
            screenshot("requirements-salarios");
            try {
                UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
                device.setOrientationLeft();
                device.waitForIdle();
                assertEquals(android.content.res.Configuration.ORIENTATION_PORTRAIT, c.getResources().getConfiguration().orientation);
                device.setOrientationNatural();
                device.unfreezeRotation();
            } catch (android.os.RemoteException e) { throw new AssertionError(e); }
            androidx.test.espresso.Espresso.pressBack();
            tap(R.id.session_logout);
            onView(withId(android.R.id.button1)).perform(click());
            onView(withId(R.id.login)).perform(scrollTo()).check(matches(isDisplayed()));
            assertFalse(Session.active(c));
        }
    }
}
