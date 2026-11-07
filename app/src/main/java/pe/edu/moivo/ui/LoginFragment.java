package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.FragmentLoginBinding;
import pe.edu.moivo.domain.Validation;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    public LoginFragment() { super(R.layout.fragment_login); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        binding = FragmentLoginBinding.bind(view);
        Typography.apply(view);
        binding.login.setOnClickListener(v -> login());
        binding.password.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) { login(); return true; }
            return false;
        });
        binding.sessionTrips.setOnClickListener(v -> startActivity(new Intent(requireContext(), HomeActivity.class)));
        binding.sessionSalaries.setOnClickListener(v -> startActivity(new Intent(requireContext(), SalaryActivity.class)));
        binding.sessionLogout.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout).setMessage("Los clientes y reservas permanecerán guardados en este dispositivo.")
            .setNegativeButton("Cancelar", null).setPositiveButton(R.string.logout, (dialog, which) -> {
                Session.set(requireContext(), false);
                renderSession();
            }).show());
        renderSession();
    }
    @Override public void onResume() { super.onResume(); renderSession(); }
    private void renderSession() {
        if (binding == null) return;
        boolean active = Session.active(requireContext());
        binding.loginForm.setVisibility(active ? View.GONE : View.VISIBLE);
        binding.sessionPanel.setVisibility(active ? View.VISIBLE : View.GONE);
    }
    private void login() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString();
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);
        boolean valid = true;
        if (!Validation.email(email)) { binding.emailLayout.setError(getString(R.string.error_email)); valid = false; }
        if (password.isEmpty()) { binding.passwordLayout.setError("Ingresa la contraseña."); valid = false; }
        if (!valid) {
            if (binding.emailLayout.getError() != null) binding.email.requestFocus();
            else binding.password.requestFocus();
            return;
        }
        if (!Validation.login(email, password)) {
            binding.passwordLayout.setError("Credenciales incorrectas. Revisa los datos de acceso de demostración.");
            binding.password.requestFocus();
            return;
        }
        Session.set(requireContext(), true);
        binding.password.setText("");
        InputMethodManager keyboard = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        if (requireActivity() instanceof LoginActivity) ((LoginActivity) requireActivity()).enter();
        else renderSession();
    }
    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
