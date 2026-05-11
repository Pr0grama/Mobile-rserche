package ru.mirea.ilmetoviv.data_thread;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.TimeUnit;
import ru.mirea.goncharovas.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int executionOrder = 0;
    private final String TAG = "ThreadUI_Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ===== Создаём 3 Runnable =====
        final Runnable runn1 = () -> {
            executionOrder++;
            String msg = executionOrder + ". runn1 via runOnUiThread\n";
            binding.textViewResult.append(msg);
            Log.d(TAG, "Executed: runn1 at " + System.currentTimeMillis());
        };

        final Runnable runn2 = () -> {
            executionOrder++;
            String msg = executionOrder + ". runn2 via View.post\n";
            binding.textViewResult.append(msg);
            Log.d(TAG, "Executed: runn2 at " + System.currentTimeMillis());
        };

        final Runnable runn3 = () -> {
            executionOrder++;
            String msg = executionOrder + ". runn3 via View.postDelayed(2000)\n";
            binding.textViewResult.append(msg);
            Log.d(TAG, "Executed: runn3 at " + System.currentTimeMillis());
        };

        // ===== Обработчик кнопки =====
        binding.buttonStart.setOnClickListener(v -> {
            // Сброс состояния
            executionOrder = 0;
            binding.textViewResult.setText("Запуск теста...\n");
            Log.d(TAG, "Test started");

            // ===== Фоновый поток =====
            new Thread(() -> {
                try {
                    Log.d(TAG, "Background: sleep 2s");
                    TimeUnit.SECONDS.sleep(2);  // Ждём 2 секунды

                    // 1. runOnUiThread — постит в главный Looper немедленно
                    Log.d(TAG, "Background: posting runn1 via runOnUiThread");
                    runOnUiThread(runn1);

                    Log.d(TAG, "Background: sleep 1s");
                    TimeUnit.SECONDS.sleep(1);  // Ещё 1 секунда (итого 3с от старта)

                    // 2. postDelayed — выполнится через 2с после вызова (т.е. ~5с от старта)
                    Log.d(TAG, "Background: posting runn3 via postDelayed(2000)");
                    binding.textViewResult.postDelayed(runn3, 2000);

                    // 3. post — постит немедленно в очередь сообщений View
                    Log.d(TAG, "Background: posting runn2 via View.post");
                    binding.textViewResult.post(runn2);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Thread interrupted", e);
                }
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}