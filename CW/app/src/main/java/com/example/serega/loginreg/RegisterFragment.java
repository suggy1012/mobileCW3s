package com.example.serega.loginreg;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.serega.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class RegisterFragment extends Fragment {
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button buttonReg;
    Button buttonLoginNow;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    public RegisterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_registerFragment_to_loginFragment);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        buttonReg = view.findViewById(R.id.btn_register);
        progressBar = view.findViewById(R.id.progressBar);
        buttonLoginNow = view.findViewById(R.id.btn_login_now);

        buttonLoginNow.setOnClickListener(navigation -> Navigation.findNavController(view)
                .navigate(R.id.action_registerFragment_to_loginFragment));

        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText()) ;
            String password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Email is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Password is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Authentication succeeded.",
                                    Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_registerFragment_to_loginFragment);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Authentication failed. " + Objects
                                            .requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}