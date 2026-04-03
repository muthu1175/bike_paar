package com.example.bikepaar;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;
import retrofit2.http.Header;
import retrofit2.http.GET;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import java.util.List;
import retrofit2.http.Query;








public interface ApiService {

    @POST("login/")
    Call<Map<String, String>> login(@Body Map<String, String> body);
    @GET("profile/")
    Call<UserResponse> getProfile(
            @Header("Authorization") String token
    );

    @POST("signup/")
    Call<Map<String, String>> signup(@Body Map<String, String> body);

    @POST("forgot-password/")
    Call<Map<String, String>> forgotPassword(@Body Map<String, String> body);

    @Multipart
    @POST("upload-profile-image/")
    Call<Map<String, String>> uploadProfileImage(
            @Header("Authorization") String token,
            @Part MultipartBody.Part image
    );



    @GET("search/")
    Call<List<Bike>> searchBikes(@Header("Authorization") String token, @Query("q") String query);

    @GET("reviews/")
    Call<List<Map<String, Object>>> getBikeReviews(@Header("Authorization") String token, @Query("bike_id") String bikeId);

    @POST("reviews/")
    Call<Map<String, String>> addBikeReview(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @GET("app-reviews/")
    Call<List<Map<String, Object>>> getAppReviews();

    @GET("bikes/below-100cc/")
    Call<List<Bike>> getBelow100ccBikes(@Header("Authorization") String token);

    @GET("bikes/100-150cc/")
    Call<List<Bike>> get100to150ccBikes(@Header("Authorization") String token);

    @GET("bikes/150-200cc/")
    Call<List<Bike>> get150to200ccBikes(@Header("Authorization") String token);

    @GET("bikes/200-350cc/")
    Call<List<Bike>> get200to350ccBikes(@Header("Authorization") String token);

    @GET("bikes/350-500cc/")
    Call<List<Bike>> get350to500ccBikes(@Header("Authorization") String token);

    @GET("bikes/500-750cc/")
    Call<List<Bike>> get500to750ccBikes(@Header("Authorization") String token);

    @GET("bikes/750-1000cc/")
    Call<List<Bike>> get750to1000ccBikes(@Header("Authorization") String token);

    @GET("bikes/above-1000cc/")
    Call<List<Bike>> getAbove1000ccBikes(@Header("Authorization") String token);

    @GET("bikes/30k-80k/")
    Call<List<Bike>> get30kto80kBikes(@Header("Authorization") String token);

    @GET("bikes/80k-150k/")
    Call<List<Bike>> get80kto150kBikes(@Header("Authorization") String token);

    @GET("bikes/150k-300k/")
    Call<List<Bike>> get150kto300kBikes(@Header("Authorization") String token);

    @GET("bikes/300k-500k/")
    Call<List<Bike>> get300kto500kBikes(@Header("Authorization") String token);

    @GET("bikes/5l-10l/")
    Call<List<Bike>> get5Lto10LBikes(@Header("Authorization") String token);

    @GET("bikes/10l-30l/")
    Call<List<Bike>> get10Lto30LBikes(@Header("Authorization") String token);

    @GET("bikes/above-30l/")
    Call<List<Bike>> getAbove30LBikes(@Header("Authorization") String token);

    @POST("ai-suggest/")
    Call<List<Bike>> getAiSuggestions(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @POST("app-reviews/")
    Call<Map<String, String>> addAppReview(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @POST("feedback/")
    Call<Map<String, String>> addFeedback(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );



    @GET("bikes/sports/")
    Call<List<Bike>> getSportsBikes(@Header("Authorization") String token);

    @GET("bikes/scooters/")
    Call<List<Bike>> getScooterBikes(@Header("Authorization") String token);

    @GET("bikes/cruisers/")
    Call<List<Bike>> getCruiserBikes(@Header("Authorization") String token);

    @GET("bikes/commuters/")
    Call<List<Bike>> getCommuterBikes(@Header("Authorization") String token);

    @GET("bikes/street/")
    Call<List<Bike>> getStreetBikes(@Header("Authorization") String token);

    @GET("bikes/super/")
    Call<List<Bike>> getSuperBikes(@Header("Authorization") String token);

    @GET("bikes/tourer/")
    Call<List<Bike>> getTourerBikes(@Header("Authorization") String token);

    @GET("bikes/adventure/")
    Call<List<Bike>> getAdventureBikes(@Header("Authorization") String token);

    @GET("bikes/scramblers/")
    Call<List<Bike>> getScramblerBikes(@Header("Authorization") String token);

    @GET("bikes/")
    Call<List<SportsBike>> getAllBikes(@Header("Authorization") String token);

    @GET("favourites/")
    Call<List<SportsBike>> getFavorites(@Header("Authorization") String token);

    @POST("favourites/")
    Call<Map<String, String>> addFavorite(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );

    @retrofit2.http.HTTP(method = "DELETE", path = "favourites/", hasBody = true)
    Call<Map<String, String>> removeFavorite(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );


    @GET("bikes/by-brand/")
    Call<List<Bike>> getBrandBikes(@Header("Authorization") String token, @Query("brand") String brand);

    @GET("bikes/popular/")
    Call<List<Bike>> getPopularBikes(@Header("Authorization") String token);

    @GET("bikes/recent/")
    Call<List<Bike>> getRecentLaunches(@Header("Authorization") String token);

    // Email OTP Verification
    @POST("send-email-otp/")
    Call<Map<String, String>> sendEmailOTP(@Body Map<String, String> body);

    @POST("verify-email-otp/")
    Call<Map<String, Object>> verifyEmailOTP(@Body Map<String, String> body);
}
