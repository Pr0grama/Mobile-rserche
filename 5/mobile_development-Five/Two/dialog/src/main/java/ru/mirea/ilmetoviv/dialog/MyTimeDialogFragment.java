package ru.mirea.ilmetoviv.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyTimeDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Получаем текущее время
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Создаём TimePickerDialog (24-часовой формат)
        return new TimePickerDialog(
                getActivity(),
                this,           // обработчик установки времени
                hour, minute,   // начальные значения
                true            // 24-часовой формат (false = 12-часовой с AM/PM)
        );
    }

    // Вызывается при нажатии "Установить" в диалоге
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Форматируем время для отображения
        String time = String.format("%02d:%02d", hourOfDay, minute);

        // Показываем результат в MainActivity
        if (getActivity() != null) {
            ((MainActivity) getActivity()).onTimeSelected(time);
        }

        Toast.makeText(getActivity(), "Выбрано время: " + time, Toast.LENGTH_SHORT).show();
    }
}