package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onStart(){
        File database = getApplicationContext().getDatabasePath("prefecturesmemo.db");
        Button btContinue = findViewById(R.id.btMenuContinue);
        Button btCheckScore = findViewById(R.id.btMenuCheckScore);

        if(database.exists()) {
            btContinue.setEnabled(true);

            DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try{

                Cursor cursor = db.query("pftsresultmemo", new String[] {"_id"},
                        null, null, null, null, null);

                if(cursor.moveToFirst()){
                    btCheckScore.setEnabled(true);
                }
                else{
                    btCheckScore.setEnabled(false);
                }
            }
            finally{
                db.close();
            }
        }
        else{
            btContinue.setEnabled(false);
            btCheckScore.setEnabled(false);

        }
        super.onStart();
    }

    public void menuStartClickListener(View view) {

        getApplicationContext().deleteDatabase("prefecturesmemo.db");

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        startActivity(intent);

    }

    public void menuContinueClickListener(View view) {

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        startActivity(intent);
    }

    public void checkScoreClickListener(View view){
        Intent intent = new Intent(MainActivity.this, CheckScoreActivity.class);

        startActivity(intent);
    }

}
