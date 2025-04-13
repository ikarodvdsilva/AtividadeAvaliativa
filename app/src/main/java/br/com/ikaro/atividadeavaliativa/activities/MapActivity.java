package br.com.ikaro.atividadeavaliativa.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import br.com.ikaro.atividadeavaliativa.R;

public class MapActivity extends AppCompatActivity implements LocationListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private LocationManager locationManager;
    private GeoPoint selectedLocation;
    private Button btnConfirm;
    private Toolbar toolbar;
    private Marker marker;
    private boolean viewOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.select_location);

        btnConfirm = findViewById(R.id.btnConfirm);
        mapView = findViewById(R.id.map);

        viewOnly = getIntent().getBooleanExtra("viewOnly", false);
        if (viewOnly) {
            btnConfirm.setVisibility(View.GONE);
            getSupportActionBar().setTitle(R.string.view_location);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLocation != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("latitude", selectedLocation.getLatitude());
                    resultIntent.putExtra("longitude", selectedLocation.getLongitude());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(MapActivity.this, R.string.please_select_location, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (!viewOnly) {
                    selectedLocation = p;
                    updateMarker();
                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(eventsOverlay);

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            double lat = getIntent().getDoubleExtra("latitude", 0);
            double lng = getIntent().getDoubleExtra("longitude", 0);
            selectedLocation = new GeoPoint(lat, lng);
            mapView.getController().setCenter(selectedLocation);
            updateMarker();
        } else {
            checkLocationPermission();
        }
    }

    private void updateMarker() {
        if (marker != null) {
            mapView.getOverlays().remove(marker);
        }

        marker = new Marker(mapView);
        marker.setPosition(selectedLocation);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(getString(R.string.selected_location));
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null && selectedLocation == null) {
            selectedLocation = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mapView.getController().setCenter(selectedLocation);
            updateMarker();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (selectedLocation == null) {
            selectedLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapView.getController().setCenter(selectedLocation);
            updateMarker();

            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}
