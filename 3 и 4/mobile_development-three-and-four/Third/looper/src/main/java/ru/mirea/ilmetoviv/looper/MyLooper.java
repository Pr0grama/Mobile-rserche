package ru.mirea.ilmetoviv.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {
    public Handler mHandler;
    private final Handler mainHandler;
    private static final String TAG = "MyLooper";

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: поток запущен, имя: " + Thread.currentThread().getName());

        // 1. Подготовка Looper в этом потоке
        Looper.prepare();

        // 2. Создаём Handler, привязанный к этому Looper
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Получаем данные из сообщения
                Bundle data = msg.getData();
                String ageStr = data.getString("age");
                String occupation = data.getString("occupation");

                Log.d(TAG, "Получено: возраст=" + ageStr + ", профессия=" + occupation);

                // 3. Имитация задержки: возраст * 1000 мс
                int age = 0;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Некорректный возраст", e);
                }

                try {
                    Log.d(TAG, "Задержка на " + (age * 1000) + " мс...");
                    Thread.sleep(age * 1000L); // ← ключевая строка задания
                } catch (InterruptedException e) {
                    Log.e(TAG, "Прервано", e);
                    Thread.currentThread().interrupt();
                }

                // 4. Обработка: считаем длину профессии
                int lettersCount = occupation != null ? occupation.length() : 0;
                String result = String.format(
                        "Пользователь (%d лет) работает как '%s'. В слове '%s' — %d букв.",
                        age, occupation, occupation, lettersCount
                );

                // 5. Отправляем результат обратно в главный поток
                Message response = Message.obtain();
                Bundle responseBundle = new Bundle();
                responseBundle.putString("result", result);
                response.setData(responseBundle);
                mainHandler.sendMessage(response);

                Log.d(TAG, "Обработка завершена, результат отправлен в UI");
            }
        };

        // 6. Запускаем цикл обработки сообщений
        Looper.loop();

        Log.d(TAG, "Looper остановлен");
    }
}