package com.example.myshop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class GsonImpl {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResponseException.class, (JsonSerializer<ResponseException>) (src, typeOfSrc, context) -> context.serialize(src.response()))
            .create();

    public static Gson getGson(){
        return GSON;
    }
}
