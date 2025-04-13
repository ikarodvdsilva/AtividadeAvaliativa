package br.com.ikaro.atividadeavaliativa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.models.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reports;
    private OnReportClickListener listener;

    public interface OnReportClickListener {
        void onReportClick(Report report);
    }

    public ReportAdapter(List<Report> reports, OnReportClickListener listener) {
        this.reports = reports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.bind(report, listener);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvType, tvDate, tvStatus;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvType = itemView.findViewById(R.id.tvType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(final Report report, final OnReportClickListener listener) {
            tvTitle.setText(report.getTitle());
            tvType.setText(report.getType());
            tvDate.setText(report.getCreatedAt());
            tvStatus.setText(report.getStatus());

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReportClick(report);
                }
            });
        }
    }
}

