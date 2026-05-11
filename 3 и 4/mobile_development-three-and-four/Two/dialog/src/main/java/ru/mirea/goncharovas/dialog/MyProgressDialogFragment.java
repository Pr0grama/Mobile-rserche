package ru.mirea.goncharovas.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Создаём ProgressDialog
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Загрузка");
        progressDialog.setMessage("Пожалуйста, подождите...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false); // Нельзя закрыть нажатием вне диалога

        // Автоматически закрываем диалог через 3 секунды (имитация загрузки)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getDialog() != null && getDialog().isShowing()) {
                getDialog().dismiss();

                // Уведомляем MainActivity о завершении
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).onProgressComplete();
                }
            }
        }, 3000);

        return progressDialog;
    }
}