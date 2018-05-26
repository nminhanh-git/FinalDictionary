package com.app.mightylion.finaldictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by nminh on 12/3/2017.
 */

public class SearchedWordDatabaseHandler extends SQLiteOpenHelper {
    //    all STATIC variables

    //    Database version
    private static final int DATABASE_VERSION = 1;

    //    Database name
    private static final String DATABASE_NAME = "searchedWord";

    //    Searched word table name
    private static final String TABLE_NAME = "word";

    //     Searched word table column name
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";

    private ArrayList<String> wordList = new ArrayList<>();

    public SearchedWordDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME + " ("
                        + KEY_ID + " INTEGER PRIMARY KEY, "
                        + KEY_WORD + " TEXT" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    long addWord(String word) {
        if (wordList.contains(word)) {
            return 10000;
        }
        SQLiteDatabase db = null;
        long id = 0;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            id = db.insert(TABLE_NAME, "", values);
        } catch (Exception ex) {
            Log.i("error", "addWord: " + ex.getMessage());
        }
        return id;
    }

    public ArrayList<String> getAllSearchedWord() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                wordList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reversedArrayList(wordList);
    }

    public ArrayList<String> reversedArrayList(ArrayList<String> original) {
        int count = 0;
        ArrayList<String> reversed = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            reversed.add(original.get(i));
            count++;
            if (count == 30) break;
        }
        return reversed;
    }

    public void clearSearchHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

}
