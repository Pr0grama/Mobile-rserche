package ru.mirea.ilmetoviv.mireaproject;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundTaskWorker extends Worker {

    private static final String TAG = "BackgroundTaskWorker";

    // Ключи для входных/выходных данных
    public static final String KEY_INPUT_MESSAGE = "input_message";
    public static final String KEY_OUTPUT_RESULT = "output_result";

    public BackgroundTaskWorker(@NonNull Context context,
                                @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    @NonNull
    public Result doWork() {
        // Получаем входные данные (опционально)
        String input = getInputData().getString(KEY_INPUT_MESSAGE);
        Log.d(TAG, "Задача запущена с параметром: " + input);

        try {
            // === ВАША ФОНОВАЯ ЛОГИКА ЗДЕСЬ ===
            // Например: загрузка данных, обработка файлов, синхронизация

            // Имитация длительной операции
            for (int i = 0; i <= 100; i += 10) {
                Thread.sleep(200);
                // setProgressAsync() доступен только в CoroutineWorker (Kotlin)
                // Для Java можно использовать уведомления или LiveData через базу
            }

            // Возвращаем результат
            return Result.success();

        } catch (InterruptedException e) {
            Log.e(TAG, "Задача прервана", e);
            return Result.retry(); // Повторить при сетевой ошибке
        } catch (Exception e) {
            Log.e(TAG, "Критическая ошибка", e);
            return Result.failure(); // Окончательный провал
        }
    }
}