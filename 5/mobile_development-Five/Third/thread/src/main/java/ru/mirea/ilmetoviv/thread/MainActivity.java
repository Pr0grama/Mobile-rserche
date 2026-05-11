package ru.mirea.ilmetoviv.thread;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import ru.mirea.ilmetoviv.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ===== Работа с главным потоком (по заданию) =====
        TextView infoTextView = binding.textViewResult;
        Thread mainThread = Thread.currentThread();

        infoTextView.setText("Имя текущего потока: " + mainThread.getName());

        // Меняем имя потока (подставь свои данные)
        mainThread.setName("МОЙ НОМЕР ГРУППЫ: БСБО-01-23, НОМЕР ПО СПИСКУ: 15, МОЙ ЛЮБИМЫЙ ФИЛЬМ: Начало");
        infoTextView.append("\nНовое имя потока: " + mainThread.getName());

        Log.d(MainActivity.class.getSimpleName(),
                "Stack: " + Arrays.toString(mainThread.getStackTrace()));

        // ===== Обработчик кнопки =====
        binding.buttonCalculate.setOnClickListener(v -> {
            String totalStr = binding.editTextTotalPairs.getText().toString();
            String daysStr = binding.editTextStudyDays.getText().toString();

            if (totalStr.isEmpty() || daysStr.isEmpty()) {
                Toast.makeText(this, "Заполните оба поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalPairs = Integer.parseInt(totalStr);
            int studyDays = Integer.parseInt(daysStr);

            // ===== Запуск фонового потока для вычислений =====
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Имитация «тяжёлой» операции (можно убрать в реальном проекте)
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Расчёт среднего
                    double average = (double) totalPairs / studyDays;
                    String result = String.format("Среднее количество пар в день: %.2f", average);

                    // ===== Возврат результата в UI-поток =====
                    // ВАЖНО: обновление UI только через runOnUiThread!
                    runOnUiThread(() -> {
                        binding.textViewResult.setText(result);
                        Log.d("ThreadCalc", "Расчёт завершён в потоке: " + Thread.currentThread().getName());
                    });
                }
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Предотвращаем утечку памяти
    }
}