package ru.mirea.goncharovas.toastapp; // ← Ваш пакет!

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.goncharovas.toastapp.R;

public class MainActivity extends AppCompatActivity {

    // 🔹 Ваши данные (замените на свои!)
    private static final String STUDENT_NUMBER = "12345";  // Номер студента
    private static final String GROUP_NUMBER = "ИУБО-01-24"; // Номер группы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Обработчик кнопки "Подсчитать символы"
     * Считает количество символов в EditText и показывает Toast
     */
    public void onCountClick(View view) {
        // 1. Находим поле ввода
        EditText editText = findViewById(R.id.editTextInput);

        // 2. Получаем текст и считаем длину
        String inputText = editText.getText().toString();
        int charCount = inputText.length();

        // 3. Формируем сообщение по шаблону
        String message = String.format(
                "СТУДЕНТ № %s ГРУППА %s Количество символов - %d",
                STUDENT_NUMBER,
                GROUP_NUMBER,
                charCount
        );

        // 4. Показываем Toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}