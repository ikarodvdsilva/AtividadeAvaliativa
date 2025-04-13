package br.com.ikaro.atividadeavaliativa.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.adapters.UserAdapter;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText etSearch;
    private RecyclerView rvUsers;
    private TextView tvEmpty;

    private DatabaseHelper databaseHelper;
    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        databaseHelper = new DatabaseHelper(this);
        userList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        etSearch = findViewById(R.id.etSearch);
        rvUsers = findViewById(R.id.rvUsers);
        tvEmpty = findViewById(R.id.tvEmpty);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUsers();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadUsers() {
        userList = databaseHelper.getAllUsers();

        if (userList.isEmpty()) {
            rvUsers.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvUsers.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            userAdapter = new UserAdapter(userList);
            rvUsers.setAdapter(userAdapter);
        }
    }

    private void filterUsers(String query) {
        List<User> filteredList = new ArrayList<>();

        for (User user : userList) {
            if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        if (filteredList.isEmpty()) {
            rvUsers.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvUsers.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            userAdapter = new UserAdapter(filteredList);
            rvUsers.setAdapter(userAdapter);
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

