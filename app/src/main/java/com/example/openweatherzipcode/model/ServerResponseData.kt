package com.example.openweatherzipcode.model

import com.google.gson.annotations.SerializedName

//ServerResponseData класс, который содержит поля, полученные из запроса с сервера в виде Json объекта.
// Также тут созданы классы для более корректной обработки данных
class ServerResponseData (
        @field:SerializedName("coord") val coordinates: Coordinates,
        @field:SerializedName("weather") val weather: List<WeatherDescription>,
        @field:SerializedName("base") val base: String,
        @field:SerializedName("main") val main: MainData,
        @field:SerializedName("visibility") val visibility: Int,
        @field:SerializedName("wind") val windData: WindData,
        @field:SerializedName("clouds") val clouds: Clouds,
        @field:SerializedName("dt") val dt: Int,
        @field:SerializedName("sys") val sys: SystemData,
        @field:SerializedName("timezone") val timezone: String,
        @field:SerializedName("id") val id: Int,
        @field:SerializedName("name") val name: String,
        @field:SerializedName("cod") val cod: Int
)
data class Coordinates(
    val lon: Float,
    val lat: Float
)
data class WeatherDescription(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
data class MainData(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val humidity: Int
)
data class WindData(
    val speed: Float,
    val deg: Int
)
data class SystemData(
    val type: Int,
    val id: Int,
    val message: Float,
    val Country: String,
    val sunrise: Long,
    val sunset: Long
)
data class Clouds(
    val all: Int
)