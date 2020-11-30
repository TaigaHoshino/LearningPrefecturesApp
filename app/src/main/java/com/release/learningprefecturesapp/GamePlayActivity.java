package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayActivity extends AppCompatActivity {

    List<Map<String, Object>> _pftList = new ArrayList<>();
    private int _counter = 0;
    private int _correctCounter = 0;
    private Button _btAnswer;
    private Button _btNext;
    private EditText _etPft;
    private EditText _etPfto;
    private ImageView _pftPhoto;
    private TextView _tvCorrectRatio;
    private TextView _tvNumOfAnswers;
    private int _mp3correct;
    private int _mp3wrong;
    private int _numberOfQuestions;
    private int _numberOfAnsweredQuestions = 0;
    private SoundPool _soundPool;
    private boolean _isDataEmpty = false;
    InterstitialAdGenerator interstitialAd;

    public void playMp3correct(){_soundPool.play(_mp3correct, 1f, 1f, 0, 0, 1f);}
    public void playMp3wrong(){_soundPool.play(_mp3wrong, 1f, 1f, 0, 0, 1f);}

    @Override
    public void onStart(){

        enableCheckScoreButton();

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_game_play);
        _btAnswer = findViewById(R.id.btGpAns);
        _btNext = findViewById(R.id.btGpNext);
        _etPft = findViewById(R.id.etGpPft);
        _etPfto = findViewById(R.id.etGpPfto);
        _pftPhoto = findViewById(R.id.ivGpPhoto);
        _tvCorrectRatio = findViewById(R.id.tvGpCorrectRatio);
        _tvNumOfAnswers = findViewById(R.id.tvGpNumOfAns);
        interstitialAd = new InterstitialAdGenerator(getApplicationContext());

        _soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        _mp3correct = _soundPool.load(this, R.raw.correct_sound, 1);
        _mp3wrong = _soundPool.load(this, R.raw.wrong_sound, 1);
        File database = getApplicationContext().getDatabasePath("prefecturesmemo.db");
        _etPfto.setEnabled(false);
        Map<String, Object> data;

        if(database.exists()){
            LoadGame();
        }
        else {
            Intent intent = getIntent();
            if(intent.getIntExtra("gameMode", 1) == 1){
                createRamdomMode();
            }else if(intent.getIntExtra("gameMode", 1) == 2){
                createRegionMode();
            }else{
                createWeakPointMode();
            }

        }

        data = _pftList.get(_counter);
        _btNext.setEnabled(false);
        _pftPhoto.setImageResource((int)data.get("photo"));
        _btAnswer.setOnClickListener(new answerClickListener());
        _tvNumOfAnswers.setText(_counter + " / " + _numberOfQuestions);
        float ratio;

        if(_counter == 0){
            ratio = 0;
        }else{
            ratio = (float)_correctCounter / _counter * 100;
        }

        _tvCorrectRatio.setText((int)ratio + "％");


    }

    public class answerClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            Map<String, Object> data = _pftList.get(_counter);
            ImageView ivRltPhoto = findViewById(R.id.ivGpResult);

            ivRltPhoto.setVisibility(View.VISIBLE);

            if (_etPft.getText().toString().equals(data.get("pft"))) {
                ivRltPhoto.setImageResource(R.drawable.correct);
                playMp3correct();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ImageView ivRltPhoto = findViewById(R.id.ivGpResult);
                        ivRltPhoto.setVisibility(View.INVISIBLE);

                    }
                }, 500);
                _etPft.setEnabled(false);
                _etPfto.setEnabled(true);
                _btAnswer.setOnClickListener(new answerClickListener2());

            } else {
                ivRltPhoto.setImageResource(R.drawable.wrong);
                playMp3wrong();
                _etPft.setEnabled(false);
                _etPfto.setEnabled(false);
                _btAnswer.setEnabled(false);
                _btNext.setEnabled(true);
                _counter++;
                DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                try{
                    String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo, isCorrect, pftWrong, pftoWrong) VALUES (?, ?, ?, ?, ?, ?)";

                    SQLiteStatement stmt = db.compileStatement(sqlInsert);
                    stmt.bindString(1, (String) data.get("pft"));
                    stmt.bindString(2, (String) data.get("pfto"));
                    stmt.bindLong(3, (int) data.get("photo"));
                    stmt.bindLong(4, 0);
                    stmt.bindString(5, _etPft.getText().toString());
                    stmt.bindString(6, "");

                    stmt.executeInsert();

                    String sqlDelete = "DELETE FROM countermemo WHERE _id = ?";
                    stmt = db.compileStatement(sqlDelete);
                    stmt.bindLong(1, 0);

                    stmt.executeUpdateDelete();

                    sqlInsert = "INSERT INTO countermemo (_id, counter, correctCounter, numOfQuestions) VALUES (?, ?, ?, ?)";

                    stmt = db.compileStatement(sqlInsert);
                    stmt.bindLong(1, 0);
                    stmt.bindLong(2, _counter);
                    stmt.bindLong(3, _correctCounter);
                    stmt.bindLong(4, _numberOfQuestions);

                    stmt.executeInsert();
                }
                finally{
                    db.close();
                }

                WrongCounter wrongCounter = new WrongCounter();
                wrongCounter.execute(String.valueOf(data.get("id")));

                //wrongCounter((int)data.get("id"));

                _etPft.setText((String)data.get("pft"));
                _etPfto.setText((String)data.get("pfto"));

                if(_counter == _numberOfQuestions){
                    Button btStop = findViewById(R.id.btGpStop);
                    btStop.setEnabled(false);
                }
            }

        }

    }

    public class answerClickListener2 implements View.OnClickListener{

        @Override
        public void onClick(View view){

            Map<String, Object> data = _pftList.get(_counter);
            ImageView ivRltPhoto = findViewById(R.id.ivGpResult);
            ivRltPhoto.setVisibility(View.VISIBLE);
            DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            SQLiteStatement stmt;

            if (_etPfto.getText().toString().equals(data.get("pfto"))) {
                ivRltPhoto.setImageResource(R.drawable.correct);
                playMp3correct();
                _etPfto.setEnabled(false);
                _correctCounter++;

                try{
                    String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo," +
                            " isCorrect) VALUES (?, ?, ?, ?)";

                    stmt = db.compileStatement(sqlInsert);
                    stmt.bindString(1, (String) data.get("pft"));
                    stmt.bindString(2, (String) data.get("pfto"));
                    stmt.bindLong(3, (int) data.get("photo"));
                    stmt.bindLong(4, 1);

                    stmt.executeInsert();
                }
                catch (SQLException e){
                    db.close();
                }
            }
            else {
                ivRltPhoto.setImageResource(R.drawable.wrong);
                playMp3wrong();
                _etPfto.setEnabled(false);

                try{
                    String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo," +
                            " isCorrect, pftoWrong) VALUES (?, ?, ?, ?, ?)";
                    stmt = db.compileStatement(sqlInsert);
                    stmt.bindString(1, (String) data.get("pft"));
                    stmt.bindString(2, (String) data.get("pfto"));
                    stmt.bindLong(3, (int) data.get("photo"));
                    stmt.bindLong(4, 0);
                    stmt.bindString(5, _etPfto.getText().toString());

                    stmt.executeInsert();
                }
                catch (SQLException e){
                    db.close();
                }

                WrongCounter wrongCounter = new WrongCounter();
                wrongCounter.execute(String.valueOf(data.get("id")));

                //wrongCounter((int)data.get("id"));
                _etPfto.setText((String)data.get("pfto"));

            }

            _btAnswer.setEnabled(false);
            _btNext.setEnabled(true);
            _counter++;

            try{
                String sqlDelete = "DELETE FROM countermemo WHERE _id = ?";
                stmt = db.compileStatement(sqlDelete);
                stmt.bindLong(1, 0);

                stmt.executeUpdateDelete();

                String sqlInsert = "INSERT INTO countermemo (_id, counter, correctCounter, numOfQuestions) VALUES (?, ?, ?, ?)";

                stmt = db.compileStatement(sqlInsert);
                stmt.bindLong(1, 0);
                stmt.bindLong(2, _counter);
                stmt.bindLong(3, _correctCounter);
                stmt.bindLong(4, _numberOfQuestions);

                stmt.executeInsert();
            }
            finally{
                db.close();
            }

            if(_counter == _numberOfQuestions){
                Button btStop = findViewById(R.id.btGpStop);
                btStop.setEnabled(false);
            }
        }
    }

    public void nextClickListener(View view){

        if(_isDataEmpty == true){
            Button btCheckScore = findViewById(R.id.btGpCheckScore);
            btCheckScore.setEnabled(true);
            _isDataEmpty = false;
        }

        if(_counter == _numberOfQuestions){//ゲーム終了
            Intent intent = new Intent(GamePlayActivity.this, GameFinishActivity.class);

            intent.putExtra("correctCounter", _correctCounter);
            intent.putExtra("numberOfQuestions", _numberOfQuestions);

            startActivity(intent);
        }
        else {
            Map<String, Object> data;
            ImageView ivRltPhoto = findViewById(R.id.ivGpResult);
            ivRltPhoto.setVisibility(View.INVISIBLE);
            data = _pftList.get(_counter);
            _pftPhoto.setImageResource((int) data.get("photo"));
            _etPft.setEnabled(true);
            _etPfto.setEnabled(false);
            _btAnswer.setEnabled(true);
            _btAnswer.setOnClickListener(new answerClickListener());
            _btNext.setEnabled(false);
            _etPft.setText("");
            _etPfto.setText("");
            _tvNumOfAnswers.setText(_counter + " / " + _numberOfQuestions);
            float ratio = (float)_correctCounter / _counter * 100;
            _tvCorrectRatio.setText((int)ratio + "％");

        }
    }

    public void stopClickListener(View view){
        finish();
        interstitialAd.showInterstitial();
    }

    public void gpCheckScoreClickListener(View view){
        Intent intent = new Intent(GamePlayActivity.this, CheckScoreActivity.class);

        startActivity(intent);
    }

    public void createRamdomMode(){

        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("pft", "北海道");
        data.put("pfto", "札幌市");
        data.put("photo", R.drawable.hokkaido);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 1);
        data.put("pft", "青森県");
        data.put("pfto", "青森市");
        data.put("photo", R.drawable.aomori);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 2);
        data.put("pft", "岩手県");
        data.put("pfto", "盛岡市");
        data.put("photo", R.drawable.iwate);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 3);
        data.put("pft", "秋田県");
        data.put("pfto", "秋田市");
        data.put("photo", R.drawable.akita);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 4);
        data.put("pft", "宮城県");
        data.put("pfto", "仙台市");
        data.put("photo", R.drawable.miyagi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 5);
        data.put("pft", "山形県");
        data.put("pfto", "山形市");
        data.put("photo", R.drawable.yamagata);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 6);
        data.put("pft", "福島県");
        data.put("pfto", "福島市");
        data.put("photo", R.drawable.hukushima);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 7);
        data.put("pft", "茨城県");
        data.put("pfto", "水戸市");
        data.put("photo", R.drawable.ibaraki);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 8);
        data.put("pft", "栃木県");
        data.put("pfto", "宇都宮市");
        data.put("photo", R.drawable.tochigi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 9);
        data.put("pft", "埼玉県");
        data.put("pfto", "さいたま市");
        data.put("photo", R.drawable.saitama);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 10);
        data.put("pft", "群馬県");
        data.put("pfto", "前橋市");
        data.put("photo", R.drawable.gunma);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 11);
        data.put("pft", "千葉県");
        data.put("pfto", "千葉市");
        data.put("photo", R.drawable.chiba);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 12);
        data.put("pft", "東京都");
        data.put("pfto", "東京");
        data.put("photo", R.drawable.tokyo);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 13);
        data.put("pft", "神奈川県");
        data.put("pfto", "横浜市");
        data.put("photo", R.drawable.kanagawa);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 14);
        data.put("pft", "新潟県");
        data.put("pfto", "新潟市");
        data.put("photo", R.drawable.niigata);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 15);
        data.put("pft", "富山県");
        data.put("pfto", "富山市");
        data.put("photo", R.drawable.toyama);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 16);
        data.put("pft", "石川県");
        data.put("pfto", "金沢市");
        data.put("photo", R.drawable.ishikawa);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 17);
        data.put("pft", "福井県");
        data.put("pfto", "福井市");
        data.put("photo", R.drawable.fukui);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 18);
        data.put("pft", "山梨県");
        data.put("pfto", "甲府市");
        data.put("photo", R.drawable.yamanashi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 19);
        data.put("pft", "長野県");
        data.put("pfto", "長野市");
        data.put("photo", R.drawable.nagano);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 20);
        data.put("pft", "岐阜県");
        data.put("pfto", "岐阜市");
        data.put("photo", R.drawable.gifu);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 21);
        data.put("pft", "静岡県");
        data.put("pfto", "静岡市");
        data.put("photo", R.drawable.shizuoka);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 22);
        data.put("pft", "愛知県");
        data.put("pfto", "名古屋市");
        data.put("photo", R.drawable.aichi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 23);
        data.put("pft", "三重県");
        data.put("pfto", "津市");
        data.put("photo", R.drawable.mie);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 24);
        data.put("pft", "滋賀県");
        data.put("pfto", "大津市");
        data.put("photo", R.drawable.shiga);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 25);
        data.put("pft", "京都府");
        data.put("pfto", "京都市");
        data.put("photo", R.drawable.kyoto);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 26);
        data.put("pft", "大阪府");
        data.put("pfto", "大阪市");
        data.put("photo", R.drawable.osaka);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 27);
        data.put("pft", "兵庫県");
        data.put("pfto", "神戸市");
        data.put("photo", R.drawable.hyougo);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 28);
        data.put("pft", "奈良県");
        data.put("pfto", "奈良市");
        data.put("photo", R.drawable.nara);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 29);
        data.put("pft", "和歌山県");
        data.put("pfto", "和歌山市");
        data.put("photo", R.drawable.wakayama);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 30);
        data.put("pft", "鳥取県");
        data.put("pfto", "鳥取市");
        data.put("photo", R.drawable.totori);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 31);
        data.put("pft", "島根県");
        data.put("pfto", "松江市");
        data.put("photo", R.drawable.shimane);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 32);
        data.put("pft", "岡山県");
        data.put("pfto", "岡山市");
        data.put("photo", R.drawable.okayama);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 33);
        data.put("pft", "広島県");
        data.put("pfto", "広島市");
        data.put("photo", R.drawable.hiroshima);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 34);
        data.put("pft", "山口県");
        data.put("pfto", "山口市");
        data.put("photo", R.drawable.yamaguchi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 35);
        data.put("pft", "徳島県");
        data.put("pfto", "徳島市");
        data.put("photo", R.drawable.tokushima);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 36);
        data.put("pft", "香川県");
        data.put("pfto", "高松市");
        data.put("photo", R.drawable.kagawa);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 37);
        data.put("pft", "愛媛県");
        data.put("pfto", "松山市");
        data.put("photo", R.drawable.ehime);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 38);
        data.put("pft", "高知県");
        data.put("pfto", "高知市");
        data.put("photo", R.drawable.kouchi);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 39);
        data.put("pft", "福岡県");
        data.put("pfto", "福岡市");
        data.put("photo", R.drawable.fukuoka);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 40);
        data.put("pft", "佐賀県");
        data.put("pfto", "佐賀市");
        data.put("photo", R.drawable.saga);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 41);
        data.put("pft", "長崎県");
        data.put("pfto", "長崎市");
        data.put("photo", R.drawable.nagasaki);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 42);
        data.put("pft", "熊本県");
        data.put("pfto", "熊本市");
        data.put("photo", R.drawable.kumamoto);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 43);
        data.put("pft", "大分県");
        data.put("pfto", "大分市");
        data.put("photo", R.drawable.ooita);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 44);
        data.put("pft", "宮崎県");
        data.put("pfto", "宮崎市");
        data.put("photo", R.drawable.miyazaki);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 45);
        data.put("pft", "鹿児島県");
        data.put("pfto", "鹿児島市");
        data.put("photo", R.drawable.kagoshima);
        _pftList.add(data);

        data = new HashMap<>();
        data.put("id", 46);
        data.put("pft", "沖縄県");
        data.put("pfto", "那覇市");
        data.put("photo", R.drawable.okinawa);
        _pftList.add(data);

        Collections.shuffle(_pftList);

        Intent intent = getIntent();

        _numberOfQuestions = intent.getIntExtra("numOfQuestions", 0);

        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stmt;
        int i;

        try{
            for(i=0;i<_pftList.size();i++) {

                data = _pftList.get(i);
                String sqlInsert = "INSERT INTO prefecturesmemo (_id, pft, pfto, photo) VALUES (?, ?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);
                stmt.bindLong(1, (int) data.get("id"));
                stmt.bindString(2, (String) data.get("pft"));
                stmt.bindString(3, (String) data.get("pfto"));
                stmt.bindLong(4, (int) data.get("photo"));

                stmt.executeInsert();
            }

            String sqlInsert = "INSERT INTO countermemo (_id, counter, correctCounter, numOfQuestions) VALUES (?, ?, ?, ?)";

            stmt = db.compileStatement(sqlInsert);
            stmt.bindLong(1, 0);
            stmt.bindLong(2, 0);
            stmt.bindLong(3, 0);
            stmt.bindLong(4, _numberOfQuestions);

            stmt.executeInsert();
        }
        finally{
            db.close();
        }

    }

    private void createRegionMode(){

        _numberOfQuestions = 0;

        Intent intent = getIntent();

        Map<String, Object> data = new HashMap<>();
//        data.put("id", 0);
//        data.put("pft", "北海道");
//        data.put("pfto", "札幌市");
//        data.put("photo", R.drawable.hokkaido);
//        _pftList.add(data);

        if(intent.getBooleanExtra("tohoku", false)){

            data = new HashMap<>();
            data.put("id", 1);
            data.put("pft", "青森県");
            data.put("pfto", "青森市");
            data.put("photo", R.drawable.aomori);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 2);
            data.put("pft", "岩手県");
            data.put("pfto", "盛岡市");
            data.put("photo", R.drawable.iwate);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 3);
            data.put("pft", "秋田県");
            data.put("pfto", "秋田市");
            data.put("photo", R.drawable.akita);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 4);
            data.put("pft", "宮城県");
            data.put("pfto", "仙台市");
            data.put("photo", R.drawable.miyagi);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 5);
            data.put("pft", "山形県");
            data.put("pfto", "山形市");
            data.put("photo", R.drawable.yamagata);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 6);
            data.put("pft", "福島県");
            data.put("pfto", "福島市");
            data.put("photo", R.drawable.hukushima);
            _pftList.add(data);

            _numberOfQuestions += 6;
        }

        if(intent.getBooleanExtra("kanto", false)){
            data = new HashMap<>();
            data.put("id", 7);
            data.put("pft", "茨城県");
            data.put("pfto", "水戸市");
            data.put("photo", R.drawable.ibaraki);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 8);
            data.put("pft", "栃木県");
            data.put("pfto", "宇都宮市");
            data.put("photo", R.drawable.tochigi);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 9);
            data.put("pft", "埼玉県");
            data.put("pfto", "さいたま市");
            data.put("photo", R.drawable.saitama);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 10);
            data.put("pft", "群馬県");
            data.put("pfto", "前橋市");
            data.put("photo", R.drawable.gunma);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 11);
            data.put("pft", "千葉県");
            data.put("pfto", "千葉市");
            data.put("photo", R.drawable.chiba);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 12);
            data.put("pft", "東京都");
            data.put("pfto", "東京");
            data.put("photo", R.drawable.tokyo);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 13);
            data.put("pft", "神奈川県");
            data.put("pfto", "横浜市");
            data.put("photo", R.drawable.kanagawa);
            _pftList.add(data);

            _numberOfQuestions += 7;
        }

        if(intent.getBooleanExtra("tyubu", false)){
            data = new HashMap<>();
            data.put("id", 14);
            data.put("pft", "新潟県");
            data.put("pfto", "新潟市");
            data.put("photo", R.drawable.niigata);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 15);
            data.put("pft", "富山県");
            data.put("pfto", "富山市");
            data.put("photo", R.drawable.toyama);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 16);
            data.put("pft", "石川県");
            data.put("pfto", "金沢市");
            data.put("photo", R.drawable.ishikawa);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 17);
            data.put("pft", "福井県");
            data.put("pfto", "福井市");
            data.put("photo", R.drawable.fukui);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 18);
            data.put("pft", "山梨県");
            data.put("pfto", "甲府市");
            data.put("photo", R.drawable.yamanashi);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 19);
            data.put("pft", "長野県");
            data.put("pfto", "長野市");
            data.put("photo", R.drawable.nagano);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 20);
            data.put("pft", "岐阜県");
            data.put("pfto", "岐阜市");
            data.put("photo", R.drawable.gifu);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 21);
            data.put("pft", "静岡県");
            data.put("pfto", "静岡市");
            data.put("photo", R.drawable.shizuoka);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 22);
            data.put("pft", "愛知県");
            data.put("pfto", "名古屋市");
            data.put("photo", R.drawable.aichi);
            _pftList.add(data);

            _numberOfQuestions += 9;
        }

        if(intent.getBooleanExtra("kinki", false)){
            data = new HashMap<>();
            data.put("id", 23);
            data.put("pft", "三重県");
            data.put("pfto", "津市");
            data.put("photo", R.drawable.mie);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 24);
            data.put("pft", "滋賀県");
            data.put("pfto", "大津市");
            data.put("photo", R.drawable.shiga);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 25);
            data.put("pft", "京都府");
            data.put("pfto", "京都市");
            data.put("photo", R.drawable.kyoto);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 26);
            data.put("pft", "大阪府");
            data.put("pfto", "大阪市");
            data.put("photo", R.drawable.osaka);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 27);
            data.put("pft", "兵庫県");
            data.put("pfto", "神戸市");
            data.put("photo", R.drawable.hyougo);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 28);
            data.put("pft", "奈良県");
            data.put("pfto", "奈良市");
            data.put("photo", R.drawable.nara);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 29);
            data.put("pft", "和歌山県");
            data.put("pfto", "和歌山市");
            data.put("photo", R.drawable.wakayama);
            _pftList.add(data);

            _numberOfQuestions += 7;
        }

        if(intent.getBooleanExtra("tyugoku", false)){

            data = new HashMap<>();
            data.put("id", 30);
            data.put("pft", "鳥取県");
            data.put("pfto", "鳥取市");
            data.put("photo", R.drawable.totori);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 31);
            data.put("pft", "島根県");
            data.put("pfto", "松江市");
            data.put("photo", R.drawable.shimane);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 32);
            data.put("pft", "岡山県");
            data.put("pfto", "岡山市");
            data.put("photo", R.drawable.okayama);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 33);
            data.put("pft", "広島県");
            data.put("pfto", "広島市");
            data.put("photo", R.drawable.hiroshima);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 34);
            data.put("pft", "山口県");
            data.put("pfto", "山口市");
            data.put("photo", R.drawable.yamaguchi);
            _pftList.add(data);

            _numberOfQuestions += 5;
        }

        if(intent.getBooleanExtra("shikoku", false)){
            data = new HashMap<>();
            data.put("id", 35);
            data.put("pft", "徳島県");
            data.put("pfto", "徳島市");
            data.put("photo", R.drawable.tokushima);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 36);
            data.put("pft", "香川県");
            data.put("pfto", "高松市");
            data.put("photo", R.drawable.kagawa);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 37);
            data.put("pft", "愛媛県");
            data.put("pfto", "松山市");
            data.put("photo", R.drawable.ehime);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 38);
            data.put("pft", "高知県");
            data.put("pfto", "高知市");
            data.put("photo", R.drawable.kouchi);
            _pftList.add(data);

            _numberOfQuestions += 4;
        }

        if(intent.getBooleanExtra("kyushu", false)){
            data = new HashMap<>();
            data.put("id", 39);
            data.put("pft", "福岡県");
            data.put("pfto", "福岡市");
            data.put("photo", R.drawable.fukuoka);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 40);
            data.put("pft", "佐賀県");
            data.put("pfto", "佐賀市");
            data.put("photo", R.drawable.saga);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 41);
            data.put("pft", "長崎県");
            data.put("pfto", "長崎市");
            data.put("photo", R.drawable.nagasaki);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 42);
            data.put("pft", "熊本県");
            data.put("pfto", "熊本市");
            data.put("photo", R.drawable.kumamoto);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 43);
            data.put("pft", "大分県");
            data.put("pfto", "大分市");
            data.put("photo", R.drawable.ooita);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 44);
            data.put("pft", "宮崎県");
            data.put("pfto", "宮崎市");
            data.put("photo", R.drawable.miyazaki);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 45);
            data.put("pft", "鹿児島県");
            data.put("pfto", "鹿児島市");
            data.put("photo", R.drawable.kagoshima);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("id", 46);
            data.put("pft", "沖縄県");
            data.put("pfto", "那覇市");
            data.put("photo", R.drawable.okinawa);
            _pftList.add(data);

            _numberOfQuestions += 8;
        }

        Collections.shuffle(_pftList);

        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stmt;
        int i;

        try{
            for(i=0;i<_pftList.size();i++) {

                data = _pftList.get(i);
                String sqlInsert = "INSERT INTO prefecturesmemo (_id, pft, pfto, photo) VALUES (?, ?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);
                stmt.bindLong(1, (int) data.get("id"));
                stmt.bindString(2, (String) data.get("pft"));
                stmt.bindString(3, (String) data.get("pfto"));
                stmt.bindLong(4, (int) data.get("photo"));

                stmt.executeInsert();
            }

            String sqlInsert = "INSERT INTO countermemo (_id, counter, correctCounter, numOfQuestions) VALUES (?, ?, ?, ?)";

            stmt = db.compileStatement(sqlInsert);
            stmt.bindLong(1, 0);
            stmt.bindLong(2, 0);
            stmt.bindLong(3, 0);
            stmt.bindLong(4, _numberOfQuestions);

            stmt.executeInsert();
        }
        finally {
            db.close();
        }
    }

    private void createWeakPointMode(){

        Map<String, Object> data;

        WrongCountDatabaseHelper WChelper = new WrongCountDatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = WChelper.getWritableDatabase();
        try{
            String orderBy = "wrongCounter DESC";

            Cursor cursor = db.query("wrongcounter", new String[] {"_id", "pft", "pfto", "photo", "wrongCounter"},
                    null, null, null, null, orderBy);

            cursor.moveToFirst();

            do{
                data = new HashMap<>();
                data.put("id", cursor.getInt(0));
                data.put("pft", cursor.getString(1));
                data.put("pfto", cursor.getString(2));
                data.put("photo", cursor.getInt(3));
                _pftList.add(data);
            }while(cursor.moveToNext());

        }
        finally{
            db.close();
        }

        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        db = helper.getWritableDatabase();
        SQLiteStatement stmt;
        int i;

        try{
            for(i=0;i<_pftList.size();i++) {

                data = _pftList.get(i);
                String sqlInsert = "INSERT INTO prefecturesmemo (_id, pft, pfto, photo) VALUES (?, ?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);
                stmt.bindLong(1, (int) data.get("id"));
                stmt.bindString(2, (String) data.get("pft"));
                stmt.bindString(3, (String) data.get("pfto"));
                stmt.bindLong(4, (int) data.get("photo"));

                stmt.executeInsert();
            }

            Intent intent = getIntent();

            _numberOfQuestions = intent.getIntExtra("numOfQuestions", 0);

            String sqlInsert = "INSERT INTO countermemo (_id, counter, correctCounter, numOfQuestions) VALUES (?, ?, ?, ?)";

            stmt = db.compileStatement(sqlInsert);
            stmt.bindLong(1, 0);
            stmt.bindLong(2, 0);
            stmt.bindLong(3, 0);
            stmt.bindLong(4, _numberOfQuestions);

            stmt.executeInsert();
        }
        finally{
            db.close();
        }



    }

    private class WrongCounter extends AsyncTask<String, String, String>{

        @Override
        public String doInBackground(String... params){
            int id = Integer.valueOf(params[0]);

            WrongCountDatabaseHelper WChelper = new WrongCountDatabaseHelper(GamePlayActivity.this);
            SQLiteDatabase db = WChelper.getWritableDatabase();

            int counter = 0;

            try{
                String sql = "SELECT * FROM wrongcounter WHERE _id=" + id;

                Cursor cursor = db.rawQuery(sql, null);

                while(cursor.moveToNext()){
                    int columnIndex = cursor.getColumnIndex("wrongCounter");

                    counter = cursor.getInt(columnIndex);

                }

                Log.d("カウンター", String.valueOf(counter));
                counter++;

                ContentValues insertValue = new ContentValues();
                insertValue.put("wrongCounter", counter);
                db.update("wrongcounter", insertValue, "_id=" + id, null);


//            String sqlDelete = "DELETE FROM wrongcounter WHERE _id=" + id;
//            SQLiteStatement stmt = db.compileStatement(sqlDelete);
//
//            stmt.executeUpdateDelete();
//
//            String sqlInsert = "INSERT INTO wrongcounter (_id, pft, pfto, photo, wrongCounter)" +
//                    " VALUES (?, ?, ?, ?, ?)";
//
//            stmt = db.compileStatement(sqlInsert);
//
//            stmt.bindLong(1, (int)data.get("id"));
//            stmt.bindString(2, (String)data.get("pft"));
//            stmt.bindString(3, (String)data.get("pfto"));
//            stmt.bindLong(4, (int)data.get("photo"));
//            stmt.bindLong(5, counter);

            }
            finally{
                db.close();
            }

            String result = "";

            return result;
        }
    }

//    private void wrongCounter(int id){
//        WrongCountDatabaseHelper WChelper = new WrongCountDatabaseHelper(GamePlayActivity.this);
//        SQLiteDatabase db = WChelper.getWritableDatabase();
//
//        int counter = 0;
//
//        try{
//            String sql = "SELECT * FROM wrongcounter WHERE _id=" + id;
//
//            Cursor cursor = db.rawQuery(sql, null);
//
//            while(cursor.moveToNext()){
//                int columnIndex = cursor.getColumnIndex("wrongCounter");
//
//                counter = cursor.getInt(columnIndex);
//
//            }
//
//            Log.d("カウンター", String.valueOf(counter));
//            counter++;
//
//            ContentValues insertValue = new ContentValues();
//            insertValue.put("wrongCounter", counter);
//            db.update("wrongcounter", insertValue, "_id=" + id, null);
//
//
//            String sqlDelete = "DELETE FROM wrongcounter WHERE _id=" + id;
//            SQLiteStatement stmt = db.compileStatement(sqlDelete);
//
//            stmt.executeUpdateDelete();
//
//            String sqlInsert = "INSERT INTO wrongcounter (_id, pft, pfto, photo, wrongCounter)" +
//                    " VALUES (?, ?, ?, ?, ?)";
//
//            stmt = db.compileStatement(sqlInsert);
//
//            stmt.bindLong(1, (int)data.get("id"));
//            stmt.bindString(2, (String)data.get("pft"));
//            stmt.bindString(3, (String)data.get("pfto"));
//            stmt.bindLong(4, (int)data.get("photo"));
//            stmt.bindLong(5, counter);
//
//        }finally{
//            db.close();
//        }
//    }

    public void enableCheckScoreButton() {
        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Button btCheckScore = findViewById(R.id.btGpCheckScore);

        try{

            Cursor cursor = db.query("pftsresultmemo", new String[] {"_id"},
                    null, null, null, null, null);

            if(cursor.moveToFirst()){
                btCheckScore.setEnabled(true);
                _isDataEmpty = false;
            }
            else{
                btCheckScore.setEnabled(false);
                _isDataEmpty = true;
            }
        }
        finally{
            db.close();
        }
    }

    public void LoadGame(){
        Map<String, Object> data;
        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try{
            Cursor cursor = db.query("prefecturesmemo", new String[] {"_id", "pft", "pfto", "photo"},
                    null, null, null, null, null);

            cursor.moveToFirst();

            do{
                data = new HashMap<>();
                data.put("id", cursor.getInt(0));
                data.put("pft", cursor.getString(1));
                data.put("pfto", cursor.getString(2));
                data.put("photo", cursor.getInt(3));
                _pftList.add(data);
            }while(cursor.moveToNext());

            cursor = db.query("countermemo", new String[] {"counter", "correctCounter", "numOfQuestions"},
                    null, null, null, null, null);

            if(cursor.moveToFirst()) {
                _counter = cursor.getInt(0);
                _correctCounter = cursor.getInt(1);
                _numberOfQuestions = cursor.getInt(2);
            }

        }
        finally{
            db.close();
        }
    }
}

