package ru.mirea.ilmetoviv.sharer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String KEY = "book_key";
    public static final String USER_MESSAGE = "user_message";

    private final ActivityResultLauncher<Intent> shareLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            String newBookName = result.getData().getStringExtra(USER_MESSAGE);

                            if (newBookName != null && !newBookName.isEmpty()) {
                                TextView bookView = findViewById(R.id.textViewBook);
                                // Обновляем текст на ПЕРВОМ экране новым значением
                                bookView.setText(String.format("Название Вашей любимой книги: %s", newBookName));
                                Log.d("MainActivity", "Обновил книгу: " + newBookName);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView bookView = findViewById(R.id.textViewBook);
        Button btnOpen = findViewById(R.id.btnOpenShare);

        btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
            String currentBook = bookView.getText().toString();
            intent.putExtra(KEY, currentBook);

            shareLauncher.launch(intent);
        });
    }
}
