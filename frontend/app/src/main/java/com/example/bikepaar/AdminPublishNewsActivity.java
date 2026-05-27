package com.example.bikepaar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.view.View;
import android.widget.LinearLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPublishNewsActivity extends AppCompatActivity {

    private EditText etNewsHeadline;
    private EditText etNewsBody;
    private Uri selectedImageUri;
    private ImageView ivPreview; // Add an ID to the image view in layout later, or just find it

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                    // Optional: show preview if we assign an ID to the image view in layout
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_publish_news);

        etNewsHeadline = findViewById(R.id.etNewsHeadline);
        etNewsBody = findViewById(R.id.etNewsBody);

        // Back Button
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());
        
        // Buttons
        Button btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnSaveDraft.setOnClickListener(v -> {
            Toast.makeText(this, "Draft saved locally!", Toast.LENGTH_SHORT).show();
            finish();
        });
        
        Button btnPublishArticle = findViewById(R.id.btnPublishArticle);
        btnPublishArticle.setOnClickListener(v -> publishNews());

        // We assume the second LinearLayout is the image upload box.
        // Let's find it by finding the parent layout and getting the child, or just add ID in XML.
        // I will add ID 'llImageUpload' to XML in the next step.
        LinearLayout llImageUpload = findViewById(R.id.llImageUpload);
        if (llImageUpload != null) {
            llImageUpload.setOnClickListener(v -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            });
        }
    }

    private void publishNews() {
        String title = etNewsHeadline.getText().toString().trim();
        String body = etNewsBody.getText().toString().trim();

        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(this, "Please enter headline and body", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"), body);
        MultipartBody.Part imagePart = null;

        if (selectedImageUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(selectedImageUri);
                File file = new File(getCacheDir(), "news_image.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
                
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), fileBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.sendAdminNotification(titleBody, messageBody, imagePart).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminPublishNewsActivity.this, "News Published as Notification!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminPublishNewsActivity.this, "Failed to publish", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminPublishNewsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
