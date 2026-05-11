package ru.mirea.ilmetoviv.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import ru.mirea.ilmetoviv.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 200;
    private static final String TAG = "AudioRecord";

    private String recordFilePath;
    private MediaRecorder recorder;
    private MediaPlayer player;

    private boolean isWork = false;
    private boolean isRecording = false;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Путь к файлу в приватной директории приложения
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (storageDir != null) {
            recordFilePath = new File(storageDir, "audiorecordtest.3gp").getAbsolutePath();
        }

        checkPermissions();
        setupButtons();
    }

    /** 1. Проверка и запрос разрешений (по методичке) */
    private void checkPermissions() {
        int recordPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (recordPerm == PackageManager.PERMISSION_GRANTED && storagePerm == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                isWork = false;
                Toast.makeText(this, "Без разрешений запись невозможна", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /** 2. Настройка кнопок и управление состояниями */
    private void setupButtons() {
        binding.btnRecord.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(this, "Разрешение на микрофон не дано", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isRecording) {
                stopRecording();
                binding.btnRecord.setText("Начать запись");
                isRecording = false;
                binding.btnPlay.setEnabled(true);
            } else {
                if (isPlaying) { // Останавливаем воспроизведение, если оно идёт
                    stopPlaying();
                    isPlaying = false;
                    binding.btnPlay.setText("Воспроизвести");
                }
                startRecording();
                binding.btnRecord.setText("Остановить запись");
                isRecording = true;
                binding.btnPlay.setEnabled(false);
            }
        });

        binding.btnPlay.setOnClickListener(v -> {
            if (!isWork) return;

            File f = new File(recordFilePath);
            if (!f.exists()) {
                Toast.makeText(this, "Сначала сделайте запись", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isPlaying) {
                stopPlaying();
                binding.btnPlay.setText("Воспроизвести");
                isPlaying = false;
                binding.btnRecord.setEnabled(true);
            } else {
                startPlaying();
                binding.btnPlay.setText("Остановить");
                isPlaying = true;
                binding.btnRecord.setEnabled(false);
            }
        });
    }

    /** 3. MediaRecorder: инициализация и запуск */
    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(recordFilePath);
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "Запись начата");
        } catch (IOException e) {
            Log.e(TAG, "Ошибка prepare()", e);
            Toast.makeText(this, "Не удалось начать запись", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                // Игнорируем, если stop() вызван до start()
            } finally {
                recorder.release();
                recorder = null;
                Log.d(TAG, "Запись остановлена, файл: " + recordFilePath);
            }
        }
    }

    /** 4. MediaPlayer: воспроизведение */
    private void startPlaying() {
        try {
            player = new MediaPlayer();
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();

            // Авто-сброс UI по окончании трека
            player.setOnCompletionListener(mp -> {
                isPlaying = false;
                runOnUiThread(() -> {
                    binding.btnPlay.setText("Воспроизвести");
                    binding.btnRecord.setEnabled(true);
                });
            });
            Log.d(TAG, "Воспроизведение начато");
        } catch (IOException e) {
            Log.e(TAG, "Ошибка prepare()", e);
            Toast.makeText(this, "Не удалось воспроизвести файл", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            Log.d(TAG, "Воспроизведение остановлено");
        }
    }

    /** Очистка ресурсов при уничтожении Activity */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
        stopPlaying();
    }
}