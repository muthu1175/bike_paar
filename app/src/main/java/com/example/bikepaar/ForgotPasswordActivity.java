package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Map;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;




import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etNewPassword, etConfirmPassword;
    Button btnChangePassword;
    TextView tvRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        ImageView btnBack = findViewById(R.id.btnBack);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tvRemembered = findViewById(R.id.tvRemembered);

        // Back button
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Change Password button
        ApiService api = ApiClient.getClient().create(ApiService.class);

        btnChangePassword.setOnClickListener(v -> {

            Map<String, String> body = new HashMap<>();
            body.put("username", etUsername.getText().toString().trim());
            body.put("email", etEmail.getText().toString().trim());
            body.put("new_password", etNewPassword.getText().toString().trim());
            body.put("confirm_new_password", etConfirmPassword.getText().toString().trim());

            api.forgotPassword(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Remembered it? Log in
        tvRemembered.setOnClickListener(v -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });
    }
}