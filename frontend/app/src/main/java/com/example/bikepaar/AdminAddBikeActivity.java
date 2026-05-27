package com.example.bikepaar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AdminAddBikeActivity extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (ivSelectedImage != null) {
                        ivSelectedImage.setImageURI(selectedImageUri);
                        ivSelectedImage.setBackgroundResource(0);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        android.graphics.Bitmap imageBitmap = (android.graphics.Bitmap) extras.get("data");
                        if (ivSelectedImage != null) {
                            ivSelectedImage.setImageBitmap(imageBitmap);
                            ivSelectedImage.setBackgroundResource(0);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_bike);

        // Top Bar Back Button
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        // Top Right Drafts Button
        Button btnViewDrafts = findViewById(R.id.btnViewDrafts);
        if (btnViewDrafts != null) {
            btnViewDrafts.setOnClickListener(v -> showDraftsDialog());
        }

        // Image Selection
        CardView cvImageUpload = findViewById(R.id.cvImageUpload);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        if (cvImageUpload != null) {
            cvImageUpload.setOnClickListener(v -> showImageSourceDialog());
        }

        // Access the bottom bar directly and get children safely
        LinearLayout bottomBar = findViewById(R.id.bottomBar);
        if (bottomBar != null && bottomBar.getChildCount() >= 2) {
            LinearLayout saveDraftBtn = (LinearLayout) bottomBar.getChildAt(0);
            LinearLayout publishBtn = (LinearLayout) bottomBar.getChildAt(1);

            saveDraftBtn.setOnClickListener(v -> {
                saveDraft();
            });

            publishBtn.setOnClickListener(v -> {
                publishBike();
            });
        }

        // Initialize Spec Fields with empty values
        setupSpecField(findViewById(R.id.fldEngine), "Engine Displacement", "");
        setupSpecField(findViewById(R.id.fldPower), "Max Power", "");
        setupSpecField(findViewById(R.id.fldTorque), "Max Torque", "");
        setupSpecField(findViewById(R.id.fldTopSpeed), "Top Speed", "");
        setupSpecField(findViewById(R.id.fldMileage), "Mileage", "");
        setupSpecField(findViewById(R.id.fldTransmission), "Transmission", "");

        setupSpecField(findViewById(R.id.fldBrakingSystem), "Braking System", "");
        setupSpecField(findViewById(R.id.fldFrontBrake), "Front Brake", "");
        setupSpecField(findViewById(R.id.fldRearBrake), "Rear Brake", "");
        setupSpecField(findViewById(R.id.fldFrontSuspension), "Front Suspension", "");
        setupSpecField(findViewById(R.id.fldRearSuspension), "Rear Suspension", "");
        setupSpecField(findViewById(R.id.fldTyreType), "Tyre Type", "");
        setupSpecField(findViewById(R.id.fldInstrumentCluster), "Instrument Cluster", "");
        setupSpecField(findViewById(R.id.fldHeadlight), "Headlight", "");
        setupSpecField(findViewById(R.id.fldTailLight), "Tail Light", "");
        setupSpecField(findViewById(R.id.fldBatteryCapacity), "Battery Capacity", "");

        setupSpecField(findViewById(R.id.fldOverallLength), "Overall Length", "");
        setupSpecField(findViewById(R.id.fldOverallWidth), "Overall Width", "");
        setupSpecField(findViewById(R.id.fldWeight), "Kerb Weight", "");
        setupSpecField(findViewById(R.id.fldFuelTank), "Fuel Tank Capacity", "");
        setupSpecField(findViewById(R.id.fldSeatHeight), "Seat Height", "");
        setupSpecField(findViewById(R.id.fldClearance), "Ground Clearance", "");
    }

    private void setupSpecField(View includeView, String label, String value) {
        if (includeView == null) return;
        TextView tvLabel = includeView.findViewById(R.id.tvLabel);
        TextInputEditText etValue = includeView.findViewById(R.id.etValue);

        if (tvLabel != null) tvLabel.setText(label);
        if (etValue != null) etValue.setText(value);
    }

    private String getSpecFieldValue(int viewId) {
        View includeView = findViewById(viewId);
        if (includeView == null) return "";
        TextInputEditText etValue = includeView.findViewById(R.id.etValue);
        return etValue != null && etValue.getText() != null ? etValue.getText().toString() : "";
    }

    private void publishBike() {
        android.widget.EditText etBikeName = findViewById(R.id.etBikeName);
        android.widget.EditText etBrand = findViewById(R.id.etBrand);
        android.widget.EditText etPrice = findViewById(R.id.etPrice);

        String name = etBikeName.getText() != null ? etBikeName.getText().toString() : "";
        String brand = etBrand.getText() != null ? etBrand.getText().toString() : "";
        String price = etPrice.getText() != null ? etPrice.getText().toString() : "";

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("Model", name);
        payload.put("Brand", brand);
        payload.put("Price (₹)", price);

        payload.put("Displacement (cc)", getSpecFieldValue(R.id.fldEngine));
        payload.put("Max Power", getSpecFieldValue(R.id.fldPower));
        payload.put("Max Torque", getSpecFieldValue(R.id.fldTorque));
        payload.put("Top Speed (kmph)", getSpecFieldValue(R.id.fldTopSpeed));
        payload.put("Mileage (kmpl)", getSpecFieldValue(R.id.fldMileage));
        payload.put("Transmission", getSpecFieldValue(R.id.fldTransmission));

        payload.put("Braking System", getSpecFieldValue(R.id.fldBrakingSystem));
        payload.put("Front Brake", getSpecFieldValue(R.id.fldFrontBrake));
        payload.put("Rear Brake", getSpecFieldValue(R.id.fldRearBrake));
        payload.put("Front Suspension", getSpecFieldValue(R.id.fldFrontSuspension));
        payload.put("Rear Suspension", getSpecFieldValue(R.id.fldRearSuspension));
        payload.put("Tyre Type", getSpecFieldValue(R.id.fldTyreType));
        payload.put("Instrument Cluster", getSpecFieldValue(R.id.fldInstrumentCluster));
        payload.put("Headlight", getSpecFieldValue(R.id.fldHeadlight));
        payload.put("Tail Light", getSpecFieldValue(R.id.fldTailLight));
        payload.put("Battery Capacity (Ah)", getSpecFieldValue(R.id.fldBatteryCapacity));

        payload.put("Overall Length (mm)", getSpecFieldValue(R.id.fldOverallLength));
        payload.put("Overall Width (mm)", getSpecFieldValue(R.id.fldOverallWidth));
        payload.put("Kerb Weight (kg)", getSpecFieldValue(R.id.fldWeight));
        payload.put("Fuel Tank Capacity (L)", getSpecFieldValue(R.id.fldFuelTank));
        payload.put("Seat Height (mm)", getSpecFieldValue(R.id.fldSeatHeight));
        payload.put("Ground Clearance (mm)", getSpecFieldValue(R.id.fldClearance));

        SharedPreferences sp = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        String token = sp.getString("TOKEN", "");

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.addBike("Token " + token, payload).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddBikeActivity.this, "Bike Published to Database!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AdminAddBikeActivity.this, "Failed to publish bike", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(AdminAddBikeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            } else if (which == 1) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            }
        });
        builder.show();
    }

    private void saveDraft() {
        android.widget.EditText etBikeName = findViewById(R.id.etBikeName);
        android.widget.EditText etBrand = findViewById(R.id.etBrand);
        android.widget.EditText etPrice = findViewById(R.id.etPrice);

        String name = etBikeName.getText() != null ? etBikeName.getText().toString() : "";
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter at least a Bike Name to save draft", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject draftObj = new JSONObject();
            draftObj.put("Model", name);
            draftObj.put("Brand", etBrand.getText() != null ? etBrand.getText().toString() : "");
            draftObj.put("Price (₹)", etPrice.getText() != null ? etPrice.getText().toString() : "");

            draftObj.put("Displacement (cc)", getSpecFieldValue(R.id.fldEngine));
            draftObj.put("Max Power", getSpecFieldValue(R.id.fldPower));
            draftObj.put("Max Torque", getSpecFieldValue(R.id.fldTorque));
            draftObj.put("Top Speed (kmph)", getSpecFieldValue(R.id.fldTopSpeed));
            draftObj.put("Mileage (kmpl)", getSpecFieldValue(R.id.fldMileage));
            draftObj.put("Transmission", getSpecFieldValue(R.id.fldTransmission));

            draftObj.put("Braking System", getSpecFieldValue(R.id.fldBrakingSystem));
            draftObj.put("Front Brake", getSpecFieldValue(R.id.fldFrontBrake));
            draftObj.put("Rear Brake", getSpecFieldValue(R.id.fldRearBrake));
            draftObj.put("Front Suspension", getSpecFieldValue(R.id.fldFrontSuspension));
            draftObj.put("Rear Suspension", getSpecFieldValue(R.id.fldRearSuspension));
            draftObj.put("Tyre Type", getSpecFieldValue(R.id.fldTyreType));
            draftObj.put("Instrument Cluster", getSpecFieldValue(R.id.fldInstrumentCluster));
            draftObj.put("Headlight", getSpecFieldValue(R.id.fldHeadlight));
            draftObj.put("Tail Light", getSpecFieldValue(R.id.fldTailLight));
            draftObj.put("Battery Capacity (Ah)", getSpecFieldValue(R.id.fldBatteryCapacity));

            draftObj.put("Overall Length (mm)", getSpecFieldValue(R.id.fldOverallLength));
            draftObj.put("Overall Width (mm)", getSpecFieldValue(R.id.fldOverallWidth));
            draftObj.put("Kerb Weight (kg)", getSpecFieldValue(R.id.fldWeight));
            draftObj.put("Fuel Tank Capacity (L)", getSpecFieldValue(R.id.fldFuelTank));
            draftObj.put("Seat Height (mm)", getSpecFieldValue(R.id.fldSeatHeight));
            draftObj.put("Ground Clearance (mm)", getSpecFieldValue(R.id.fldClearance));

            SharedPreferences sp = getSharedPreferences("BIKE_DRAFTS", MODE_PRIVATE);
            String draftsJson = sp.getString("DRAFTS_LIST", "[]");
            JSONArray draftsArray = new JSONArray(draftsJson);
            
            // Check if draft with same model name already exists to replace it
            boolean found = false;
            for(int i=0; i<draftsArray.length(); i++) {
                JSONObject obj = draftsArray.getJSONObject(i);
                if(obj.optString("Model").equalsIgnoreCase(name)) {
                    draftsArray.put(i, draftObj);
                    found = true;
                    break;
                }
            }
            if(!found) {
                draftsArray.put(draftObj);
            }

            sp.edit().putString("DRAFTS_LIST", draftsArray.toString()).apply();
            Toast.makeText(this, "Draft saved successfully", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save draft", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDraftsDialog() {
        SharedPreferences sp = getSharedPreferences("BIKE_DRAFTS", MODE_PRIVATE);
        String draftsJson = sp.getString("DRAFTS_LIST", "[]");
        try {
            JSONArray draftsArray = new JSONArray(draftsJson);
            if(draftsArray.length() == 0) {
                Toast.makeText(this, "No saved drafts found", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> draftNames = new ArrayList<>();
            for(int i=0; i<draftsArray.length(); i++) {
                draftNames.add(draftsArray.getJSONObject(i).optString("Model", "Unknown Draft"));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select a Draft");
            builder.setItems(draftNames.toArray(new String[0]), (dialog, which) -> {
                showDraftActionDialog(draftsArray, which);
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDraftActionDialog(JSONArray draftsArray, int index) {
        try {
            JSONObject selectedDraft = draftsArray.getJSONObject(index);
            String modelName = selectedDraft.optString("Model", "Unknown");

            String[] options = {"Load Draft", "Delete Draft"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(modelName);
            builder.setItems(options, (dialog, which) -> {
                if(which == 0) {
                    loadDraftIntoForm(selectedDraft);
                } else if(which == 1) {
                    deleteDraft(draftsArray, index);
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDraftIntoForm(JSONObject draft) {
        android.widget.EditText etBikeName = findViewById(R.id.etBikeName);
        android.widget.EditText etBrand = findViewById(R.id.etBrand);
        android.widget.EditText etPrice = findViewById(R.id.etPrice);

        etBikeName.setText(draft.optString("Model"));
        etBrand.setText(draft.optString("Brand"));
        etPrice.setText(draft.optString("Price (₹)"));

        setSpecFieldValue(R.id.fldEngine, draft.optString("Displacement (cc)"));
        setSpecFieldValue(R.id.fldPower, draft.optString("Max Power"));
        setSpecFieldValue(R.id.fldTorque, draft.optString("Max Torque"));
        setSpecFieldValue(R.id.fldTopSpeed, draft.optString("Top Speed (kmph)"));
        setSpecFieldValue(R.id.fldMileage, draft.optString("Mileage (kmpl)"));
        setSpecFieldValue(R.id.fldTransmission, draft.optString("Transmission"));

        setSpecFieldValue(R.id.fldBrakingSystem, draft.optString("Braking System"));
        setSpecFieldValue(R.id.fldFrontBrake, draft.optString("Front Brake"));
        setSpecFieldValue(R.id.fldRearBrake, draft.optString("Rear Brake"));
        setSpecFieldValue(R.id.fldFrontSuspension, draft.optString("Front Suspension"));
        setSpecFieldValue(R.id.fldRearSuspension, draft.optString("Rear Suspension"));
        setSpecFieldValue(R.id.fldTyreType, draft.optString("Tyre Type"));
        setSpecFieldValue(R.id.fldInstrumentCluster, draft.optString("Instrument Cluster"));
        setSpecFieldValue(R.id.fldHeadlight, draft.optString("Headlight"));
        setSpecFieldValue(R.id.fldTailLight, draft.optString("Tail Light"));
        setSpecFieldValue(R.id.fldBatteryCapacity, draft.optString("Battery Capacity (Ah)"));

        setSpecFieldValue(R.id.fldOverallLength, draft.optString("Overall Length (mm)"));
        setSpecFieldValue(R.id.fldOverallWidth, draft.optString("Overall Width (mm)"));
        setSpecFieldValue(R.id.fldWeight, draft.optString("Kerb Weight (kg)"));
        setSpecFieldValue(R.id.fldFuelTank, draft.optString("Fuel Tank Capacity (L)"));
        setSpecFieldValue(R.id.fldSeatHeight, draft.optString("Seat Height (mm)"));
        setSpecFieldValue(R.id.fldClearance, draft.optString("Ground Clearance (mm)"));
        
        Toast.makeText(this, "Draft loaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void setSpecFieldValue(int viewId, String value) {
        View includeView = findViewById(viewId);
        if (includeView != null) {
            TextInputEditText etValue = includeView.findViewById(R.id.etValue);
            if (etValue != null) {
                etValue.setText(value);
            }
        }
    }

    private void deleteDraft(JSONArray draftsArray, int index) {
        try {
            draftsArray.remove(index);
            SharedPreferences sp = getSharedPreferences("BIKE_DRAFTS", MODE_PRIVATE);
            sp.edit().putString("DRAFTS_LIST", draftsArray.toString()).apply();
            Toast.makeText(this, "Draft deleted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
