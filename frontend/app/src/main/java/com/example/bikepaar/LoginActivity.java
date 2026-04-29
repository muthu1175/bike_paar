package com.example.bikepaar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnSignIn;
    TextView btnToggleLogin, btnToggleSignup, tvForgot;
    View selector;
    ImageView ivPasswordToggle;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnToggleLogin = findViewById(R.id.btnToggleLogin);
        btnToggleSignup = findViewById(R.id.btnToggleSignup);
        tvForgot = findViewById(R.id.tvForgot);
        selector = findViewById(R.id.selector);
        progressBar = findViewById(R.id.progressBar);
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);

        selectLogin();
        handleIntent(getIntent());

        ApiService api = ApiClient.getClient().create(ApiService.class);

        ivPasswordToggle.setOnClickListener(v -> {
            if (etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
                return;
            }

            // SHOW LOADING
            btnSignIn.setEnabled(false);
            btnSignIn.setText("");
            progressBar.setVisibility(View.VISIBLE);

            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", pass);

            api.login(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call,
                                       Response<Map<String, String>> response) {
                    
                    // HIDE LOADING
                    btnSignIn.setEnabled(true);
                    btnSignIn.setText("sign in");
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {

                        String username = response.body().get("username");
                        String userEmail = response.body().get("email");
                        String token = response.body().get("token");
                        String profileImage = response.body().get("profile_image");

                        // ✅ SharedPreferences MUST be inside this block
                        SharedPreferences sp =
                                getSharedPreferences("USER_DATA", MODE_PRIVATE);

                        // Clear previous user data to prevent isolation leakage
                        sp.edit().clear().apply();

                        sp.edit()
                                .putString("TOKEN", token)
                                .putString("username", username)
                                .putString("email", userEmail)
                                .putString("profile_image", profileImage) // Save new image
                                .putBoolean("LOGGED_IN", true)
                                .apply();

                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Invalid login", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    // HIDE LOADING
                    btnSignIn.setEnabled(true);
                    btnSignIn.setText("sign in");
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(LoginActivity.this,
                            t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        });

        btnToggleSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );

        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("email")) {
            String email = intent.getStringExtra("email");
            if (!TextUtils.isEmpty(email)) {
                etEmail.setText(email);
                etPassword.requestFocus();
            }
        }
    }

    private void selectLogin() {
        btnToggleLogin.setTextColor(0xFFFFFFFF);
        btnToggleSignup.setTextColor(0xFFA0A0A0);
    }
}
