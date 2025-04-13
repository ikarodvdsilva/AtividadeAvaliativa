package br.com.ikaro.atividadeavaliativa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import br.com.ikaro.atividadeavaliativa.models.User;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvAnonymous;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        setupMockData();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvAnonymous = findViewById(R.id.tvAnonymous);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CreateReportActivity.class);
                intent.putExtra("ANONYMOUS", true);
                startActivity(intent);
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

        User user = databaseHelper.getUser(email, password);

        if (user != null) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.createLoginSession(user.getId(), user.getName(), user.getEmail(), user.isAdmin());

            if (user.isAdmin()) {
                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                startActivity(intent);
            }
            finish();
        } else {
            Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMockData() {
        if (!databaseHelper.userExists("admin@exemplo.com")) {
            databaseHelper.addUser("Usuário Administrador", "12345678900", "admin@exemplo.com",
                    "123456789", "senha123", true);
            databaseHelper.addUser("Usuário Comum", "98765432100", "usuario@exemplo.com",
                    "987654321", "senha123", false);
            databaseHelper.addReport("Desmatamento Ilegal", "Desmatamento",
                    "Grande área sendo desmatada ilegalmente próximo aos limites da cidade.",
                    "-23.550520,-46.633308", 2, false, "Pendente");
            databaseHelper.addReport("Poluição da Água", "Poluição",
                    "Resíduos industriais sendo despejados no rio.",
                    "-23.557520,-46.639308", 2, false, "Investigando");
            databaseHelper.addReport("Tráfico de Animais Silvestres", "Crime contra Fauna",
                    "Avistados pássaros raros sendo vendidos no mercado local.",
                    "-23.540520,-46.623308", 2, true, "Pendente");
        }
    }
}

