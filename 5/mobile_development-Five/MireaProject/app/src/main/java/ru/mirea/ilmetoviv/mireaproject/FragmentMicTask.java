package ru.mirea.ilmetoviv.mireaproject;

import android.Manifest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import ru.mirea.ilmetoviv.mireaproject.databinding.FragmentMicTaskBinding;

public class FragmentMicTask extends Fragment {

    private FragmentMicTaskBinding binding;
    private MediaRecorder recorder;
    private Handler handler;
    private Runnable amplitudeUpdater;
    private boolean isRecording = false;

    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMicTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        handler = new Handler(Looper.getMainLooper());

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) startRecording();
                    else Toast.makeText(requireContext(), "Разрешение на микрофон отклонено", Toast.LENGTH_SHORT).show();
                });

        binding.btnRecord.setOnClickListener(v -> {
            if (!isRecording) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    startRecording();
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
                }
            } else {
                stopRecording();
            }
        });
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(requireContext().getExternalFilesDir("Music") + "/level_test.3gp");
            recorder.prepare();
            recorder.start();
            isRecording = true;
            binding.btnRecord.setText("Остановить");
            binding.tvStatus.setText("Измерение уровня шума...");

            amplitudeUpdater = new Runnable() {
                @Override
                public void run() {
                    if (recorder != null && isRecording) {
                        int amplitude = recorder.getMaxAmplitude();
                        binding.progressBar.setProgress(amplitude);
                        binding.tvStatus.setText("Уровень: " + amplitude + " (макс. 32767)");
                        handler.postDelayed(this, 100);
                    }
                }
            };
            handler.post(amplitudeUpdater);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка инициализации микрофона", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            try { recorder.stop(); } catch (RuntimeException ignored) {}
            recorder.release();
            recorder = null;
        }
        handler.removeCallbacks(amplitudeUpdater);
        isRecording = false;
        binding.btnRecord.setText("Измерить шум");
        binding.tvStatus.setText("Измерение завершено");
        binding.progressBar.setProgress(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRecording();
    }
}