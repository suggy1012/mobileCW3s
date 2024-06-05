// EmployeeViewHolder.java
package com.example.serega;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EmployeeViewHolder extends RecyclerView.ViewHolder {

    private final TextView idTextView;
    private final TextView nameTextView;
    private final TextView roleTextView;
    private final Button removeButton;
    private DatabaseReference databaseReference;

    public EmployeeViewHolder(@NonNull View itemView) {
        super(itemView);
        idTextView = itemView.findViewById(R.id.employee_id);
        nameTextView = itemView.findViewById(R.id.employee_name);
        roleTextView = itemView.findViewById(R.id.employee_role);
        removeButton = itemView.findViewById(R.id.remove_button);

    }

    public void bind(@NonNull Employee employee) {
        idTextView.setText(employee.getId());
        nameTextView.setText(employee.getName());
        roleTextView.setText(employee.getRole());
        databaseReference = FirebaseDatabase.getInstance().getReference("IT_Company/" + employee.getDepartment() + "/personnel");

        removeButton.setOnClickListener(v -> {
            // Remove the employee from the database
            String employeeId = employee.getId();
            int number = Integer.parseInt(employeeId) - 1;
            databaseReference.child(String.valueOf(number)).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(itemView.getContext(), "Developer removed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(itemView.getContext(), "Failed to remove developer: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
