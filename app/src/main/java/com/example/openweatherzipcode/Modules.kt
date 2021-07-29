package com.example.openweatherzipcode

import com.example.openweatherzipcode.model.ZipWeatherApi
import com.example.openweatherzipcode.viewmodel.MainViewModel
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

val viewModelModule : Module = module {
    viewModel { MainViewModel(provideRetrofit()) }
}

val retrofitModule : Module = module {
    factory { PODInterceptor() }
    single { provideHttpClient(get()) }
    single { provideGson() }
    single { provideRetrofit() }
}

fun provideHttpClient(interceptor: Interceptor): OkHttpClient {
    val httpClient = OkHttpClient.Builder()
    httpClient.addInterceptor(interceptor)
    httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    return httpClient.build()
}

fun provideGson(): GsonConverterFactory {
    return GsonConverterFactory.create(GsonBuilder().setLenient().create())
}

class PODInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}

fun provideRetrofit(): ZipWeatherApi {
    val podRetrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(provideGson())
        .client(provideHttpClient(PODInterceptor()))
        .build()
    return podRetrofit.create(ZipWeatherApi::class.java)
}
