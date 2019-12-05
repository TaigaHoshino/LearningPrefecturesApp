package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class GameFinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finish);

        File database = getApplicationContext().getDatabasePath("prefecturesmemo.db");

        Intent intent = getIntent();

        int correctCounter = intent.getIntExtra("correctCounter", 0);

        TextView tvScore = findViewById(R.id.tvFnsScore);

        tvScore.setText(correctCounter + "/47");

    }

    public void backToMenuListener(View view){
        getApplicationContext().deleteDatabase("prefecturesmemo.db");

        Intent intent = new Intent(GameFinishActivity.this, MainActivity.class);

        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    public void gfScoreCheckClickListener(View view){
        Intent intent = new Intent(GameFinishActivity.this, CheckScoreActivity.class);

        startActivity(intent);
    }
}
