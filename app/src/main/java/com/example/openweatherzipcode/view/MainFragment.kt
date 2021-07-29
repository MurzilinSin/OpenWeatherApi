package com.example.openweatherzipcode.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.openweatherzipcode.model.ServerResponseData
import com.example.openweatherzipcode.model.WeatherData
import com.example.openweatherzipcode.databinding.MainFragmentBinding
import com.example.openweatherzipcode.viewmodel.MainViewModel
import org.koin.android.ext.android.inject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel by inject<MainViewModel>()
    //Создание экземпляра viewBinding и viewModel при помощи lazy инициализации для дальнейшей работы

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    //в onViewCreated начинаем работать с кнопкой. По нажатию запускаем метод getData у viewModel
    //для того, чтобы получить данные по апи о погоде.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonZipCode.setOnClickListener {
            val cityName : String = binding.inputEditText.text.toString().trim()
            if(cityName.isEmpty()) {
                dialogWithOneButtonShow("Error",
                    "U need to enter some numbers to work",
                    "Try again")
            } else {
                viewModel.getData(cityName).observe(viewLifecycleOwner, {
                    renderData(it)
                })

                println("!!!! TYT " + viewLifecycleOwner.lifecycle.currentState)
                binding.inputEditText.text = null
            }
        }
    }

    //обработка данных полученных из viewModel для view. 3 состояния полученных данных.
    //Success - мы вызываем метод showData, где данные полученные из json объекту сериализуются и
    //помещаются в переменные, которые потом используются для заполнения значений в вьюхах
    //Loading - здесь используются вывод в консоль
    //Error - при ошибки данных выводится диалоговое окно
    private fun renderData(weatherData: WeatherData) {
        when(weatherData) {
            is WeatherData.Success -> {
                val serverResponseData = weatherData.serverResponseData
                showData(serverResponseData)
            }
            is WeatherData.Loading -> {
                println("WEVE JUST LOADING AND LOADING")
            }
            is WeatherData.Error -> {
                dialogWithOneButtonShow("Error",
                    "Incorrect name of city",
                    "Try another One")
            }
        }
    }

    private fun showData(data: ServerResponseData) {
        val location = data.name
         val temp = (data.main.temp * 1.8) - 459.67
         val windSpeed = data.windData.speed
         val humidity = data.main.humidity
         val visibility = data.weather[0].description
         val sunrise = data.sys.sunrise
         val sunset = data.sys.sunset
         binding.location.text = location
         binding.temp.text = "${BigDecimal(temp).setScale(2, RoundingMode.HALF_EVEN)} F"
         binding.windSpeed.text = "${windSpeed} mph"
         binding.humidity.text = "${humidity} %"
         binding.visibility.text = visibility
         binding.sunrise.text = setDateFromS(sunrise.toString())
         binding.sunset.text = setDateFromS(sunset.toString())
    }

    //Перевод полученных СЕКУНД в строку, которая уже отформатировано при помощи SimpleDateFormat.
    //В итоге получается строка с датой и временем
    private fun setDateFromS(seconds: String) : String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a E", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = seconds.toLong() * 1000
        return sdf.format(calendar.time)
    }

    //Функция, вызывающая диалоговое окно
    private fun dialogWithOneButtonShow(title: String, description: String, textButton: String) {
        val dialog = AlertDialog.Builder(context)
        dialog.apply {
            setTitle(title)
            setMessage(description)
            setPositiveButton(textButton)
            { dialog, _ ->
                dialog.cancel()
            }
        }.create().show()
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    //Сохранение данных для воспроизведение после уничтожение активити
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveDataFromTextView(outState)
    }

    //Воспроизведение сохраненных данных, если они есть
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loadDataFromSavedInstance(savedInstanceState)
    }
    private fun saveDataFromTextView(outState: Bundle) {
        outState.putString("location", binding.location.text.toString())
        outState.putString("temp", binding.temp.text.toString())
        outState.putString("windSpeed", binding.windSpeed.text.toString())
        outState.putString("humidity", binding.humidity.text.toString())
        outState.putString("visibility", binding.visibility.text.toString())
        outState.putString("sunrise", binding.sunrise.text.toString())
        outState.putString("sunset", binding.sunset.text.toString())
    }

    private fun loadDataFromSavedInstance(savedInstanceState: Bundle?) {
        binding.location.text = savedInstanceState?.getString("location")
        binding.temp.text = savedInstanceState?.getString("temp")
        binding.windSpeed.text = savedInstanceState?.getString("windSpeed")
        binding.humidity.text = savedInstanceState?.getString("humidity")
        binding.visibility.text = savedInstanceState?.getString("visibility")
        binding.sunrise.text = savedInstanceState?.getString("sunrise")
        binding.sunset.text = savedInstanceState?.getString("sunset")
    }
}