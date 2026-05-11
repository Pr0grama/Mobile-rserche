package ru.mirea.goncharovas.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyDateDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Месяцы от 0 до 11!
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Создаём DatePickerDialog
        return new DatePickerDialog(
                getActivity(),
                this,           // обработчик установки даты
                year, month, day // начальные значения
        );
    }

    // Вызывается при нажатии "Установить" в диалоге
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Форматируем дату (месяц +1, т.к. начинается с 0)
        String date = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year);

        // Показываем результат в MainActivity
        if (getActivity() != null) {
            ((MainActivity) getActivity()).onDateSelected(date);
        }

        Toast.makeText(getActivity(), "Выбрана дата: " + date, Toast.LENGTH_SHORT).show();
    }
}