package ru.mirea.ilmetoviv.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.*;

import java.util.UUID;

public class FragmentBackgroundTask extends Fragment {

    private static final String UNIQUE_WORK_NAME = "mirea_background_task";

    private Button btnStartTask;
    private Button btnCancelTask;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private UUID workId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background_task,
                container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnStartTask = view.findViewById(R.id.btnStartTask);
        btnCancelTask = view.findViewById(R.id.btnCancelTask);
        progressBar = view.findViewById(R.id.progressBar);
        tvStatus = view.findViewById(R.id.tvStatus);

        btnStartTask.setOnClickListener(v -> startBackgroundTask());
        btnCancelTask.setOnClickListener(v -> cancelBackgroundTask());

        // Наблюдаем за статусом задачи
        observeWorkStatus();
    }

    private void startBackgroundTask() {
        // Входные данные для Worker (опционально)
        Data inputData = new Data.Builder()
                .putString(BackgroundTaskWorker.KEY_INPUT_MESSAGE, "Запуск из Fragment")
                .build();

        // Настраиваем запрос на выполнение
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackgroundTaskWorker.class)
                .setInputData(inputData)
                .addTag("mirea_task") // для удобной отмены по тегу
                .build();

        workId = workRequest.getId();

        // Запускаем с уникальным именем (предотвращает дубли)
        WorkManager.getInstance(requireContext())
                .enqueueUniqueWork(
                        UNIQUE_WORK_NAME,
                        ExistingWorkPolicy.KEEP, // не перезапускать, если уже выполняется
                        workRequest
                );

        tvStatus.setText("Статус: Задача запущена...");
        progressBar.setIndeterminate(true);
        btnStartTask.setEnabled(false);
    }

    private void cancelBackgroundTask() {
        if (workId != null) {
            WorkManager.getInstance(requireContext()).cancelWorkById(workId);
            // Или по тегу: .cancelAllWorkByTag("mirea_task");
            tvStatus.setText("Статус: Отмена...");
        }
    }

    private void observeWorkStatus() {
        WorkManager.getInstance(requireContext())
                .getWorkInfoByIdLiveData(workId)
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo == null) return;

                        switch (workInfo.getState()) {
                            case ENQUEUED:
                                tvStatus.setText("Статус: В очереди...");
                                break;
                            case RUNNING:
                                tvStatus.setText("Статус: Выполняется...");
                                progressBar.setIndeterminate(true);
                                break;
                            case SUCCEEDED:
                                tvStatus.setText("Статус: ✓ Успешно завершено");
                                progressBar.setIndeterminate(false);
                                progressBar.setProgress(100);
                                btnStartTask.setEnabled(true);

                                // Получаем результат из Worker
                                String result = workInfo.getOutputData()
                                        .getString(BackgroundTaskWorker.KEY_OUTPUT_RESULT);
                                if (result != null) {
                                    Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case FAILED:
                                tvStatus.setText("Статус: ✗ Ошибка");
                                progressBar.setIndeterminate(false);
                                btnStartTask.setEnabled(true);
                                break;
                            case CANCELLED:
                                tvStatus.setText("Статус: Отменено");
                                progressBar.setIndeterminate(false);
                                btnStartTask.setEnabled(true);
                                break;
                            case BLOCKED:
                                tvStatus.setText("Статус: Ожидание условий...");
                                break;
                        }
                    }
                });
    }
}