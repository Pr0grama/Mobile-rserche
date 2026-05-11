package ru.mirea.ilmetoviv.workmanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import ru.mirea.goncharovas.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "WorkManagerApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ===== Настройка критериев запуска (требование задания) =====
        Constraints constraints = new Constraints.Builder()
                // Требует интернет БЕЗ тарификации (Wi-Fi или безлимит)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                // Требует, чтобы устройство было на зарядке
                .setRequiresCharging(true)
                // Опционально: требует, чтобы устройство не в режиме энергосбережения
                // .setRequiresBatteryNotLow(true)
                .build();

        // ===== Создание запроса на выполнение =====
        WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setConstraints(constraints)  // Применяем критерии
                .addTag("upload_task")         // Опционально: тег для отслеживания
                .build();

        // ===== Обработчик кнопки =====
        binding.buttonStart.setOnClickListener(v -> {
            Log.d(TAG, "Пользователь запросил выполнение задачи");

            // Запускаем работу через WorkManager
            WorkManager.getInstance(this)
                    .enqueue(uploadWorkRequest);

            Toast.makeText(this, "Задача поставлена в очередь", Toast.LENGTH_SHORT).show();

            // Опционально: можно отслеживать статус
            // WorkManager.getInstance(this)
            //     .getWorkInfosByTagLiveData("upload_task")
            //     .observe(this, workInfos -> { ... });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}