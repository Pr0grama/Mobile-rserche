package ru.mirea.ilmetoviv.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ru.mirea.ilmetoviv.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;
    private static final String TAG = "LooperApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ===== Handler для главного (UI) потока =====
        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result");
                binding.textViewResult.setText(result);
                Log.d(TAG, "Task execute. This is result: " + result); // ← требование задания
            }
        };

        // ===== Запускаем кастомный Looper-поток =====
        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        // ===== Обработчик кнопки =====
        binding.buttonSend.setOnClickListener(v -> {
            String age = binding.editTextAge.getText().toString().trim();
            String occupation = binding.editTextOccupation.getText().toString().trim();

            if (age.isEmpty() || occupation.isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Формируем сообщение для отправки в фоновый поток
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("age", age);
            bundle.putString("occupation", occupation);
            msg.setData(bundle);

            // Отправляем в очередь фонового потока
            if (myLooper.mHandler != null) {
                myLooper.mHandler.sendMessage(msg);
                binding.textViewResult.setText("Обработка... (задержка: " + age + " сек)");
            } else {
                Log.e(TAG, "mHandler ещё не инициализирован!");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Корректная остановка Looper
        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quit();
        }
        binding = null;
    }
}