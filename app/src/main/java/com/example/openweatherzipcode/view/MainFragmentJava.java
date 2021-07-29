package com.example.openweatherzipcode.view;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.openweatherzipcode.databinding.MainFragmentBinding;
import com.example.openweatherzipcode.model.ServerResponseData;
import com.example.openweatherzipcode.model.WeatherData;
import com.example.openweatherzipcode.model.WeatherData.Error;
import com.example.openweatherzipcode.model.WeatherData.Loading;
import com.example.openweatherzipcode.model.WeatherData.Success;
import com.example.openweatherzipcode.viewmodel.MainViewModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class MainFragmentJava extends Fragment {
    private MainFragmentBinding binding;
    private MainViewModel viewModel;

    // в методе onCreate инициализирую viewModel через ViewModelProviders
    //честно говоря, пока не нашел чем можно нормально заменить viewModelProviders.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    //в onViewCreated начинаем работать с кнопкой. По нажатию запускаем метод getData у viewModel
    //для того, чтобы получить данные по апи о погоде.
    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.buttonZipCode.setOnClickListener(new OnClickListener() {
            public final void onClick(View it) {
                String cityName = binding.inputEditText.getText().toString().trim();
                if (cityName.isEmpty()) {
                    dialogWithOneButtonShow("Error",
                            "U need to enter some numbers to work",
                            "Try again");
                } else {
                    viewModel.getData(cityName).observe(MainFragmentJava.this.getViewLifecycleOwner(), new Observer() {
                        public void onChanged(Object var1) {
                            this.onChanged((WeatherData)var1);
                        }
                        public final void onChanged(WeatherData it) {
                            MainFragmentJava.this.renderData(it);
                        }
                    });
                    binding.inputEditText.setText(null);
                }
            }
        });
    }

    //обработка данных полученных из viewModel для view. 3 состояния полученных данных.
    //Success - мы вызываем метод showData, где данные полученные из json объекту сериализуются и
    //помещаются в переменные, которые потом используются для заполнения значений в вьюхах
    //Loading - здесь используются вывод в консоль
    //Error - при ошибки данных выводится диалоговое окно
    private void renderData(WeatherData weatherData) {
        if (weatherData instanceof Success) {
            ServerResponseData serverResponseData = ((Success)weatherData).getServerResponseData();
            this.showData(serverResponseData);
        } else if (weatherData instanceof Loading) {
            String load = "WEVE JUST LOADING AND LOADING";
            System.out.println(load);
        } else if (weatherData instanceof Error) {
            this.dialogWithOneButtonShow("Error", "Incorrect name of city", "Try another One");
        }

    }

    private void showData(ServerResponseData data) {
        String location = data.getName();
        double temp = (double)data.getMain().getTemp() * 1.8D - 459.67D;
        float windSpeed = data.getWindData().getSpeed();
        int humidity = data.getMain().getHumidity();
        String visibility = (data.getWeather().get(0)).getDescription();
        long sunrise = data.getSys().getSunrise();
        long sunset = data.getSys().getSunset();
        binding.location.setText(location);
        binding.temp.setText((new BigDecimal(temp)).setScale(2,RoundingMode.HALF_EVEN).toString() + " F");
        binding.windSpeed.setText(windSpeed + " mph");
        binding.humidity.setText(humidity + " %");
        binding.visibility.setText(visibility);
        binding.sunrise.setText(setDateFromS(String.valueOf(sunrise)));
        binding.sunset.setText(setDateFromS(String.valueOf(sunset)));
    }

    //Перевод полученных СЕКУНД в строку, которая уже отформатировано при помощи SimpleDateFormat.
    //В итоге получается строка с датой и временем
    private String setDateFromS(String seconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a E", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(seconds) * (long)1000);
        return sdf.format(calendar.getTime());
    }

    //Функция, вызывающая диалоговое окно
    private void dialogWithOneButtonShow(String title, String description, String textButton) {
        Builder dialog = new Builder(this.getContext());
        dialog.setTitle(title);
        dialog.setMessage(description);
        dialog.setPositiveButton(textButton, (dialog1, id) -> dialog1.cancel());
        dialog.create().show();
    }
    //Сохранение данных для воспроизведение после уничтожение активити
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveDataFromTextView(outState);
    }
    //Воспроизведение сохраненных данных, если они есть
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            loadDataFromSavedInstance(savedInstanceState);
        }
    }

    private void loadDataFromSavedInstance(@Nullable Bundle savedInstanceState) {
        binding.location.setText(savedInstanceState.getString("location"));
        binding.temp.setText(savedInstanceState.getString("temp"));
        binding.windSpeed.setText(savedInstanceState.getString("windSpeed"));
        binding.humidity.setText(savedInstanceState.getString("humidity"));
        binding.visibility.setText(savedInstanceState.getString("visibility"));
        binding.sunrise.setText(savedInstanceState.getString("sunrise"));
        binding.sunset.setText(savedInstanceState.getString("sunset"));
    }

    private void saveDataFromTextView(@NonNull Bundle outState) {
        outState.putString("location", binding.location.getText().toString());
        outState.putString("temp", binding.temp.getText().toString());
        outState.putString("windSpeed", binding.windSpeed.getText().toString());
        outState.putString("humidity", binding.humidity.getText().toString());
        outState.putString("visibility", binding.visibility.getText().toString());
        outState.putString("sunrise", binding.sunrise.getText().toString());
        outState.putString("sunset", binding.sunset.getText().toString());
    }
}
