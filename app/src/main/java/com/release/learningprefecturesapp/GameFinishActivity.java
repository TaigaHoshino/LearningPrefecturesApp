package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameFinishActivity extends AppCompatActivity {

    InterstitialAdGenerator interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("リザルト");
        setContentView(R.layout.activity_game_finish);

        interstitialAd = new InterstitialAdGenerator(getApplicationContext());

        Intent intent = getIntent();

        int correctCounter = intent.getIntExtra("correctCounter", 0);

        TextView tvScore = findViewById(R.id.tvFnsScore);

        tvScore.setText(correctCounter + "/" + intent.getIntExtra("numberOfQuestions", 0));

    }

    public void backToMenuListener(View view){
        getApplicationContext().deleteDatabase("prefecturesmemo.db");

        Intent intent = new Intent(GameFinishActivity.this, MainActivity.class);

        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        interstitialAd.showInterstitial();
    }

    public void gfScoreCheckClickListener(View view){
        Intent intent = new Intent(GameFinishActivity.this, CheckScoreActivity.class);

        startActivity(intent);
    }
}
