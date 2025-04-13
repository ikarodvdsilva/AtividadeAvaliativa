package br.com.ikaro.atividadeavaliativa.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout statusChartContainer;
    private LinearLayout typeChartContainer;
    private TableLayout timeTable;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        databaseHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar);
        statusChartContainer = findViewById(R.id.statusChartContainer);
        typeChartContainer = findViewById(R.id.typeChartContainer);
        timeTable = findViewById(R.id.timeTable);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupStatusChart();
        setupTypeChart();
        setupTimeChart();
    }

    private void setupStatusChart() {
        statusChartContainer.removeAllViews();

        Map<String, Integer> statusCounts = databaseHelper.getReportCountsByStatus();
        int totalReports = databaseHelper.getTotalReportCount();

        Map<String, Integer> statusColors = new HashMap<>();
        statusColors.put("Pendente", Color.parseColor("#FFC107"));
        statusColors.put("Investigando", Color.parseColor("#2196F3"));
        statusColors.put("Resolvido", Color.parseColor("#4CAF50"));
        statusColors.put("Rejeitado", Color.parseColor("#F44336"));

        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            if (entry.getValue() > 0) {
                View statusView = LayoutInflater.from(this).inflate(
                        R.layout.item_report_type, statusChartContainer, false);

                View colorIndicator = statusView.findViewById(R.id.colorIndicator);
                TextView tvTypeName = statusView.findViewById(R.id.tvTypeName);
                TextView tvTypeCount = statusView.findViewById(R.id.tvTypeCount);
                ProgressBar progressBar = statusView.findViewById(R.id.progressBar);

                int color = statusColors.containsKey(entry.getKey()) ?
                        statusColors.get(entry.getKey()) : Color.GRAY;
                colorIndicator.setBackgroundColor(color);

                tvTypeName.setText(entry.getKey());
                tvTypeCount.setText(String.valueOf(entry.getValue()));

                int percentage = totalReports > 0 ? (entry.getValue() * 100) / totalReports : 0;
                progressBar.setProgress(percentage);

                statusChartContainer.addView(statusView);
            }
        }
    }

    private void setupTypeChart() {
        typeChartContainer.removeAllViews();

        Map<String, Integer> typeCounts = databaseHelper.getReportCountsByType();
        int totalReports = databaseHelper.getTotalReportCount();
        int[] colors = {
                Color.parseColor("#4CAF50"),  // Verde
                Color.parseColor("#2196F3"),  // Azul
                Color.parseColor("#FFC107"),  // Amarelo
                Color.parseColor("#FF5722"),  // Laranja
                Color.parseColor("#9C27B0")   // Roxo
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

    private void setupTimeChart() {
        int childCount = timeTable.getChildCount();
        if (childCount > 1) {
            timeTable.removeViews(1, childCount - 1);
        }

        String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho"};
        int[] counts = {3, 5, 7, 4, 6, 8};

        for (int i = 0; i < months.length; i++) {
            TableRow row = new TableRow(this);
            row.setPadding(8, 8, 8, 8);

            if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F5F5F5"));
            }

            TextView tvMonth = new TextView(this);
            tvMonth.setText(months[i]);
            tvMonth.setTextColor(Color.parseColor("#333333"));
            tvMonth.setGravity(android.view.Gravity.CENTER);
            row.addView(tvMonth);

            TextView tvCount = new TextView(this);
            tvCount.setText(String.valueOf(counts[i]));
            tvCount.setTextColor(Color.parseColor("#333333"));
            tvCount.setGravity(android.view.Gravity.CENTER);
            row.addView(tvCount);

            timeTable.addView(row);
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
