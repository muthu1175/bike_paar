package com.example.bikepaar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class AdminSendNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_send_notification);

        // Top bar back button
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Target Audience Toggles
        TextView btnAudienceAll = findViewById(R.id.btnAudienceAll);
        TextView btnAudienceActive = findViewById(R.id.btnAudienceActive);

        btnAudienceAll.setOnClickListener(v -> {
            btnAudienceAll.setBackgroundResource(R.drawable.rounded_toggle_selected);
            btnAudienceAll.setTextColor(0xFFFFFFFF); // White
            
            btnAudienceActive.setBackground(null);
            btnAudienceActive.setTextColor(0xFF604030); // Dark Brown
        });

        btnAudienceActive.setOnClickListener(v -> {
            btnAudienceActive.setBackgroundResource(R.drawable.rounded_toggle_selected);
            btnAudienceActive.setTextColor(0xFFFFFFFF); // White
            
            btnAudienceAll.setBackground(null);
            btnAudienceAll.setTextColor(0xFF604030); // Dark Brown
        });

        // Live Preview update
        EditText etTitle = findViewById(R.id.etNotificationTitle);
        EditText etMessage = findViewById(R.id.etNotificationMessage);
        TextView tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        TextView tvPreviewMessage = findViewById(R.id.tvPreviewMessage);

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    tvPreviewTitle.setText("Sample Notification");
                } else {
                    tvPreviewTitle.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    tvPreviewMessage.setText("Users will receive this alert as a push message on their mobile lock screen.");
                } else {
                    tvPreviewMessage.setText(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Send Button
        Button btnSendNow = findViewById(R.id.btnSendNow);
        btnSendNow.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            
            if(title.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please enter both Title and Message", Toast.LENGTH_SHORT).show();
                return;
            }
            
            btnSendNow.setEnabled(false);
            btnSendNow.setText("Sending...");

            RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
            RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"), message);

            ApiService api = ApiClient.getClient().create(ApiService.class);
            api.sendAdminNotification(titleBody, messageBody, null).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    btnSendNow.setEnabled(true);
                    btnSendNow.setText("Send Now");
                    
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminSendNotificationActivity.this, "Notification sent to users successfully!", Toast.LENGTH_LONG).show();
                        etTitle.setText("");
                        etMessage.setText("");
                    } else {
                        Toast.makeText(AdminSendNotificationActivity.this, "Error sending notification", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    btnSendNow.setEnabled(true);
                    btnSendNow.setText("Send Now");
                    Toast.makeText(AdminSendNotificationActivity.this, "Network error: Failed to send", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
