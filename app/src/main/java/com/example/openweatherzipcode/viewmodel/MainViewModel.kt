package com.example.openweatherzipcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.openweatherzipcode.BuildConfig
import com.example.openweatherzipcode.model.WeatherData
import com.example.openweatherzipcode.model.ZipWeatherApi
import kotlinx.coroutines.*


class MainViewModel(
        private val zipWeatherApi: ZipWeatherApi
                    ) : ViewModel() {

    var job: Job? = null
    private val liveDataForViewToObserve: MutableLiveData<WeatherData> = MutableLiveData()
    fun getData(cityName : String): LiveData<WeatherData> {
        sendServerRequest(cityName)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(cityName: String) {
        liveDataForViewToObserve.value = WeatherData.Loading(null)
        val apiKey: String = BuildConfig.WEATHER_API_KEY
        if(apiKey.isBlank()) {
            WeatherData.Error(Throwable("Api key is not found"))
        } else {
            job = CoroutineScope(Dispatchers.Main).launch {
                val response = zipWeatherApi.getWeather(cityName, apiKey)
                if(response.isSuccessful) {
                    liveDataForViewToObserve.value = WeatherData.Success(response.body()!!)
                } else {
                    val message = response.message()
                    if (message.isNullOrEmpty()) {
                        liveDataForViewToObserve.value =
                                WeatherData.Error(Throwable("Unidentified error"))
                    } else {
                        liveDataForViewToObserve.value =
                                WeatherData.Error(Throwable(message))
                    }
                }
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}