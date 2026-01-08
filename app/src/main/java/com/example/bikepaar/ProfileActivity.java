package com.example.bikepaar;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.bumptech.glide.Glide;


public class ProfileActivity extends AppCompatActivity {

    private static final int REQ_PICK_IMAGE = 101;
    private static final int REQ_PERMISSION = 102;
    private static final int REQ_EDIT_PROFILE = 100;

    private TextView tvUserName, tvEmail;
    private ImageView imgProfile, aiButton;
    private BottomNavigationView bottomNavigationView;

    // 🔥 SIMPLE URI → FILE PATH CONVERTER
    private String getRealPathFromUri(Uri uri) {
        String path = null;
        String[] proj = { android.provider.MediaStore.Images.Media.DATA };

        android.database.Cursor cursor =
                getContentResolver().query(uri, proj, null, null, null);

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        imgProfile = findViewById(R.id.imgProfile);
        aiButton = findViewById(R.id.aiButton);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        ImageView ivMenu = findViewById(R.id.ivMenu);
        ImageView ivBell = findViewById(R.id.ivBell);
        ImageView btnCamera = findViewById(R.id.btnCamera);
        View avatarContainer = findViewById(R.id.avatarContainer);
//        ImageView btnEditPersonal = findViewById(R.id.btnEditPersonal);
        View rowSettings = findViewById(R.id.rowSettings);

        loadUserData();

        ivMenu.setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));

        ivBell.setOnClickListener(v ->
                Toast.makeText(this, "No Notifications", Toast.LENGTH_SHORT).show());

        View.OnClickListener avatarClick = v -> openImagePicker();
        avatarContainer.setOnClickListener(avatarClick);
        btnCamera.setOnClickListener(avatarClick);
        imgProfile.setOnClickListener(avatarClick);

//        btnEditPersonal.setOnClickListener(v -> openEditPersonalActivity());

        rowSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        aiButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AiQuestionActivity.class);
            i.putExtra("step", 1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_search) {
                Intent i = new Intent(ProfileActivity.this, SearchActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }  else if (id == R.id.nav_fav) {
                Intent i = new Intent(ProfileActivity.this, FavouriteActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String username = sp.getString("username", null);
        String email = sp.getString("email", null);
        String imageUrl = sp.getString("profile_image", null); // 🔥

        if (username != null) tvUserName.setText(username);
        if (email != null) tvEmail.setText(email);

        // 🔥 PROFILE IMAGE LOAD (HERE ONLY)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.bikepaar_logo)
                    .into(imgProfile);
        }
    }

    private void openEditPersonalActivity() {
        Intent intent = new Intent(this, EditPersonalActivity.class);
        intent.putExtra("username", tvUserName.getText().toString());
        intent.putExtra("email", tvEmail.getText().toString());
        startActivityForResult(intent, REQ_EDIT_PROFILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                imgProfile.setImageURI(uri);
                uploadProfileImage(uri); // 🔥 NEW
            }
        }
    }

    // 🔥 NEW: IMAGE UPLOAD (ISOLATED METHOD)
    private void uploadProfileImage(Uri uri) {

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        if (token.isEmpty()) return;

        File file = new File(getRealPathFromUri(uri));
        RequestBody reqFile =
                RequestBody.create(MediaType.parse("image/*"), file);


        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.uploadProfileImage("Token " + token, body)
                .enqueue(new Callback<Map<String, String>>() {

                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                                           Response<Map<String, String>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            String imageUrl = response.body().get("image_url");

                            sp.edit().putString("profile_image", imageUrl).apply();

                            Toast.makeText(ProfileActivity.this,
                                    "Profile image saved", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQ_PERMISSION);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_PERMISSION);
                return;
            }
        }

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, REQ_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        }
    }
}
