package br.com.ikaro.atividadeavaliativa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.activities.ReportDetailsActivity;
import br.com.ikaro.atividadeavaliativa.adapters.ReportAdapter;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.Report;

import java.util.ArrayList;
import java.util.List;

public class PendingReportsFragment extends Fragment implements ReportAdapter.OnReportClickListener {

    private RecyclerView rvReports;
    private TextView tvEmpty;
    private DatabaseHelper databaseHelper;
    private List<Report> pendingReports;
    private ReportAdapter reportAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        rvReports = view.findViewById(R.id.rvReports);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        databaseHelper = new DatabaseHelper(getContext());
        pendingReports = new ArrayList<>();

        loadPendingReports();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPendingReports();
    }

    private void loadPendingReports() {
        List<Report> pendingList = databaseHelper.getReportsByStatus("Pendente");
        List<Report> investigatingList = databaseHelper.getReportsByStatus("Investigando");

        pendingReports.clear();
        pendingReports.addAll(pendingList);
        pendingReports.addAll(investigatingList);

        if (pendingReports.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            reportAdapter = new ReportAdapter(pendingReports, this);
            rvReports.setAdapter(reportAdapter);
        }
    }

    @Override
    public void onReportClick(Report report) {
        Intent intent = new Intent(getActivity(), ReportDetailsActivity.class);
        intent.putExtra("REPORT_ID", report.getId());
        intent.putExtra("IS_ADMIN", true);
        startActivity(intent);
    }
}
