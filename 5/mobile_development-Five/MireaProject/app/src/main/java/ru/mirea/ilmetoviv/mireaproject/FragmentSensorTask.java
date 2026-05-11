package ru.mirea.ilmetoviv.mireaproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ru.mirea.ilmetoviv.mireaproject.databinding.FragmentSensorTaskBinding;

public class FragmentSensorTask extends Fragment implements SensorEventListener {

    private FragmentSensorTaskBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    // ✅ Храним последние показания между вызовами onSensorChanged
    private final float[] mGravity = new float[3];
    private final float[] mGeomagnetic = new float[3];
    private boolean hasGravity = false;
    private boolean hasGeomagnetic = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSensorTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sensorManager = (SensorManager) requireActivity().getSystemService(requireActivity().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null || magnetometer == null) {
            binding.tvDirection.setText("Датчики компаса отсутствуют на этом устройстве");
            binding.tvDegree.setText("---");
            Toast.makeText(requireContext(), "Магнитометр не найден", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mGravity, 0, 3);
            hasGravity = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);
            hasGeomagnetic = true;
        }

        // Вычисляем ориентацию только когда оба датчика отдали данные
        if (hasGravity && hasGeomagnetic) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimuthInDegrees = (float) Math.toDegrees(orientation[0]);
                azimuthInDegrees = (azimuthInDegrees + 360) % 360;
                int degree = Math.round(azimuthInDegrees);

                binding.tvDegree.setText(degree + "°");

                String direction;
                if (degree >= 315 || degree < 45) direction = "Север";
                else if (degree >= 45 && degree < 135) direction = "Восток";
                else if (degree >= 135 && degree < 225) direction = "Юг";
                else direction = "Запад";
                binding.tvDirection.setText("Направление: " + direction);

                // Плавный поворот без deprecated RotateAnimation
                binding.ivCompass.animate().rotation(-degree).setDuration(150).start();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Можно игнорировать для учебного задания
    }
}