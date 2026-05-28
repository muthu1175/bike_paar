package com.example.bikepaar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Custom Gson to handle empty strings/invalid numbers safely
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(int.class, new TypeAdapter<Integer>() {
                        @Override
                        public void write(JsonWriter out, Integer value) throws IOException {
                            out.value(value);
                        }

                        @Override
                        public Integer read(JsonReader in) throws IOException {
                            if (in.peek() == JsonToken.NULL) {
                                in.nextNull();
                                return 0;
                            }
                            if (in.peek() == JsonToken.STRING) {
                                String s = in.nextString();
                                if (s == null || s.trim().isEmpty()) {
                                    return 0;
                                }
                                try {
                                    return Integer.parseInt(s.trim());
                                } catch (NumberFormatException e) {
                                    return 0; // Fallback to 0 on error
                                }
                            }
                            try {
                                return in.nextInt();
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                    })
                    // Also register for Integer object wrapper if needed, but int.class covers primitive
                    .registerTypeAdapter(Integer.class, new TypeAdapter<Integer>() {
                         @Override
                         public void write(JsonWriter out, Integer value) throws IOException {
                             out.value(value);
                         }
                         @Override
                         public Integer read(JsonReader in) throws IOException {
                             if (in.peek() == JsonToken.NULL) {
                                 in.nextNull();
                                 return null;
                             }
                             if (in.peek() == JsonToken.STRING) {
                                 String s = in.nextString();
                                 if (s == null || s.trim().isEmpty()) return 0;
                                 try { return Integer.parseInt(s.trim()); } catch(Exception e) { return 0; }
                             }
                             return in.nextInt();
                         }
                    })
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.59.166.87:8000/api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
