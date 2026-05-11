package ru.mirea.ilmetoviv.serviceapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ru.mirea.ilmetoviv.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Запрашиваем разрешения при необходимости
        checkAndRequestPermissions();

        // Кнопка «Воспроизвести»
        binding.buttonPlay.setOnClickListener(v -> {
            Log.d(TAG, "Запуск сервиса");
            Intent serviceIntent = new Intent(this, PlayerService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Для Android 8+ используем startForegroundService
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                startService(serviceIntent);
            }

            Toast.makeText(this, "Воспроизведение начато", Toast.LENGTH_SHORT).show();
        });

        // Кнопка «Остановить»
        binding.buttonStop.setOnClickListener(v -> {
            Log.d(TAG, "Остановка сервиса");
            stopService(new Intent(this, PlayerService.class));
            Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkAndRequestPermissions() {
        // POST_NOTIFICATIONS нужен только для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                android.Manifest.permission.POST_NOTIFICATIONS,
                                android.Manifest.permission.FOREGROUND_SERVICE
                        },
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Разрешения получены");
            } else {
                Log.w(TAG, "Разрешения не получены — сервис может не работать");
                Toast.makeText(this, "Предоставьте разрешения для работы сервиса",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}