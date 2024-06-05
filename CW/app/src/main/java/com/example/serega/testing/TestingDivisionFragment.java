package com.example.serega.testing;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.serega.Employee;
import com.example.serega.EmployeeAdapter;
import com.example.serega.MainActivity;
import com.example.serega.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TestingDivisionFragment extends Fragment {

    private EmployeeAdapter adapter;
    private DatabaseReference databaseReference;

    public TestingDivisionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("IT_Company/Testing/personnel");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_testing_division, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.testing_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmployeeAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton buttonAddTester = rootView.findViewById(R.id.add_testing_btn);
        buttonAddTester.setOnClickListener(v -> showAddTesterDialog());

        displayTesters();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !MainActivity.isAdmin(currentUser.getEmail())) {
            buttonAddTester.setVisibility(View.GONE);
        }
        return rootView;
    }

    private void displayTesters() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Employee> testers = new ArrayList<>();
                for (DataSnapshot testerSnapshot : snapshot.getChildren()) {
                    Employee tester = testerSnapshot.getValue(Employee.class);
                    if (tester != null) {
                        testers.add(tester);
                    }
                }
                adapter.setEmployees(testers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TestingFragment", "Failed to read value.", error.toException());
            }
        });
    }

    private void showAddTesterDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_action, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("Add Tester");

        // Initialize the input fields in the dialog
        EditText editTextId = dialogView.findViewById(R.id.editTextTimestamp);
        editTextId.setHint("ID");
        EditText editTextName = dialogView.findViewById(R.id.editTextMessage);
        editTextName.setHint("Name");
        EditText editTextRole = dialogView.findViewById(R.id.editTextServerId);
        editTextRole.setHint("Role");

        Button addBtn = dialogView.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(v -> {
            // Extract the user input from the dialog
            String id = editTextId.getText().toString();
            String name = editTextName.getText().toString();
            String role = editTextRole.getText().toString();

            // Validate the user input
            if (id.isEmpty() || name.isEmpty() || role.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            String number = MainActivity.convertStringToNumberAndSubtractOne(editTextId.getText().toString());
            Employee tester = new Employee(id, name, role, "Testing");

            databaseReference.child(number).setValue(tester).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Tester added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to add tester: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Display the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}