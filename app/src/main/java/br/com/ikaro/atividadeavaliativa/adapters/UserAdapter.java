package br.com.ikaro.atividadeavaliativa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvUserType;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvUserType = itemView.findViewById(R.id.tvUserType);
        }

        void bind(User user) {
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvPhone.setText(user.getPhone());
            tvUserType.setText(user.isAdmin() ? "Administrador" : "Usuário Comum");

            if (user.isAdmin()) {
                tvUserType.setBackgroundResource(R.drawable.bg_admin_tag);
            } else {
                tvUserType.setBackgroundResource(R.drawable.bg_user_tag);
            }
        }
    }
}

