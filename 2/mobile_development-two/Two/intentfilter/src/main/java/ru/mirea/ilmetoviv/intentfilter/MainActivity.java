package ru.mirea.goncharovas.intentfilter; // ← Ваш пакет!

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;import ru.mirea.goncharovas.intentfilter.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Обработчик кнопки "Открыть сайт"
     * Запускает браузер с адресом MIREA
     */
    public void onOpenWebsiteClick(View view) {
        // Создаём URI с адресом сайта (убираем лишние пробелы)
        Uri address = Uri.parse("https://www.mirea.ru/");

        // Создаём неявное намерение с действием ACTION_VIEW
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);

        // Проверяем, есть ли приложение для обработки этого намерения
        if (openLinkIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openLinkIntent);
        } else {
            Toast.makeText(this, "Браузер не найден", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Обработчик кнопки "Поделиться ФИО"
     * Открывает диалог выбора приложения для отправки текста
     */
    public void onShareDataClick(View view) {
        // Создаём намерение с действием ACTION_SEND (поделиться)
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Указываем тип данных — простой текст
        shareIntent.setType("text/plain");

        // Добавляем тему и текст (замените на свои данные)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "ФАМИЛИЯ ИМЯ ОТЧЕСТВО");

        // Создаём диалог выбора приложения (Chooser)
        startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"));
    }
}