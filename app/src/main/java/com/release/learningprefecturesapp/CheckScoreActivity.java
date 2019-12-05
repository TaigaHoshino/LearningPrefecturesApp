package com.release.learningprefecturesapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CheckScoreActivity extends AppCompatActivity {

    private int _pftsresultId = -1;
    private ListView _lvScoreMenu;
    List<Map<String, Object>> _pftList;
    private static final String[] FROM = {"pft", "pfto", "isCorrect", "pftWrong", "pftoWrong"};
    private static final int[] TO = {R.id.tvScorePft, R.id.tvScorePfto, R.id.ivScoreResult, R.id.tvScorePftAns, R.id.tvScorePftoAns};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_score);

        _pftList = new ArrayList<>();
        Map<String, Object> data;
        _lvScoreMenu = findViewById(R.id.lvScoreMenu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        DatabaseHelper helper = new DatabaseHelper(CheckScoreActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        try{
            Cursor cursor = db.query("pftsresultmemo",
                    new String[] {"pft", "pfto", "photo", "isCorrect", "pftWrong", "pftoWrong"},
                    null, null, null, null, null);
            cursor.moveToFirst();

            do{
                data = new HashMap<>();
                data.put("pft", cursor.getString(0));
                data.put("pfto", cursor.getString(1));
                data.put("photo", cursor.getInt(2));
                if(cursor.getInt(3) == 1){
                    data.put("isCorrect", R.drawable.correct);
                }
                else {
                    data.put("isCorrect", R.drawable.wrong);
                }

                if(cursor.getString(4) != null) {
                    data.put("pftWrong", cursor.getString(4));
                }
                else {
                    data.put("pftWrong", "正解");
                }
                if(cursor.getString(5) != null) {
                    data.put("pftoWrong", cursor.getString(5));
                }else {
                    data.put("pftoWrong", "正解");
                }
                _pftList.add(data);
            }while(cursor.moveToNext());

            SimpleAdapter adapter = new SimpleAdapter(CheckScoreActivity.this, _pftList, R.layout.row, FROM, TO);

            _lvScoreMenu.setAdapter(adapter);

            _lvScoreMenu.setOnItemClickListener(new ListItemClickListener());

        }
        finally{
            db.close();
        }


    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            _pftsresultId = position+1;

            DatabaseHelper helper = new DatabaseHelper(CheckScoreActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();

            String sql = "SELECT*FROM pftsresultmemo WHERE _id =" + _pftsresultId;

            Cursor cursor = db.rawQuery(sql, null);

            String pft = "";
            String pfto = "";
            int photo = 0;
            String pftWrong = "";
            String pftoWrong = "";

            while(cursor.moveToNext()){
                int idxPft = cursor.getColumnIndex("pft");
                int idxPfto = cursor.getColumnIndex("pfto");
                int idxPhoto = cursor.getColumnIndex("photo");
                int idxPftWrong = cursor.getColumnIndex("pftWrong");
                int idxPftoWrong = cursor.getColumnIndex("pftoWrong");

                pft = cursor.getString(idxPft);
                pfto = cursor.getString(idxPfto);
                photo = cursor.getInt(idxPhoto);
                if(cursor.getString(idxPftWrong) != null) {
                    pftWrong = cursor.getString(idxPftWrong);
                }
                else {
                    pftWrong = "正解";
                }
                if(cursor.getString(idxPftoWrong) != null) {
                    pftoWrong = cursor.getString(idxPftoWrong);
                }
                else {
                    pftoWrong = "正解";
                }
            }

            Intent intent = new Intent(CheckScoreActivity.this, DetaildResultActivity.class);

            intent.putExtra("pft", pft);
            intent.putExtra("pfto", pfto);
            intent.putExtra("photo", photo);
            intent.putExtra("pftWrong", pftWrong);
            intent.putExtra("pftoWrong", pftoWrong);

            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

