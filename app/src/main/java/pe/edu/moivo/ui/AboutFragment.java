package pe.edu.moivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import pe.edu.moivo.R;

public class AboutFragment extends Fragment {
    public AboutFragment() { super(R.layout.fragment_about); }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        Typography.apply(view);
        view.findViewById(R.id.about_salaries).setOnClickListener(v ->
            startActivity(new Intent(requireContext(), SalaryActivity.class)));
    }
}
