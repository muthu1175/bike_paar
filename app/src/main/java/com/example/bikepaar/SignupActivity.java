package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText etSignupEmail, etUsername, etSignupPassword, etConfirmPassword;
    Button btnCreateAccount;
    TextView tvLogin;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etSignupEmail = findViewById(R.id.etSignupEmail);
        etUsername = findViewById(R.id.etUsername);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvLogin = findViewById(R.id.tvLogin);
        btnBack = findViewById(R.id.btnBack);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        btnCreateAccount.setOnClickListener(v -> {
            if (!validateInputs()) return;

            Map<String, String> body = new HashMap<>();
            body.put("username", etUsername.getText().toString().trim());
            body.put("email", etSignupEmail.getText().toString().trim());
            body.put("password", etSignupPassword.getText().toString().trim());
            body.put("confirm_password",
                    etConfirmPassword.getText().toString().trim()); // 🔥 THIS


            api.signup(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                        finish(); // back to login
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(SignupActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

    }

    private boolean validateInputs() {
        return !TextUtils.isEmpty(etSignupEmail.getText())
                && !TextUtils.isEmpty(etUsername.getText())
                && !TextUtils.isEmpty(etSignupPassword.getText())
                && etSignupPassword.getText().toString()
                .equals(etConfirmPassword.getText().toString());
    }
}
