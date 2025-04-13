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

public class ResolvedReportsFragment extends Fragment implements ReportAdapter.OnReportClickListener {

    private RecyclerView rvReports;
    private TextView tvEmpty;
    private DatabaseHelper databaseHelper;
    private List<Report> resolvedReports;
    private ReportAdapter reportAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        rvReports = view.findViewById(R.id.rvReports);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        databaseHelper = new DatabaseHelper(getContext());
        resolvedReports = new ArrayList<>();

        loadResolvedReports();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadResolvedReports();
    }

    private void loadResolvedReports() {
        List<Report> resolvedList = databaseHelper.getReportsByStatus("Resolvido");
        List<Report> rejectedList = databaseHelper.getReportsByStatus("Rejeitado");

        resolvedReports.clear();
        resolvedReports.addAll(resolvedList);
        resolvedReports.addAll(rejectedList);

        if (resolvedReports.isEmpty()) {
            rvReports.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvReports.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            reportAdapter = new ReportAdapter(resolvedReports, this);
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
