package com.example.bikepaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditPersonalActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editConfirmEmail;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal);

        // Prevent keyboard from pushing up bottom navigation
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editConfirmEmail = findViewById(R.id.editConfirmEmail);

        // Set current values from profile
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String email = intent.getStringExtra("email");

            if (username != null) editUsername.setText(username);
            if (email != null) {
                editEmail.setText(email);
                editConfirmEmail.setText(email);
            }
        }

        // Save button click
        findViewById(R.id.btnSave).setOnClickListener(v -> saveChanges());

        // Back button click
        findViewById(R.id.ivBack).setOnClickListener(v -> onBackPressed());


        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent i = new Intent(EditPersonalActivity.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_search) {
                Intent i = new Intent(EditPersonalActivity.this, SearchActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_fav) {
                Intent i = new Intent(EditPersonalActivity.this, FavouriteActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(EditPersonalActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        // AI button click
        findViewById(R.id.aiButton).setOnClickListener(v -> {
            Intent i = new Intent(EditPersonalActivity.this, AiQuestionActivity.class);
            i.putExtra("step", 1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void saveChanges() {
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String confirmEmail = editConfirmEmail.getText().toString().trim();

        // Validation
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.equals(confirmEmail)) {
            Toast.makeText(this, "Emails do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database (to be implemented with backend)
        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();

        // Pass data back to ProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("email", email);
        setResult(RESULT_OK, resultIntent);

        // Go back
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}