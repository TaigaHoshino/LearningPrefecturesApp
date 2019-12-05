package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetaildResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaild_result);

        Intent intent = getIntent();

        String pft = intent.getStringExtra("pft");
        String pfto = intent.getStringExtra("pfto");
        int photo = intent.getIntExtra("photo", 0);
        String pftWrong = intent.getStringExtra("pftWrong");
        String pftoWrong = intent.getStringExtra("pftoWrong");

        ImageView ivPhoto = findViewById(R.id.ivDpResultPhoto);
        TextView tvPftPropAns = findViewById(R.id.tvDtResultPftPropAns);
        TextView tvPftoPropAns = findViewById(R.id.tvDtResultPftoPropAns);
        TextView tvPftAns = findViewById(R.id.tvDtResultPftAns);
        TextView tvPftoAns = findViewById(R.id.tvDtResultPftoAns);

        ivPhoto.setImageResource(photo);
        tvPftPropAns.setText(pft);
        tvPftoPropAns.setText(pfto);
        tvPftAns.setText(pftWrong);
        tvPftoAns.setText(pftoWrong);

    }

    public void backToListListener(View view){
        finish();
    }
}
