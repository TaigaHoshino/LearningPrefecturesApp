package com.release.learningprefecturesapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button _tohokuButton;
    private Button _kantoButton;
    private Button _tyubuButton;
    private Button _kinkiButton;
    private Button _tyugokuButton;
    private Button _shikokuButton;
    private Button _kyushuButton;
    private RadioButton _rbRegionMode;
    private RadioButton _rbRandomMode;
    private RadioButton _rbWeakpointMode;
    private boolean isGameReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom);
        actionBar.setDisplayShowCustomEnabled(true);

//        requestWindowFeature(Window.FEATURE_LEFT_ICON);
//        requestWindowFeature(Window.FEATURE_RIGHT_ICON);

        setContentView(R.layout.activity_main);

//        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.actionbar_image_tokyo);
//        setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.actionbar_image_tokyo);

        File database = getApplicationContext().getDatabasePath("prefectureswrongcounter.db");

        if(!database.exists()){
            createWrongCounterDatabase();
        }

    }

    @Override
    public void onStart(){
        _rbWeakpointMode = findViewById(R.id.weakPointMode);
        isGameReady = false;
        File database = getApplicationContext().getDatabasePath("prefecturesmemo.db");
        Button btContinue = findViewById(R.id.btMenuContinue);
        Button btCheckScore = findViewById(R.id.btMenuCheckScore);
        Button btResetCount = findViewById(R.id.btMenuResetCount);
        _tohokuButton = findViewById(R.id.Tohoku);
        _kantoButton = findViewById(R.id.Kanto);
        _tyubuButton = findViewById(R.id.Tyubu);
        _kinkiButton = findViewById(R.id.Kinki);
        _tyugokuButton = findViewById(R.id.Tyugoku);
        _shikokuButton = findViewById(R.id.Shikoku);
        _kyushuButton = findViewById(R.id.KyuShu);
        _rbRegionMode = findViewById(R.id.regionMode);
        _rbRandomMode = findViewById(R.id.randomMode);

        _tohokuButton.setOnClickListener(new regionButtonClickListener());
        _kantoButton.setOnClickListener(new regionButtonClickListener());
        _tyubuButton.setOnClickListener(new regionButtonClickListener());
        _kinkiButton.setOnClickListener(new regionButtonClickListener());
        _tyugokuButton.setOnClickListener(new regionButtonClickListener());
        _shikokuButton.setOnClickListener(new regionButtonClickListener());
        _kyushuButton.setOnClickListener(new regionButtonClickListener());
        _rbRegionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _rbWeakpointMode.setChecked(false);
                _rbRegionMode.setChecked(true);
                _rbRandomMode.setChecked(false);

            }
        });

        _rbRandomMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _rbWeakpointMode.setChecked(false);
                _rbRegionMode.setChecked(false);
                _rbRandomMode.setChecked(true);
            }
        });

        _rbWeakpointMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _rbWeakpointMode.setChecked(true);
                _rbRegionMode.setChecked(false);
                _rbRandomMode.setChecked(false);
            }
        });

        btResetCount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                ResetCountConfirmDialogFragment dialogFragment = new ResetCountConfirmDialogFragment();

                dialogFragment.show(getSupportFragmentManager(), "ResetCountConfirmDialogFragment");
            }
        });

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

        if(_rbRandomMode.isChecked()){
            randomMode();
        }
        else if(_rbRegionMode.isChecked()){
            regionMode();
        }
        else if(_rbWeakpointMode.isChecked()){
            weakpointMode();
        }
        else{
            String warning = "モードを選択してください";

            Toast toast = Toast.makeText(MainActivity.this, warning, Toast.LENGTH_LONG);

            ToastMaster.setToast(toast);

        }

    }

    public class regionButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            view.setSelected(!view.isSelected());
        }
    }

    public void menuContinueClickListener(View view) {

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        startActivity(intent);
    }

    public void checkScoreClickListener(View view){
        Intent intent = new Intent(MainActivity.this, CheckScoreActivity.class);

        startActivity(intent);
    }

    private void randomMode(){
        Spinner configSpinner = findViewById(R.id.spGameConfig);

        int numOfQuestions = 0;

        switch (configSpinner.getSelectedItemPosition()){
            case 0:
                numOfQuestions = 10;
                break;

            case 1:
                numOfQuestions = 20;
                break;

            case 2:
                numOfQuestions = 30;
                break;

            case 3:
                numOfQuestions = 47;
                break;
        }

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        intent.putExtra("gameMode", 1);
        intent.putExtra("numOfQuestions", numOfQuestions);
        getApplicationContext().deleteDatabase("prefecturesmemo.db");
        startActivity(intent);
    }

    private void regionMode(){

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        if(_tohokuButton.isSelected()){
            intent.putExtra("tohoku", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("tohoku", false);
        }

        if(_kantoButton.isSelected()){
            intent.putExtra("kanto", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("kanto", false);
        }

        if(_tyubuButton.isSelected()){
            intent.putExtra("tyubu", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("tyubu", false);
        }

        if(_kinkiButton.isSelected()){
            intent.putExtra("kinki", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("kinki", false);
        }

        if(_tyugokuButton.isSelected()){
            intent.putExtra("tyugoku", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("tyugoku", false);
        }

        if(_shikokuButton.isSelected()){
            intent.putExtra("shikoku", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("shikoku", false);
        }

        if(_kyushuButton.isSelected()){
            intent.putExtra("kyushu", true);
            isGameReady = true;
        }
        else{
            intent.putExtra("kyushu", false);
        }

        if(isGameReady){
            intent.putExtra("gameMode", 2);
            getApplicationContext().deleteDatabase("prefecturesmemo.db");
            startActivity(intent);
        }
        else{
            String warning = "一つ以上選択してください";

            Toast toast = Toast.makeText(MainActivity.this, warning, Toast.LENGTH_LONG);

            ToastMaster.setToast(toast);
        }
    }

    private void weakpointMode(){

        Spinner configSpinner = findViewById(R.id.spGameConfig2);

        int numOfQuestions = 0;

        switch (configSpinner.getSelectedItemPosition()){
            case 0:
                numOfQuestions = 10;
                break;

            case 1:
                numOfQuestions = 20;
                break;

            case 2:
                numOfQuestions = 30;
                break;

            case 3:
                numOfQuestions = 47;
                break;
        }

        getApplicationContext().deleteDatabase("prefecturesmemo.db");

        Intent intent = new Intent(MainActivity.this, GamePlayActivity.class);

        intent.putExtra("gameMode", 3);
        intent.putExtra("numOfQuestions", numOfQuestions);

        startActivity(intent);
    }

    public void createWrongCounterDatabase(){

        List<Map<String, Object>> pftList = new ArrayList<>();

        Map<String, Object> data = new HashMap<>();
        data.put("id", 0);
        data.put("pft", "北海道");
        data.put("pfto", "札幌市");
        data.put("photo", R.drawable.hokkaido);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 1);
        data.put("pft", "青森県");
        data.put("pfto", "青森市");
        data.put("photo", R.drawable.aomori);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 2);
        data.put("pft", "岩手県");
        data.put("pfto", "盛岡市");
        data.put("photo", R.drawable.iwate);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 3);
        data.put("pft", "秋田県");
        data.put("pfto", "秋田市");
        data.put("photo", R.drawable.akita);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 4);
        data.put("pft", "宮城県");
        data.put("pfto", "仙台市");
        data.put("photo", R.drawable.miyagi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 5);
        data.put("pft", "山形県");
        data.put("pfto", "山形市");
        data.put("photo", R.drawable.yamagata);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 6);
        data.put("pft", "福島県");
        data.put("pfto", "福島市");
        data.put("photo", R.drawable.hukushima);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 7);
        data.put("pft", "茨城県");
        data.put("pfto", "水戸市");
        data.put("photo", R.drawable.ibaraki);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 8);
        data.put("pft", "栃木県");
        data.put("pfto", "宇都宮市");
        data.put("photo", R.drawable.tochigi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 9);
        data.put("pft", "埼玉県");
        data.put("pfto", "さいたま市");
        data.put("photo", R.drawable.saitama);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 10);
        data.put("pft", "群馬県");
        data.put("pfto", "前橋市");
        data.put("photo", R.drawable.gunma);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 11);
        data.put("pft", "千葉県");
        data.put("pfto", "千葉市");
        data.put("photo", R.drawable.chiba);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 12);
        data.put("pft", "東京都");
        data.put("pfto", "東京");
        data.put("photo", R.drawable.tokyo);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 13);
        data.put("pft", "神奈川県");
        data.put("pfto", "横浜市");
        data.put("photo", R.drawable.kanagawa);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 14);
        data.put("pft", "新潟県");
        data.put("pfto", "新潟市");
        data.put("photo", R.drawable.niigata);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 15);
        data.put("pft", "富山県");
        data.put("pfto", "富山市");
        data.put("photo", R.drawable.toyama);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 16);
        data.put("pft", "石川県");
        data.put("pfto", "金沢市");
        data.put("photo", R.drawable.ishikawa);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 17);
        data.put("pft", "福井県");
        data.put("pfto", "福井市");
        data.put("photo", R.drawable.fukui);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 18);
        data.put("pft", "山梨県");
        data.put("pfto", "甲府市");
        data.put("photo", R.drawable.yamanashi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 19);
        data.put("pft", "長野県");
        data.put("pfto", "長野市");
        data.put("photo", R.drawable.nagano);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 20);
        data.put("pft", "岐阜県");
        data.put("pfto", "岐阜市");
        data.put("photo", R.drawable.gifu);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 21);
        data.put("pft", "静岡県");
        data.put("pfto", "静岡市");
        data.put("photo", R.drawable.shizuoka);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 22);
        data.put("pft", "愛知県");
        data.put("pfto", "名古屋市");
        data.put("photo", R.drawable.aichi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 23);
        data.put("pft", "三重県");
        data.put("pfto", "津市");
        data.put("photo", R.drawable.mie);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 24);
        data.put("pft", "滋賀県");
        data.put("pfto", "大津市");
        data.put("photo", R.drawable.shiga);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 25);
        data.put("pft", "京都府");
        data.put("pfto", "京都市");
        data.put("photo", R.drawable.kyoto);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 26);
        data.put("pft", "大阪府");
        data.put("pfto", "大阪市");
        data.put("photo", R.drawable.osaka);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 27);
        data.put("pft", "兵庫県");
        data.put("pfto", "神戸市");
        data.put("photo", R.drawable.hyougo);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 28);
        data.put("pft", "奈良県");
        data.put("pfto", "奈良市");
        data.put("photo", R.drawable.nara);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 29);
        data.put("pft", "和歌山県");
        data.put("pfto", "和歌山市");
        data.put("photo", R.drawable.wakayama);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 30);
        data.put("pft", "鳥取県");
        data.put("pfto", "鳥取市");
        data.put("photo", R.drawable.totori);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 31);
        data.put("pft", "島根県");
        data.put("pfto", "松江市");
        data.put("photo", R.drawable.shimane);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 32);
        data.put("pft", "岡山県");
        data.put("pfto", "岡山市");
        data.put("photo", R.drawable.okayama);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 33);
        data.put("pft", "広島県");
        data.put("pfto", "広島市");
        data.put("photo", R.drawable.hiroshima);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 34);
        data.put("pft", "山口県");
        data.put("pfto", "山口市");
        data.put("photo", R.drawable.yamaguchi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 35);
        data.put("pft", "徳島県");
        data.put("pfto", "徳島市");
        data.put("photo", R.drawable.tokushima);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 36);
        data.put("pft", "香川県");
        data.put("pfto", "高松市");
        data.put("photo", R.drawable.kagawa);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 37);
        data.put("pft", "愛媛県");
        data.put("pfto", "松山市");
        data.put("photo", R.drawable.ehime);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 38);
        data.put("pft", "高知県");
        data.put("pfto", "高知市");
        data.put("photo", R.drawable.kouchi);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 39);
        data.put("pft", "福岡県");
        data.put("pfto", "福岡市");
        data.put("photo", R.drawable.fukuoka);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 40);
        data.put("pft", "佐賀県");
        data.put("pfto", "佐賀市");
        data.put("photo", R.drawable.saga);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 41);
        data.put("pft", "長崎県");
        data.put("pfto", "長崎市");
        data.put("photo", R.drawable.nagasaki);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 42);
        data.put("pft", "熊本県");
        data.put("pfto", "熊本市");
        data.put("photo", R.drawable.kumamoto);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 43);
        data.put("pft", "大分県");
        data.put("pfto", "大分市");
        data.put("photo", R.drawable.ooita);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 44);
        data.put("pft", "宮崎県");
        data.put("pfto", "宮崎市");
        data.put("photo", R.drawable.miyazaki);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 45);
        data.put("pft", "鹿児島県");
        data.put("pfto", "鹿児島市");
        data.put("photo", R.drawable.kagoshima);
        pftList.add(data);

        data = new HashMap<>();
        data.put("id", 46);
        data.put("pft", "沖縄県");
        data.put("pfto", "那覇市");
        data.put("photo", R.drawable.okinawa);
        pftList.add(data);

        WrongCountDatabaseHelper helper = new WrongCountDatabaseHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        SQLiteStatement stmt;

        int i;

        try{
            for(i=0;i<pftList.size();i++) {

                data = pftList.get(i);
                String sqlInsert = "INSERT INTO wrongcounter (_id, pft, pfto, photo, wrongCounter) VALUES (?, ?, ?, ?, ?)";
                stmt = db.compileStatement(sqlInsert);
                stmt.bindLong(1, (int) data.get("id"));
                stmt.bindString(2, (String) data.get("pft"));
                stmt.bindString(3, (String) data.get("pfto"));
                stmt.bindLong(4, (int) data.get("photo"));
                stmt.bindLong(5,0);

                stmt.executeInsert();
            }
        }
        finally {
            db.close();
        }


    }

}
