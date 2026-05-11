package ru.mirea.ilmetoviv.mireaproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.mirea.ilmetoviv.mireaproject.databinding.FragmentCameraTaskBinding;

public class FragmentCameraTask extends Fragment {

    private FragmentCameraTaskBinding binding;
    private Uri imageUri;

    // ✅ Инициализируем лаунчеры на уровне класса (до onResume/onViewCreated)
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK) {
                    if (imageUri != null) {
                        binding.ivPhoto.setImageURI(imageUri);
                        Toast.makeText(requireContext(), "Фото сохранено", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Съёмка отменена", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) openCamera();
                else Toast.makeText(requireContext(), "Разрешение на камеру отклонено", Toast.LENGTH_SHORT).show();
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnTakePhoto.setOnClickListener(v -> checkAndOpenCamera());
    }

    private void checkAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) == null) {
            Toast.makeText(requireContext(), "Приложение камеры не найдено", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File photoFile = createImageFile();
            String authority = requireContext().getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(requireContext(), authority, photoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Ошибка создания файла: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "NOTE_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir("Pictures");
        if (storageDir == null) throw new IOException("Внешнее хранилище недоступно");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}