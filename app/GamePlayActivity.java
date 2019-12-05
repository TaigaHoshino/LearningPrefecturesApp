package com.release.learningprefecturesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class GamePlayActivity extends AppCompatActivity {

    List<Map<String, Object>> _pftList = new ArrayList<>();
    int _counter = 0;
    int _correctCounter = 0;
    Button _btAnswer;
    Button _btNext;
    EditText _etPft;
    EditText _etPfto;
    ImageView _pftPhoto;

    @Override
    public void onRestart(){

        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        _btAnswer = findViewById(R.id.btGpAns);
        _btNext = findViewById(R.id.btGpNext);
        _etPft = findViewById(R.id.etGpPft);
        _etPfto = findViewById(R.id.etGpPfto);
        _pftPhoto = findViewById(R.id.ivGpPhoto);
        File database = getApplicationContext().getDatabasePath("prefecturesmemo.db");
        _etPfto.setEnabled(false);
        Map<String, Object> data = new HashMap<>();

        if(database.exists()){

            DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try{
                Cursor cursor = db.query("prefecturesmemo", new String[] {"pft", "pfto", "photo", "correctCounter"},
                        null, null, null, null, null);

                cursor.moveToFirst();
                data.put("pft", cursor.getString(0));
                data.put("pfto", cursor.getString(1));
                data.put("photo", cursor.getInt(2));
                _pftList.add(data);
                _correctCounter = cursor.getInt(3);

                while(cursor.moveToNext()) {
                    data = new HashMap<>();
                    data.put("pft", cursor.getString(0));
                    data.put("pfto", cursor.getString(1));
                    data.put("photo", cursor.getInt(2));
                    _pftList.add(data);
                }

            }
            finally{
                db.close();
            }

            if(_counter >= _pftList.size()-1){
                Button btStop = findViewById(R.id.btGpStop);
                btStop.setEnabled(false);
            }
        }
        else {

            data.put("pft", "北海道");
            data.put("pfto", "札幌市");
            data.put("photo", R.drawable.hokkaido);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("pft", "青森県");
            data.put("pfto", "青森市");
            data.put("photo", R.drawable.aomori);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("pft", "岩手県");
            data.put("pfto", "盛岡市");
            data.put("photo", R.drawable.iwate);
            _pftList.add(data);

            data = new HashMap<>();
            data.put("pft", "秋田県");
            data.put("pfto", "秋田市");
            data.put("photo", R.drawable.akita);
            _pftList.add(data);

//            data = new HashMap<>();
//            data.put("pft", "宮城県");
//            data.put("pfto", "仙台市");
//            data.put("photo", R.drawable.miyagi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "山形県");
//            data.put("pfto", "山形市");
//            data.put("photo", R.drawable.yamagata);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "福島県");
//            data.put("pfto", "福島市");
//            data.put("photo", R.drawable.hukushima);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "茨城県");
//            data.put("pfto", "水戸市");
//            data.put("photo", R.drawable.ibaraki);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "栃木県");
//            data.put("pfto", "宇都宮市");
//            data.put("photo", R.drawable.tochigi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "埼玉県");
//            data.put("pfto", "さいたま市");
//            data.put("photo", R.drawable.saitama);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "群馬県");
//            data.put("pfto", "前橋市");
//            data.put("photo", R.drawable.gunma);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "千葉県");
//            data.put("pfto", "千葉市");
//            data.put("photo", R.drawable.chiba);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "東京都");
//            data.put("pfto", "東京");
//            data.put("photo", R.drawable.tokyo);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "神奈川県");
//            data.put("pfto", "横浜市");
//            data.put("photo", R.drawable.kanagawa);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "新潟県");
//            data.put("pfto", "新潟市");
//            data.put("photo", R.drawable.niigata);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "富山県");
//            data.put("pfto", "富山市");
//            data.put("photo", R.drawable.toyama);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "石川県");
//            data.put("pfto", "金沢市");
//            data.put("photo", R.drawable.ishikawa);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "福井県");
//            data.put("pfto", "福井市");
//            data.put("photo", R.drawable.fukui);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "山梨県");
//            data.put("pfto", "甲府市");
//            data.put("photo", R.drawable.yamanashi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "長野県");
//            data.put("pfto", "長野市");
//            data.put("photo", R.drawable.nagano);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "岐阜県");
//            data.put("pfto", "岐阜市");
//            data.put("photo", R.drawable.gifu);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "静岡県");
//            data.put("pfto", "静岡市");
//            data.put("photo", R.drawable.shizuoka);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "愛知県");
//            data.put("pfto", "名古屋市");
//            data.put("photo", R.drawable.aichi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "三重県");
//            data.put("pfto", "津市");
//            data.put("photo", R.drawable.mie);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "滋賀県");
//            data.put("pfto", "大津市");
//            data.put("photo", R.drawable.shiga);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "京都府");
//            data.put("pfto", "京都市");
//            data.put("photo", R.drawable.kyoto);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "大阪府");
//            data.put("pfto", "大阪市");
//            data.put("photo", R.drawable.osaka);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "兵庫県");
//            data.put("pfto", "神戸市");
//            data.put("photo", R.drawable.hyougo);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "奈良県");
//            data.put("pfto", "奈良市");
//            data.put("photo", R.drawable.nara);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "和歌山県");
//            data.put("pfto", "和歌山市");
//            data.put("photo", R.drawable.wakayama);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "鳥取県");
//            data.put("pfto", "鳥取市");
//            data.put("photo", R.drawable.totori);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "島根県");
//            data.put("pfto", "松江市");
//            data.put("photo", R.drawable.shimane);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "岡山県");
//            data.put("pfto", "岡山市");
//            data.put("photo", R.drawable.okayama);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "広島県");
//            data.put("pfto", "広島市");
//            data.put("photo", R.drawable.hiroshima);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "山口県");
//            data.put("pfto", "山口市");
//            data.put("photo", R.drawable.yamaguchi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "徳島県");
//            data.put("pfto", "徳島市");
//            data.put("photo", R.drawable.tokushima);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "香川県");
//            data.put("pfto", "高松市");
//            data.put("photo", R.drawable.kagawa);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "愛媛県");
//            data.put("pfto", "松山市");
//            data.put("photo", R.drawable.ehime);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "高知県");
//            data.put("pfto", "高知市");
//            data.put("photo", R.drawable.kouchi);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "福岡県");
//            data.put("pfto", "福岡市");
//            data.put("photo", R.drawable.fukuoka);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "佐賀県");
//            data.put("pfto", "佐賀市");
//            data.put("photo", R.drawable.saga);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "長崎県");
//            data.put("pfto", "長崎市");
//            data.put("photo", R.drawable.nagasaki);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "熊本県");
//            data.put("pfto", "熊本市");
//            data.put("photo", R.drawable.kumamoto);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "大分県");
//            data.put("pfto", "大分市");
//            data.put("photo", R.drawable.ooita);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "宮崎県");
//            data.put("pfto", "宮崎市");
//            data.put("photo", R.drawable.miyazaki);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "鹿児島県");
//            data.put("pfto", "鹿児島市");
//            data.put("photo", R.drawable.kagoshima);
//            _pftList.add(data);
//
//            data = new HashMap<>();
//            data.put("pft", "沖縄県");
//            data.put("pfto", "那覇市");
//            data.put("photo", R.drawable.okinawa);
//            _pftList.add(data);
//
//            Collections.shuffle(_pftList);

        }

        data = _pftList.get(0);
        _btNext.setEnabled(false);
        _pftPhoto.setImageResource((int)data.get("photo"));
        _btAnswer.setOnClickListener(new answerClickListener());

    }

    public class answerClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            Map<String, Object> data = _pftList.get(_counter);
            ImageView ivRltPhoto = findViewById(R.id.ivGpResult);

            ivRltPhoto.setVisibility(View.VISIBLE);

            if (_etPft.getText().toString().equals((String)data.get("pft"))) {
                ivRltPhoto.setImageResource(R.drawable.correct);
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
                _etPft.setText((String)data.get("pft"));
                _etPfto.setText((String)data.get("pfto"));
                _etPft.setEnabled(false);
                _etPfto.setEnabled(false);
                _btAnswer.setEnabled(false);
                _btNext.setEnabled(true);
                _counter++;
                DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo," +
                        " isCorrect, pftWrong) VALUES (?, ?, ?, ?, ?)";

                SQLiteStatement stmt = db.compileStatement(sqlInsert);
                stmt.bindString(1, (String) data.get("pft"));
                stmt.bindString(2, (String) data.get("pfto"));
                stmt.bindLong(3, (int) data.get("photo"));
                stmt.bindLong(4, 0);
                stmt.bindString(5, _etPft.getText().toString());
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

            if (_etPfto.getText().toString().equals((String)data.get("pfto"))) {
                ivRltPhoto.setImageResource(R.drawable.correct);
                _etPfto.setEnabled(false);
                _correctCounter++;
                String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo," +
                        " isCorrect) VALUES (?, ?, ?, ?)";

                SQLiteStatement stmt = db.compileStatement(sqlInsert);
                stmt.bindString(1, (String) data.get("pft"));
                stmt.bindString(2, (String) data.get("pfto"));
                stmt.bindLong(3, (int) data.get("photo"));
                stmt.bindLong(4, 1);
            }
            else {
                ivRltPhoto.setImageResource(R.drawable.wrong);
                _etPfto.setText((String)data.get("pfto"));
                _etPfto.setEnabled(false);
                String sqlInsert = "INSERT INTO pftsresultmemo (pft, pfto, photo," +
                        " isCorrect, pftoWrong) VALUES (?, ?, ?, ?, ?)";

                SQLiteStatement stmt = db.compileStatement(sqlInsert);
                stmt.bindString(1, (String) data.get("pft"));
                stmt.bindString(2, (String) data.get("pfto"));
                stmt.bindLong(3, (int) data.get("photo"));
                stmt.bindLong(4, 0);
                stmt.bindString(5, _etPfto.getText().toString());
            }

            _btAnswer.setEnabled(false);
            _btNext.setEnabled(true);
            _counter++;
        }
    }

    public void nextClickListener(View view){

        if(_counter >= _pftList.size()-1){
            Button btStop = findViewById(R.id.btGpStop);
            btStop.setEnabled(false);
        }

        if(_counter == _pftList.size()){
            Intent intent = new Intent(GamePlayActivity.this, GameFinishActivity.class);

            intent.putExtra("correctCounter", _correctCounter);

            startActivity(intent);
        }
        else {
            Map<String, Object> data = new HashMap<>();
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
        }
    }

    public void StopClickListener(View view){

        DatabaseHelper helper = new DatabaseHelper(GamePlayActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        try{
            String sql = "DROP TABLE IF EXISTS prefecturesmemo";
            db.execSQL(sql);

            Map<String, Object> data = _pftList.get(_counter);
            
            String sqlInsert = "INSERT INTO prefecturesmemo (pft, pfto, photo, correctCounter) VALUES (?, ?, ?, ?)";

            SQLiteStatement stmt = db.compileStatement(sqlInsert);
            stmt.bindString(1, (String) data.get("pft"));
            stmt.bindString(2, (String) data.get("pfto"));
            stmt.bindLong(3, (int) data.get("photo"));
            stmt.bindLong(4, _correctCounter);

            stmt.executeInsert();

            int i;

            for(i=_counter+1;i<_pftList.size();i++) {

                data = _pftList.get(i);
                sqlInsert = "INSERT INTO prefecturesmemo (pft, pfto, photo) VALUES (?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);
                stmt.bindString(1, (String) data.get("pft"));
                stmt.bindString(2, (String) data.get("pfto"));
                stmt.bindLong(3, (int) data.get("photo"));

                stmt.executeInsert();
            }

        }
        finally{
            db.close();
        }
        finish();
    }
}
