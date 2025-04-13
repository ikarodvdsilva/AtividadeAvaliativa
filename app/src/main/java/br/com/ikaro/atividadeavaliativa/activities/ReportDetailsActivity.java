package br.com.ikaro.atividadeavaliativa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.adapters.ImageAdapter;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.Report;
import br.com.ikaro.atividadeavaliativa.models.ReportImage;
import br.com.ikaro.atividadeavaliativa.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ReportDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle, tvType, tvStatus, tvDate, tvDescription, tvReporter, tvReporterContact;
    private RecyclerView rvImages;
    private LinearLayout adminActionsContainer;
    private RadioGroup rgStatus;
    private RadioButton rbPending, rbInvestigating, rbResolved, rbRejected;
    private TextInputEditText etAdminNotes;
    private Button btnUpdateStatus;
    private FrameLayout mapContainer;
    private MapView mapView;

    private DatabaseHelper databaseHelper;
    private Report report;
    private boolean isAdmin;
    private int reportId;
    private double latitude = 0;
    private double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_report_details);

        databaseHelper = new DatabaseHelper(this);

        reportId = getIntent().getIntExtra("REPORT_ID", 0);
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        tvReporter = findViewById(R.id.tvReporter);
        tvReporterContact = findViewById(R.id.tvReporterContact);
        rvImages = findViewById(R.id.rvImages);
        adminActionsContainer = findViewById(R.id.adminActionsContainer);
        mapContainer = findViewById(R.id.mapContainer);
        rgStatus = findViewById(R.id.rgStatus);
        rbPending = findViewById(R.id.rbPending);
        rbInvestigating = findViewById(R.id.rbInvestigating);
        rbResolved = findViewById(R.id.rbResolved);
        rbRejected = findViewById(R.id.rbRejected);
        etAdminNotes = findViewById(R.id.etAdminNotes);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadReportData();

        if (isAdmin) {
            adminActionsContainer.setVisibility(View.VISIBLE);
            setupAdminActions();
        } else {
            adminActionsContainer.setVisibility(View.GONE);
        }

        setupMap();

        mapContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportDetailsActivity.this, MapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("viewOnly", true);
                startActivity(intent);
            }
        });
    }

    private void setupMap() {
        mapView = new MapView(this);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mapView.setLayoutParams(params);

        mapContainer.removeAllViews();

        mapContainer.addView(mapView);

        mapView.onResume();
    }

    private void loadReportData() {
        report = databaseHelper.getReportById(reportId);

        if (report != null) {
            tvTitle.setText(report.getTitle());
            tvType.setText(report.getType());
            tvStatus.setText(report.getStatus());
            tvDate.setText(report.getCreatedAt());
            tvDescription.setText(report.getDescription());

            String[] locationParts = report.getLocation().split(",");
            if (locationParts.length == 2) {
                try {
                    latitude = Double.parseDouble(locationParts[0]);
                    longitude = Double.parseDouble(locationParts[1]);

                    mapContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mapView != null && latitude != 0 && longitude != 0) {
                                GeoPoint reportLocation = new GeoPoint(latitude, longitude);
                                mapView.getController().setCenter(reportLocation);

                                mapView.getOverlays().clear();

                                Marker marker = new Marker(mapView);
                                marker.setPosition(reportLocation);
                                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                marker.setTitle(getString(R.string.report_location));
                                mapView.getOverlays().add(marker);
                                mapView.invalidate();
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            switch (report.getStatus()) {
                case "Pendente":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "Investigando":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_investigating);
                    break;
                case "Resolvido":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
                    break;
                case "Rejeitado":
                    tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                    break;
            }

            if (report.isAnonymous()) {
                tvReporter.setText(R.string.anonymous_reporter);
                tvReporterContact.setVisibility(View.GONE);
            } else {
                User reporter = databaseHelper.getUserById(report.getUserId());
                if (reporter != null) {
                    tvReporter.setText(reporter.getName());
                    tvReporterContact.setText(reporter.getEmail() + " • " + reporter.getPhone());
                    tvReporterContact.setVisibility(View.VISIBLE);
                }
            }

            List<ReportImage> images = databaseHelper.getReportImages(reportId);
            if (!images.isEmpty()) {
                ImageAdapter imageAdapter = new ImageAdapter(images, this);
                rvImages.setAdapter(imageAdapter);
            }
        }
    }

    private void setupAdminActions() {
        switch (report.getStatus()) {
            case "Pendente":
                rbPending.setChecked(true);
                break;
            case "Investigando":
                rbInvestigating.setChecked(true);
                break;
            case "Resolvido":
                rbResolved.setChecked(true);
                break;
            case "Rejeitado":
                rbRejected.setChecked(true);
                break;
        }

        if (report.getAdminNotes() != null && !report.getAdminNotes().isEmpty()) {
            etAdminNotes.setText(report.getAdminNotes());
        }

        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReportStatus();
            }
        });
    }

    private void updateReportStatus() {
        String status = "";
        int selectedId = rgStatus.getCheckedRadioButtonId();

        if (selectedId == R.id.rbPending) {
            status = "Pendente";
        } else if (selectedId == R.id.rbInvestigating) {
            status = "Investigando";
        } else if (selectedId == R.id.rbResolved) {
            status = "Resolvido";
        } else if (selectedId == R.id.rbRejected) {
            status = "Rejeitado";
        }

        String adminNotes = etAdminNotes.getText().toString().trim();

        boolean success = databaseHelper.updateReportStatus(reportId, status, adminNotes);

        if (success) {
            Toast.makeText(this, "Status da denúncia atualizado", Toast.LENGTH_SHORT).show();
            loadReportData();
        } else {
            Toast.makeText(this, "Falha ao atualizar status", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
