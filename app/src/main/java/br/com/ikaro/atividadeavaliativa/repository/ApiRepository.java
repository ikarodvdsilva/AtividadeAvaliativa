package br.com.ikaro.atividadeavaliativa.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import br.com.ikaro.atividadeavaliativa.api.ApiClient;
import br.com.ikaro.atividadeavaliativa.models.*;
import br.com.ikaro.atividadeavaliativa.utils.Result;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class ApiRepository {
    private static final String TAG = "ApiRepository";
    private final ApiClient apiClient;
    private final MutableLiveData<Result<User>> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Result<List<Report>>> reports = new MutableLiveData<>();
    private final MutableLiveData<Result<Report>> currentReport = new MutableLiveData<>();
    private final MutableLiveData<Result<Statistics>> statistics = new MutableLiveData<>();
    
    public ApiRepository(Context context) {
        apiClient = ApiClient.getInstance(context);
    }
    
    public LiveData<Result<User>> login(String email, String password) {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();
        apiClient.login(email, password, new ApiClient.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                result.setValue(Result.success(user));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public LiveData<Result<User>> register(String name, String cpf, String email, String phone, String password) {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();
        apiClient.register(name, cpf, email, phone, password, new ApiClient.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                result.setValue(Result.success(user));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public LiveData<Result<User>> getCurrentUser() {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();
        apiClient.getCurrentUser(new ApiClient.ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                result.setValue(Result.success(user));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public LiveData<Result<List<Report>>> getReports() {
        MutableLiveData<Result<List<Report>>> result = new MutableLiveData<>();
        apiClient.getReports(new ApiClient.ApiCallback<List<Report>>() {
            @Override
            public void onSuccess(List<Report> reports) {
                result.setValue(Result.success(reports));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public LiveData<Result<Report>> createReport(Report report) {
        MutableLiveData<Result<Report>> result = new MutableLiveData<>();
        apiClient.createReport(report, new ApiClient.ApiCallback<Report>() {
            @Override
            public void onSuccess(Report report) {
                result.setValue(Result.success(report));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public LiveData<Result<ReportImage>> uploadImage(int reportId, File imageFile) {
        MutableLiveData<Result<ReportImage>> result = new MutableLiveData<>();
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
            
            apiClient.uploadImage(reportId, body, new ApiClient.ApiCallback<ReportImage>() {
                @Override
                public void onSuccess(ReportImage reportImage) {
                    result.setValue(Result.success(reportImage));
                }

                @Override
                public void onError(String errorMessage) {
                    result.setValue(Result.error(new Exception(errorMessage)));
                }
            });
        } catch (Exception e) {
            result.setValue(Result.error(e));
        }
        return result;
    }
    
    public LiveData<Result<Statistics>> getStatistics() {
        MutableLiveData<Result<Statistics>> result = new MutableLiveData<>();
        apiClient.getStatistics(new ApiClient.ApiCallback<Statistics>() {
            @Override
            public void onSuccess(Statistics stats) {
                result.setValue(Result.success(stats));
            }

            @Override
            public void onError(String errorMessage) {
                result.setValue(Result.error(new Exception(errorMessage)));
            }
        });
        return result;
    }
    
    public void logout() {
        apiClient.clearToken();
    }
} 