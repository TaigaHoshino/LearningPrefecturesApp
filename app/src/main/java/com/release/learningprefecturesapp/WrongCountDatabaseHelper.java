package com.release.learningprefecturesapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WrongCountDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "prefectureswrongcounter.db";

    private static final int DATABASE_VERSION = 1;

    public WrongCountDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE wrongcounter (");
        sb.append("_id INTEGER,");
        sb.append("pft TEXT,");
        sb.append("pfto TEXT,");
        sb.append("photo INTEGER,");
        sb.append("wrongCounter INTEGER");
        sb.append(");");
        String sql = sb.toString();

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
