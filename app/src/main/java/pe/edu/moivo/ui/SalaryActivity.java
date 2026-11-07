package pe.edu.moivo.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import pe.edu.moivo.R;
import pe.edu.moivo.data.Employee;
import pe.edu.moivo.databinding.ActivitySalaryBinding;

public class SalaryActivity extends BaseActivity {
    private ActivitySalaryBinding binding;
    private final List<Employee> employees = Employee.examples();
    private int displayedEmployee;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (isFinishing()) return;
        binding = ActivitySalaryBinding.inflate(getLayoutInflater());
        screen(binding.getRoot(), getString(R.string.view_salaries), true);
        List<String> names = new ArrayList<>();
        names.add(getString(R.string.employee_prompt));
        for (Employee employee : employees) names.add(employee.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, names);
        adapter.setDropDownViewResource(R.layout.spinner_text);
        binding.employeeSpinner.setAdapter(adapter);
        binding.consultSalary.setOnClickListener(v -> showSalary());
        binding.employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != displayedEmployee) binding.salaryResult.setVisibility(View.GONE);
                binding.salaryError.setVisibility(View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { binding.salaryResult.setVisibility(View.GONE); }
        });
        if (state != null) {
            binding.employeeSpinner.setSelection(state.getInt("selection", 0));
            displayedEmployee = state.getInt("displayed", 0);
            if (displayedEmployee > 0 && displayedEmployee == state.getInt("selection", 0)) render(displayedEmployee);
        }
    }
    private void showSalary() {
        int position = binding.employeeSpinner.getSelectedItemPosition();
        if (position <= 0 || position > employees.size()) {
            binding.salaryError.setVisibility(View.VISIBLE);
            binding.salaryResult.setVisibility(View.GONE);
            return;
        }
        displayedEmployee = position;
        binding.salaryError.setVisibility(View.GONE);
        render(position);
    }
    private void render(int position) {
        Employee employee = employees.get(position - 1);
        binding.salaryResult.setText(getString(R.string.salary_result,
            employee.name, employee.role, Formats.money(employee.salaryCents)));
        binding.salaryResult.setVisibility(View.VISIBLE);
    }
    @Override protected void onSaveInstanceState(@NonNull Bundle state) {
        state.putInt("selection", binding.employeeSpinner.getSelectedItemPosition());
        state.putInt("displayed", displayedEmployee);
        super.onSaveInstanceState(state);
    }
}
