package ru.mirea.ilmetoviv.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим TextView для отображения результатов
        textViewResult = findViewById(R.id.textViewResult);
    }

    // 🔹 Обработчик кнопки "Выбрать время"
    public void onShowTimePicker(View view) {
        DialogFragment timeDialog = new MyTimeDialogFragment();
        timeDialog.show(getSupportFragmentManager(), "time_picker");
    }

    // 🔹 Обработчик кнопки "Выбрать дату"
    public void onShowDatePicker(View view) {
        DialogFragment dateDialog = new MyDateDialogFragment();
        dateDialog.show(getSupportFragmentManager(), "date_picker");
    }

    // 🔹 Обработчик кнопки "Показать прогресс"
    public void onShowProgressDialog(View view) {
        DialogFragment progressDialog = new MyProgressDialogFragment();
        progressDialog.show(getSupportFragmentManager(), "progress_dialog");
    }

    // 🔹 Обработчик кнопки "Показать Snackbar"
    public void onShowSnackbar(View view) {
        Snackbar snackbar = Snackbar.make(
                view,                           // View, относительно которого позиционировать
                "Это Snackbar! Нажмите для действия",  // Текст сообщения
                Snackbar.LENGTH_LONG            // Длительность
        );

        // Добавляем действие (кнопку) в Snackbar
        snackbar.setAction("ОК", v ->
                Toast.makeText(this, "Нажато действие в Snackbar", Toast.LENGTH_SHORT).show()
        );

        // Меняем цвет текста действия (опционально)
        snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_blue_light, null));

        snackbar.show();
    }

    // 🔹 Методы-обработчики результатов из диалогов

    public void onTimeSelected(String time) {
        textViewResult.setText("Выбрано время: " + time);
    }

    public void onDateSelected(String date) {
        textViewResult.setText("Выбрана дата: " + date);
    }

    public void onProgressComplete() {
        textViewResult.setText("Загрузка завершена! ✓");
        Toast.makeText(this, "Прогресс завершён", Toast.LENGTH_SHORT).show();
    }

    // 🔹 Обработчики для старого MyDialogFragment (если используете)
    public void onOkClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Иду дальше\"!", Toast.LENGTH_LONG).show();
    }

    public void onCancelClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"Нет\"!", Toast.LENGTH_LONG).show();
    }

    public void onNeutralClicked() {
        Toast.makeText(this, "Вы выбрали кнопку \"На паузе\"!", Toast.LENGTH_LONG).show();
    }
}