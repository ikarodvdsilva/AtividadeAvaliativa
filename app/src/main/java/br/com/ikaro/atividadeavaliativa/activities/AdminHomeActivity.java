package br.com.ikaro.atividadeavaliativa.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Map;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.fragments.PendingReportsFragment;
import br.com.ikaro.atividadeavaliativa.fragments.ResolvedReportsFragment;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvEmpty;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private TextView tvTotalReports, tvPendingReports, tvInvestigatingReports, tvResolvedReports;
    private LinearLayout typeChartContainer;
    private Button btnViewAllReports;

    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvEmpty = findViewById(R.id.tvEmpty);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tvTotalReports = findViewById(R.id.tvTotalReports);
        tvPendingReports = findViewById(R.id.tvPendingReports);
        tvInvestigatingReports = findViewById(R.id.tvInvestigatingReports);
        tvResolvedReports = findViewById(R.id.tvResolvedReports);
        typeChartContainer = findViewById(R.id.typeChartContainer);
        btnViewAllReports = findViewById(R.id.btnViewAllReports);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        ReportPagerAdapter pagerAdapter = new ReportPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.pending);
                    break;
                case 1:
                    tab.setText(R.string.resolved);
                    break;
            }
        }).attach();

        View headerView = navigationView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvName);
        TextView tvEmail = headerView.findViewById(R.id.tvEmail);

        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());

        loadDashboardData();

        if (btnViewAllReports != null) {
            btnViewAllReports.setOnClickListener(v -> {
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        int totalReports = databaseHelper.getTotalReportCount();
        int pendingReports = databaseHelper.getReportCountByStatus("Pendente");
        int investigatingReports = databaseHelper.getReportCountByStatus("Investigando");
        int resolvedReports = databaseHelper.getReportCountByStatus("Resolvido");

        tvTotalReports.setText(String.valueOf(totalReports));
        tvPendingReports.setText(String.valueOf(pendingReports));
        tvInvestigatingReports.setText(String.valueOf(investigatingReports));
        tvResolvedReports.setText(String.valueOf(resolvedReports));

        setupTypeChart(totalReports);
    }

    private void setupTypeChart(int totalReports) {
        typeChartContainer.removeAllViews();

        Map<String, Integer> typeCounts = databaseHelper.getReportCountsByType();

        int[] colors = {
                Color.parseColor("#4CAF50"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#FF5722"),
                Color.parseColor("#9C27B0")
        };

        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            if (entry.getValue() > 0) {
                View typeView = LayoutInflater.from(this).inflate(
                        R.layout.item_report_type, typeChartContainer, false);

                View colorIndicator = typeView.findViewById(R.id.colorIndicator);
                TextView tvTypeName = typeView.findViewById(R.id.tvTypeName);
                TextView tvTypeCount = typeView.findViewById(R.id.tvTypeCount);
                ProgressBar progressBar = typeView.findViewById(R.id.progressBar);

                int color = colors[colorIndex % colors.length];
                colorIndicator.setBackgroundColor(color);

                tvTypeName.setText(entry.getKey());
                tvTypeCount.setText(String.valueOf(entry.getValue()));

                int percentage = totalReports > 0 ? (entry.getValue() * 100) / totalReports : 0;
                progressBar.setProgress(percentage);

                typeChartContainer.addView(typeView);

                colorIndex++;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            tabLayout.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        } else if (id == R.id.nav_users) {
            Intent intent = new Intent(AdminHomeActivity.this, UserManagementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reports) {
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(AdminHomeActivity.this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            sessionManager.logoutUser();
            Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static class ReportPagerAdapter extends FragmentStateAdapter {
        public ReportPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PendingReportsFragment();
                case 1:
                    return new ResolvedReportsFragment();
                default:
                    return new PendingReportsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
