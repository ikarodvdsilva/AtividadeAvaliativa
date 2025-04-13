package br.com.ikaro.atividadeavaliativa.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.adapters.ImageAdapter;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.ReportImage;
import br.com.ikaro.atividadeavaliativa.utils.ImageUtils;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateReportActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_LOCATION_PERMISSION = 102;
    private static final int REQUEST_MAP = 103;

    private Toolbar toolbar;
    private AutoCompleteTextView actType;
    private TextInputEditText etTitle, etDescription;
    private Button btnAddImage, btnSubmit;
    private RecyclerView rvImages;
    private CheckBox cbAnonymous;
    private View mapContainer;
    private TextView tvMapPlaceholder;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private List<ReportImage> reportImages;
    private ImageAdapter imageAdapter;
    private String currentLocation = "-23.550520,-46.633308";
    private double latitude = -23.550520;
    private double longitude = -46.633308;
    private boolean isAnonymous = false;
    private boolean locationSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        reportImages = new ArrayList<>();

        isAnonymous = getIntent().getBooleanExtra("ANONYMOUS", false);

        toolbar = findViewById(R.id.toolbar);
        actType = findViewById(R.id.actType);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        rvImages = findViewById(R.id.rvImages);
        cbAnonymous = findViewById(R.id.cbAnonymous);
        mapContainer = findViewById(R.id.mapContainer);
        tvMapPlaceholder = findViewById(R.id.tvMapPlaceholder);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isAnonymous) {
            cbAnonymous.setChecked(true);
            cbAnonymous.setEnabled(false);
        }

        String[] crimeTypes = {"Desmatamento", "Poluição", "Crime contra Fauna", "Mineração Ilegal", "Pesca Ilegal", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, crimeTypes);
        actType.setAdapter(adapter);

        imageAdapter = new ImageAdapter(reportImages, this);
        rvImages.setAdapter(imageAdapter);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissions();
            }
        });

        mapContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermissions();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            openImagePicker();
        }
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            openMap();
        }
    }

    private void openMap() {
        Intent intent = new Intent(CreateReportActivity.this, MapActivity.class);
        if (locationSelected) {
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
        }
        startActivityForResult(intent, REQUEST_MAP);
    }

    private void openImagePicker() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    private void submitReport() {
        String title = etTitle.getText().toString().trim();
        String type = actType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        boolean anonymous = cbAnonymous.isChecked();

        if (title.isEmpty() || type.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!locationSelected) {
            Toast.makeText(this, "Por favor, selecione uma localização", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = isAnonymous ? 0 : sessionManager.getUserId();

        currentLocation = latitude + "," + longitude;

        long reportId = databaseHelper.addReport(title, type, description, currentLocation, userId, anonymous, "Pendente");

        if (reportId != -1) {
            for (ReportImage image : reportImages) {
                databaseHelper.addReportImage(reportId, image.getImagePath());
            }

            Toast.makeText(this, "Denúncia enviada com sucesso", Toast.LENGTH_SHORT).show();

            if (isAnonymous) {
                Intent intent = new Intent(CreateReportActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Falha ao enviar denúncia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permissão da câmera é necessária para tirar fotos", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMap();
            } else {
                Toast.makeText(this, "Permissão de localização é necessária para reportar locais precisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                String imagePath = ImageUtils.saveBitmapToFile(this, imageBitmap);
                if (imagePath != null) {
                    ReportImage reportImage = new ReportImage(reportImages.size() + 1, 0, imagePath);
                    reportImages.add(reportImage);
                    imageAdapter.notifyDataSetChanged();
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    String imagePath = ImageUtils.saveBitmapToFile(this, bitmap);
                    if (imagePath != null) {
                        ReportImage reportImage = new ReportImage(reportImages.size() + 1, 0, imagePath);
                        reportImages.add(reportImage);
                        imageAdapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_MAP && data != null) {
                latitude = data.getDoubleExtra("latitude", 0);
                longitude = data.getDoubleExtra("longitude", 0);
                locationSelected = true;
                tvMapPlaceholder.setText(R.string.location_selected);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
