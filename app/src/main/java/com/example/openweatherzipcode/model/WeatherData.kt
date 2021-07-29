package com.example.openweatherzipcode.model
//Sealed классы WeatherData для обработки полученных данных из Retrofit
sealed class WeatherData {
    data class Success(val serverResponseData: ServerResponseData) : WeatherData()
    data class Error(val error: Throwable) : WeatherData()
    data class Loading(val progress: Int?) : WeatherData()
}