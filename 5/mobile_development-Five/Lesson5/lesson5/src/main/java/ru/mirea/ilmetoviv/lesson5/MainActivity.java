package ru.mirea.ilmetoviv.lesson5;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.ilmetoviv.lesson5.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем SensorManager
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        @SuppressWarnings("deprecation")
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Находим ListView через ViewBinding
        ListView listSensor = binding.sensorListView;

        // Создаём список данных для отображения
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        for (Sensor sensor : sensors) {
            HashMap<String, Object> sensorData = new HashMap<>();
            sensorData.put("Name", sensor.getName());
            sensorData.put("Value", sensor.getMaximumRange());
            arrayList.add(sensorData);
        }

        // Создаём адаптер для отображения двух строк в каждом элементе
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Value"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        // Устанавливаем адаптер в ListView
        listSensor.setAdapter(adapter);
    }
}