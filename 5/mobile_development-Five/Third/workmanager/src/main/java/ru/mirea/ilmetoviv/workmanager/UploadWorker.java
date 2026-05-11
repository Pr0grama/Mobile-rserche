package ru.mirea.ilmetoviv.workmanager;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {

    public static final String TAG = "UploadWorker";

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: задача запущена в потоке: " + Thread.currentThread().getName());

        // Имитация «тяжёлой» операции (загрузка файла, синхронизация и т.д.)
        try {
            // 10 секунд — как в задании
            TimeUnit.SECONDS.sleep(10);

            // Здесь могла бы быть реальная работа:
            // - загрузка файла на сервер
            // - сохранение в БД
            // - отправка аналитики

        } catch (InterruptedException e) {
            Log.e(TAG, "Задача прервана", e);
            return Result.failure(); // ← Сообщаем WorkManager о неудаче
        }

        Log.d(TAG, "doWork: задача успешно завершена");
        return Result.success(); // ← Сообщаем об успехе
    }
}