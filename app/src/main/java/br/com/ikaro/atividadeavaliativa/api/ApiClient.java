package br.com.ikaro.atividadeavaliativa.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.ikaro.atividadeavaliativa.models.LoginRequest;
import br.com.ikaro.atividadeavaliativa.models.LoginResponse;
import br.com.ikaro.atividadeavaliativa.models.Report;
import br.com.ikaro.atividadeavaliativa.models.ReportImage;
import br.com.ikaro.atividadeavaliativa.models.Statistics;
import br.com.ikaro.atividadeavaliativa.models.User;
import br.com.ikaro.atividadeavaliativa.models.UserCreate;
import br.com.ikaro.atividadeavaliativa.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static final String PREF_NAME = "ApiPrefs";
    private static final String KEY_TOKEN = "token";
    private static ApiClient instance;
    private final ApiService apiService;
    private final SharedPreferences preferences;
    private final SessionManager sessionManager;
    private String token;

    private ApiClient(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sessionManager = new SessionManager(context);
        token = preferences.getString(KEY_TOKEN, null);

        Log.d(TAG, "Inicializando ApiClient com URL base: " + BASE_URL);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, "OkHttp: " + message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    Log.d(TAG, "Fazendo requisição para: " + original.url());
                    
                    okhttp3.Request.Builder builder = original.newBuilder();
                    if (token != null) {
                        builder.header("Authorization", "Bearer " + token);
                        Log.d(TAG, "Adicionando token de autorização");
                    }
                    
                    okhttp3.Request request = builder.build();
                    return chain.proceed(request);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        Log.d(TAG, "ApiClient inicializado com sucesso");
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void saveToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        preferences.edit().remove(KEY_TOKEN).apply();
    }

    // Interface para callbacks
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    // Autenticação
    public void login(String email, String password, ApiCallback<User> callback) {
        Log.d(TAG, "Tentando login com email: " + email);
        apiService.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Resposta do login - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Login bem sucedido");
                    LoginResponse loginResponse = response.body();
                    token = loginResponse.getAccessToken();
                    saveToken(token);
                    getCurrentUser(callback);
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e(TAG, "Falha no login. Código: " + response.code() + ", Erro: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler corpo da resposta", e);
                    }
                    callback.onError("Falha no login: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Erro de conexão no login", t);
                String errorMessage = "Erro de conexão: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Não foi possível conectar ao servidor. Verifique se a API está rodando.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage = "Conexão recusada pelo servidor. Verifique se a API está rodando na porta 8000.";
                }
                callback.onError(errorMessage);
            }
        });
    }

    public void register(String name, String cpf, String email, String phone, String password, ApiCallback<User> callback) {
        Log.d(TAG, "Tentando registrar usuário: " + email);
        UserCreate user = new UserCreate(name, cpf, email, phone, password, false);
        apiService.register(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Resposta do registro - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Registro bem sucedido");
                    callback.onSuccess(response.body());
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e(TAG, "Falha no registro. Código: " + response.code() + ", Erro: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler corpo da resposta", e);
                    }
                    callback.onError("Falha no registro: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Erro de conexão no registro", t);
                String errorMessage = "Erro de conexão: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Não foi possível conectar ao servidor. Verifique se a API está rodando.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage = "Conexão recusada pelo servidor. Verifique se a API está rodando na porta 8000.";
                }
                callback.onError(errorMessage);
            }
        });
    }

    public void getCurrentUser(ApiCallback<User> callback) {
        Log.d(TAG, "Obtendo dados do usuário atual");
        apiService.getCurrentUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "Resposta getCurrentUser - Código: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Dados do usuário obtidos com sucesso");
                    User user = response.body();
                    sessionManager.saveUserDetails(user);
                    callback.onSuccess(user);
                } else {
                    String errorBody = "";
                    try {
                        errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e(TAG, "Falha ao obter dados do usuário. Código: " + response.code() + ", Erro: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Erro ao ler corpo da resposta", e);
                    }
                    callback.onError("Falha ao obter dados do usuário: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Erro de conexão ao obter dados do usuário", t);
                String errorMessage = "Erro de conexão: " + t.getMessage();
                if (t instanceof java.net.UnknownHostException) {
                    errorMessage = "Não foi possível conectar ao servidor. Verifique se a API está rodando.";
                } else if (t instanceof java.net.ConnectException) {
                    errorMessage = "Conexão recusada pelo servidor. Verifique se a API está rodando na porta 8000.";
                }
                callback.onError(errorMessage);
            }
        });
    }

    // Denúncias
    public void getReports(ApiCallback<List<Report>> callback) {
        apiService.getReports().enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao obter denúncias");
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void getReport(int id, ApiCallback<Report> callback) {
        apiService.getReport(id).enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao obter denúncia");
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void createReport(Report report, ApiCallback<Report> callback) {
        apiService.createReport(report).enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao criar denúncia");
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void updateReport(int id, Report report, ApiCallback<Report> callback) {
        apiService.updateReport(id, report).enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao atualizar denúncia");
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void deleteReport(int id, ApiCallback<Void> callback) {
        apiService.deleteReport(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Falha ao excluir denúncia");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    // Imagens
    public void uploadImage(int reportId, okhttp3.MultipartBody.Part file, ApiCallback<ReportImage> callback) {
        apiService.uploadImage(reportId, file).enqueue(new Callback<ReportImage>() {
            @Override
            public void onResponse(Call<ReportImage> call, Response<ReportImage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao fazer upload da imagem");
                }
            }

            @Override
            public void onFailure(Call<ReportImage> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    public void deleteImage(int reportId, int imageId, ApiCallback<Void> callback) {
        apiService.deleteImage(reportId, imageId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Falha ao excluir imagem");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    // Estatísticas
    public void getStatistics(ApiCallback<Statistics> callback) {
        apiService.getStatistics().enqueue(new Callback<Statistics>() {
            @Override
            public void onResponse(Call<Statistics> call, Response<Statistics> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Falha ao obter estatísticas");
                }
            }

            @Override
            public void onFailure(Call<Statistics> call, Throwable t) {
                callback.onError("Erro de conexão: " + t.getMessage());
            }
        });
    }

    private void setAuthToken(String token) {
        sessionManager.saveAuthToken(token);
    }
}
