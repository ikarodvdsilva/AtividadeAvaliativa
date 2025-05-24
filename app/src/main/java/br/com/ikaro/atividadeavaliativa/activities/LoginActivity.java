package br.com.ikaro.atividadeavaliativa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.models.User;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;
import br.com.ikaro.atividadeavaliativa.viewmodel.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvAnonymous;
    private MainViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvAnonymous = findViewById(R.id.tvAnonymous);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvAnonymous.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateReportActivity.class);
            intent.putExtra("ANONYMOUS", true);
            startActivity(intent);
        });

        // Observar mudanças no usuário atual
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                if (user.isAdmin()) {
                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

        // Observar erros
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(email, password);
    }
}

