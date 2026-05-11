package ru.mirea.ilmetoviv.cryptoloader;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.google.android.material.snackbar.Snackbar;

import javax.crypto.SecretKey;

import ru.mirea.ilmetoviv.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private ActivityMainBinding binding;
    private static final int LOADER_ID = 1001;
    private static final String TAG = "CryptoMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonEncrypt.setOnClickListener(v -> {
            String input = binding.editTextInput.getText().toString().trim();

            if (input.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Введите текст", Snackbar.LENGTH_SHORT).show();
                return;
            }

            // 1. Генерируем ключ
            SecretKey key = ru.mirea.cryptoloader.CryptoLoader.generateKey();
            Log.d(TAG, "Ключ сгенерирован: " + key.getAlgorithm());

            // 2. Шифруем текст
            byte[] encrypted = ru.mirea.cryptoloader.CryptoLoader.encryptMsg(input, key);
            Log.d(TAG, "Текст зашифрован, длина: " + encrypted.length + " байт");

            // 3. Готовим Bundle для передачи в Loader
            Bundle args = new Bundle();
            args.putByteArray(ru.mirea.cryptoloader.CryptoLoader.ARG_CIPHER_TEXT, encrypted);
            args.putByteArray(ru.mirea.cryptoloader.CryptoLoader.ARG_KEY, key.getEncoded());

            // 4. Инициализируем Loader
            binding.textViewStatus.setText("Дешифровка в фоне...");
            LoaderManager.getInstance(this)
                    .initLoader(LOADER_ID, args, this);
        });
    }

    // ===== Реализация LoaderCallbacks =====

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: id=" + id);
        return new ru.mirea.cryptoloader.CryptoLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String decryptedText) {
        Log.d(TAG, "onLoadFinished: результат = " + decryptedText);

        binding.textViewStatus.setText("Готово!");

        // Показываем результат в SnackBar (требование задания)
        Snackbar.make(binding.getRoot(),
                        "Расшифровано: " + decryptedText,
                        Snackbar.LENGTH_LONG)
                .setAction("OK", null)
                .show();

        // Дополнительно — можно показать в Toast для надёжности
        Toast.makeText(this, "Результат: " + decryptedText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TAG, "onLoaderReset: очищаем ссылки");
        // В реальном проекте здесь нужно очистить ссылки на данные
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Предотвращаем утечку
    }
}