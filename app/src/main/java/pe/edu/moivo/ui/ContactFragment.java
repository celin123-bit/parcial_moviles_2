package pe.edu.moivo.ui;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import pe.edu.moivo.R;
import pe.edu.moivo.databinding.FragmentContactBinding;
import pe.edu.moivo.domain.Validation;

public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;
    public ContactFragment() { super(R.layout.fragment_contact); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        binding = FragmentContactBinding.bind(view);
        Typography.apply(view);
        binding.companyDetails.setText(getString(R.string.contact_details, getString(R.string.company_email),
            getString(R.string.company_phone), getString(R.string.company_address), getString(R.string.company_hours)));
        binding.sendContact.setOnClickListener(v -> send());
        binding.copyContact.setOnClickListener(v -> {
            String body = validatedBody();
            if (body == null) return;
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.contact_subject), body));
            status(R.string.contact_copied);
        });
    }
    private String validatedBody() {
        String name = binding.contactName.getText().toString().trim();
        String email = binding.contactEmail.getText().toString().trim();
        String phone = binding.contactPhone.getText().toString().trim();
        String message = binding.contactMessage.getText().toString().trim();
        binding.contactNameLayout.setError(null);
        binding.contactEmailLayout.setError(null);
        binding.contactPhoneLayout.setError(null);
        binding.contactMessageLayout.setError(null);
        binding.contactStatus.setVisibility(View.GONE);
        boolean valid = true;
        View firstInvalid = null;
        if (name.length() < 3 || name.length() > 80) {
            binding.contactNameLayout.setError(getString(R.string.error_name));
            firstInvalid = binding.contactName; valid = false;
        }
        if (!Validation.email(email)) {
            binding.contactEmailLayout.setError(getString(R.string.error_email));
            if (firstInvalid == null) firstInvalid = binding.contactEmail; valid = false;
        }
        if (!phone.matches("[0-9]{9}")) {
            binding.contactPhoneLayout.setError(getString(R.string.error_phone));
            if (firstInvalid == null) firstInvalid = binding.contactPhone; valid = false;
        }
        if (message.length() < 10 || message.length() > 1000) {
            binding.contactMessageLayout.setError(getString(R.string.error_message));
            if (firstInvalid == null) firstInvalid = binding.contactMessage; valid = false;
        }
        if (!valid) { firstInvalid.requestFocus(); return null; }
        return getString(R.string.contact_body, name, email, phone, message);
    }
    private void send() {
        String body = validatedBody();
        if (body == null) return;
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.company_email)
            + "?subject=" + Uri.encode(getString(R.string.contact_subject)) + "&body=" + Uri.encode(body)));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.company_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_subject));
        intent.putExtra(Intent.EXTRA_TEXT, body);
        try { startActivity(intent); status(R.string.contact_ready); }
        catch (ActivityNotFoundException e) { status(R.string.contact_no_email); }
    }
    private void status(int message) {
        binding.contactStatus.setText(message);
        binding.contactStatus.setVisibility(View.VISIBLE);
    }
    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
