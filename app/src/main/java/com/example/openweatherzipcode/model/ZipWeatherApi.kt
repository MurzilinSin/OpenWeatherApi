package com.example.openweatherzipcode.model

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Old version using Retrofit Call<t>
/*
interface ZipWeatherApi {
    @GET ("/data/2.5/weather")
        suspend fun getWeather(@Query("q")cityName: String,
                       @Query("appid")apiKey: String) : Call<ServerResponseData>
}*/

interface ZipWeatherApi {
    @GET("/data/2.5/weather")
    suspend fun getWeather(@Query("q") cityName: String,
                           @Query("appid") apiKey: String): Response<ServerResponseData>
}