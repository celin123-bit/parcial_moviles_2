package pe.edu.moivo.ui;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.ActivityMainBinding;

/** Las cuatro secciones públicas del Navigation Drawer tienen su propio Fragment. */
public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private int selected = R.id.nav_home;
    @Override protected boolean requiresSession() { return false; }

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        screen(binding.getRoot(), getString(R.string.app_name), false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, toolbar,
            R.string.open_drawer, R.string.close_drawer);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.primary_dark));
        binding.drawerNavigation.setNavigationItemSelectedListener(item -> {
            showSection(item.getItemId());
            binding.drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        selected = state == null ? getIntent().getIntExtra("section", R.id.nav_home)
            : state.getInt("section", R.id.nav_home);
        showSection(selected);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) binding.drawer.closeDrawer(GravityCompat.START);
                else if (selected != R.id.nav_home) showSection(R.id.nav_home);
                else { setEnabled(false); getOnBackPressedDispatcher().onBackPressed(); setEnabled(true); }
            }
        });
    }

    private void showSection(int id) {
        if (id != R.id.nav_about && id != R.id.nav_contact && id != R.id.nav_login) id = R.id.nav_home;
        selected = id;
        String tag = "drawer:" + id;
        Fragment target = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment.isAdded()) transaction.hide(fragment).setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        if (target == null) {
            if (id == R.id.nav_about) target = new AboutFragment();
            else if (id == R.id.nav_contact) target = new ContactFragment();
            else if (id == R.id.nav_login) target = new LoginFragment();
            else target = new HomeFragment();
            transaction.add(R.id.section_container, target, tag);
        }
        transaction.show(target).setMaxLifecycle(target, Lifecycle.State.RESUMED).commitNow();
        binding.drawerNavigation.setCheckedItem(id);
        toolbar.setTitle(binding.drawerNavigation.getMenu().findItem(id).getTitle());
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showSection(intent.getIntExtra("section", R.id.nav_home));
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle state) {
        state.putInt("section", selected);
        super.onSaveInstanceState(state);
    }
}
