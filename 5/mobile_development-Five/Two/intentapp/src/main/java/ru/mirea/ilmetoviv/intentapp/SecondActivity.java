package ru.mirea.ilmetoviv.intentapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String time = getIntent().getStringExtra("TIME_KEY");
        if (time == null) time = "время не получено";

        int myNumber = 1;
        int square = myNumber * myNumber;

        String result = "КВАДРАТ ЗНАЧЕНИЯ МОЕГО НОМЕРА ПО СПИСКУ В ГРУППЕ СОСТАВЛЯЕТ ЧИСЛО "
                + square + ", а текущее время " + time;

        TextView textView = findViewById(R.id.textViewResult); // Должно совпадать с XML
        textView.setText(result);
    }
}