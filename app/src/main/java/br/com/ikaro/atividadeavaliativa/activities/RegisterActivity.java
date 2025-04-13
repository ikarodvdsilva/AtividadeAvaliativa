package br.com.ikaro.atividadeavaliativa.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.com.ikaro.atividadeavaliativa.R;
import br.com.ikaro.atividadeavaliativa.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etName, etCpf, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        ivBack = findViewById(R.id.ivBack);
        etName = findViewById(R.id.etName);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String cpf = etCpf.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || cpf.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.userExists(email)) {
            Toast.makeText(this, "Email já está em uso", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.addUser(name, cpf, email, phone, password, false);

        if (success) {
            Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Falha no cadastro", Toast.LENGTH_SHORT).show();
        }
    }
}

