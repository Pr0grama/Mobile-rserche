package ru.mirea.ilmetoviv.serviceapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "PlayerService";
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Не поддерживаем привязку — только запуск через startService
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: сервис создан");

        // 1. Создаём канал уведомлений (обязательно для Android 8+)
        createNotificationChannel();

        // 2. Инициализируем MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(false); // Не повторять

        // 3. Обработчик окончания воспроизведения
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.d(TAG, "onCompletion: трек завершён");
            stopForeground(STOP_FOREGROUND_REMOVE);
            stopSelf();
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: начинаем воспроизведение");

        // 4. Создаём уведомление
        Notification notification = buildNotification();

        // 5. Запускаем сервис в режиме Foreground
        startForeground(NOTIFICATION_ID, notification);

        // 6. Начинаем воспроизведение
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        // Возвращаем START_STICKY: если система убьёт сервис — перезапустит
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: сервис уничтожается");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopForeground(STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }

    // ===== Вспомогательные методы =====

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Студент: [Твоя Фамилия] — Музыкальный плеер", // ← УКАЖИ СВОЁ НАЗВАНИЕ!
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Канал для отображения статуса воспроизведения");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        // Intent для возврата в приложение при клике на уведомление
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("🎵 Музыкальный плеер")
                .setContentText("Воспроизводится: Гончаров Андрей — LoonBoon") // ← УКАЖИ СВОЁ!
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true) // Нельзя смахнуть уведомление
                .build();
    }
}