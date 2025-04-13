package br.com.ikaro.atividadeavaliativa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.adapters.ReportAdapter;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.Report;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UserHomeActivity extends AppCompatActivity implements ReportAdapter.OnReportClickListener {

    private Toolbar toolbar;
    private RecyclerView rvReports;
    private TextView tvEmpty;
    private FloatingActionButton fabAddReport;
    private SwipeRefreshLayout swipeRefresh;
    private DatabaseHelper databaseHelper;
    private ReportAdapter reportAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        toolbar = findViewById(R.id.toolbar);
        rvReports = findViewById(R.id.rvReports);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddReport = findViewById(R.id.fabAddReport);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        setSupportActionBar(toolbar);

        fabAddReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomeActivity.this, CreateReportActivity.class);
                startActivity(intent);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadReports();
                swipeRefresh.setRefreshing(false);
            }
        });

        loadReports();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    private void loadReports() {
        int userId = sessionManager.getUserId();
        List<Report> reports = databaseHelper.getUserReports(userId);

        if (reports.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            reportAdapter = new ReportAdapter(reports, this);
            rvReports.setAdapter(reportAdapter);
        }
    }

    @Override
    public void onReportClick(Report report) {
        Intent intent = new Intent(UserHomeActivity.this, ReportDetailsActivity.class);
        intent.putExtra("REPORT_ID", report.getId());
        intent.putExtra("IS_ADMIN", false);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            sessionManager.logoutUser();
            Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

