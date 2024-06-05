package com.example.serega.loginreg;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.serega.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    Button buttonRegNow;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    public LoginFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null ){
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_loginFragment_to_accountFragment);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = view.findViewById(R.id.email);
        editTextPassword = view.findViewById(R.id.password);
        buttonRegNow = view.findViewById(R.id.btn_register_now);
        progressBar = view.findViewById(R.id.progressBar);
        buttonLogin = view.findViewById(R.id.btn_login);

        buttonRegNow.setOnClickListener(navigation -> Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment));

        buttonLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Email is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Password is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_accountFragment);
                                    }


                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}
