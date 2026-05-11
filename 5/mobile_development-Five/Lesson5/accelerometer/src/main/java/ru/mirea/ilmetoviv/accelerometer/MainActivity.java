package ru.mirea.ilmetoviv.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView azimuthTextView;
    private TextView pitchTextView;
    private TextView rollTextView;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация TextView через findViewById
        azimuthTextView = findViewById(R.id.textViewAzimuth);
        pitchTextView = findViewById(R.id.textViewPitch);
        rollTextView = findViewById(R.id.textViewRoll);

        // Получаем SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Получаем датчик акселерометра по умолчанию
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Проверка: есть ли акселерометр на устройстве
        if (accelerometerSensor == null) {
            azimuthTextView.setText("Акселерометр не найден");
            pitchTextView.setVisibility(android.view.View.GONE);
            rollTextView.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Регистрируем слушатель при возврате в приложение
        if (accelerometerSensor != null) {
            sensorManager.registerListener(
                    this,                           // SensorEventListener
                    accelerometerSensor,            // датчик
                    SensorManager.SENSOR_DELAY_NORMAL  // частота обновлений
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Отменяем регистрацию для экономии батареи
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Проверяем, что событие от акселерометра
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // values[0] — ось X (влево/вправо)
            // values[1] — ось Y (вперёд/назад)
            // values[2] — ось Z (вверх/вниз)
            float valueX = event.values[0];
            float valueY = event.values[1];
            float valueZ = event.values[2];

            // Обновляем TextView
            azimuthTextView.setText(String.format("X (влево/вправо): %.2f м/с²", valueX));
            pitchTextView.setText(String.format("Y (вперёд/назад): %.2f м/с²", valueY));
            rollTextView.setText(String.format("Z (вверх/вниз): %.2f м/с²", valueZ));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Этот метод можно оставить пустым для учебного задания
        // Здесь можно обрабатывать изменение точности датчика
    }
}