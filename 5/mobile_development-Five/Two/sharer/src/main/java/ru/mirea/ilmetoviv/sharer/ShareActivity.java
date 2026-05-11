package ru.mirea.ilmetoviv.sharer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        TextView bookView = findViewById(R.id.textViewBook);
        EditText userInput = findViewById(R.id.editTextUserInput);
        Button btnSend = findViewById(R.id.btnSend);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String oldBook = extras.getString(MainActivity.KEY);
            if (oldBook != null) {
                bookView.setText(String.format("Было: %s", oldBook));
            }
        }

        btnSend.setOnClickListener(v -> {
            String newBookName = userInput.getText().toString().trim();

            if (newBookName.isEmpty()) {
                userInput.setError("Введите название книги");
                return;
            }

            Intent data = new Intent();
            data.putExtra(MainActivity.USER_MESSAGE, newBookName);

            Log.d("ShareActivity", "Отправляю обратно: " + newBookName);
            setResult(Activity.RESULT_OK, data);
            finish();
        });
    }
}