package br.com.ikaro.atividadeavaliativa.api;

import br.com.ikaro.atividadeavaliativa.models.*;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {
    @FormUrlEncoded
    @POST("token")
    Call<LoginResponse> login(
        @Field("username") String email,
        @Field("password") String password
    );

    @POST("users/")
    Call<User> register(@Body UserCreate user);

    @GET("users/me")
    Call<User> getCurrentUser();

    @GET("reports")
    Call<List<Report>> getReports();

    @GET("reports/{id}")
    Call<Report> getReport(@Path("id") int id);

    @POST("reports")
    Call<Report> createReport(@Body Report report);

    @PUT("reports/{id}")
    Call<Report> updateReport(@Path("id") int id, @Body Report report);

    @DELETE("reports/{id}")
    Call<Void> deleteReport(@Path("id") int id);

    @Multipart
    @POST("reports/{id}/images")
    Call<ReportImage> uploadImage(
        @Path("id") int reportId,
        @Part("file") okhttp3.MultipartBody.Part file
    );

    @DELETE("reports/{reportId}/images/{imageId}")
    Call<Void> deleteImage(
        @Path("reportId") int reportId,
        @Path("imageId") int imageId
    );

    @GET("statistics")
    Call<Statistics> getStatistics();
} 