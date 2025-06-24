package br.com.ikaro.atividadeavaliativa.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import br.com.ikaro.atividadeavaliativa.models.*;
import br.com.ikaro.atividadeavaliativa.repository.ApiRepository;
import br.com.ikaro.atividadeavaliativa.utils.Result;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final ApiRepository repository;
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<Report>> reports = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    public MainViewModel(Application application) {
        super(application);
        repository = new ApiRepository(application);
    }
    
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    public LiveData<List<Report>> getReports() {
        return reports;
    }
    
    public LiveData<String> getError() {
        return error;
    }
    
    public void login(String email, String password) {
        repository.login(email, password).observeForever(result -> {
            if (result.isSuccess()) {
                currentUser.setValue(result.getData());
                error.setValue(null);
            } else {
                error.setValue(result.getError().getMessage());
            }
        });
    }
    
    public LiveData<Result<User>> register(String name, String cpf, String email, String phone, String password) {
        return repository.register(name, cpf, email, phone, password);
    }
    
    public void loadReports() {
        repository.getReports().observeForever(result -> {
            if (result.isSuccess()) {
                reports.setValue(result.getData());
                error.setValue(null);
            } else {
                error.setValue(result.getError().getMessage());
            }
        });
    }
    
    public void createReport(Report report) {
        repository.createReport(report).observeForever(result -> {
            if (result.isSuccess()) {
                loadReports(); // Recarrega a lista de relatórios
                error.setValue(null);
            } else {
                error.setValue(result.getError().getMessage());
            }
        });
    }
    
    public LiveData<Result<ReportImage>> uploadImage(int reportId, Uri imageUri) {
        MutableLiveData<Result<ReportImage>> result = new MutableLiveData<>();
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
            File tempFile = File.createTempFile("image", ".jpg", getApplication().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            outputStream.close();
            inputStream.close();
            
            repository.uploadImage(reportId, tempFile).observeForever(uploadResult -> {
                tempFile.delete();
                result.setValue(uploadResult);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error uploading image", e);
            result.setValue(Result.error(e));
        }
        return result;
    }
    
    public void logout() {
        repository.logout();
        currentUser.setValue(null);
    }
} 