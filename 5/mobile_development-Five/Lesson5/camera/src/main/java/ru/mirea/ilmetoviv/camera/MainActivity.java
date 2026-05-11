package ru.mirea.ilmetoviv.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.mirea.ilmetoviv.camera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 100;
    private ActivityMainBinding binding;
    private Uri imageUri;
    private boolean isWork = false;

    // Лаунчер для получения результата от камеры
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                // Успешно: отображаем сохранённое изображение
                                binding.imageView.setImageURI(imageUri);
                                Toast.makeText(MainActivity.this, "Фото сохранено", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Съёмка отменена", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Проверяем и запрашиваем разрешения
        checkCameraPermission();

        // 2. Обработчик нажатия на ImageView
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWork) {
                    openCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Разрешение на камеру не получено", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkCameraPermission() {
        int cameraStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
            } else {
                isWork = false;
                Toast.makeText(this, "Без разрешения камера не работает", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            // Формируем URI через FileProvider
            String authority = getPackageName() + ".fileprovider";
            imageUri = FileProvider.getUriForFile(this, authority, photoFile);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // Даём временное право на запись файла
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            cameraLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка создания файла: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Создаёт временный файл в папке приложения */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures"); // Папка внутри приложения
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}