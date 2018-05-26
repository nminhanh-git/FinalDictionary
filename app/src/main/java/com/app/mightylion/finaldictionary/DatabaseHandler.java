package com.app.mightylion.finaldictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by nminh on 11/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    //Database path
    private static String DATABASE_PATH;
    //Database name
    private static final String DATABASE_NAME = "anh_viet.db";

    private final Context myContext;
    private Cursor cursor;


    protected DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
        myContext = context;
        DATABASE_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean isDatabaseExist() {
        return myContext.getDatabasePath(DATABASE_NAME).exists();
    }


    public void createDatabase() {
        if (isDatabaseExist()) {
            Log.i("Create Database", "Database available");
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            try {
                copyFromDatabase();
            } catch (IOException e) {
                Log.i("Create Database", "File Path error! ");
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }

    public void copyFromDatabase() throws IOException {
        InputStream inputStream = myContext.getAssets().open(DATABASE_NAME);

        File outputFilePath = new File(DATABASE_PATH);
        if (!outputFilePath.exists()) {
            outputFilePath.mkdir();
        }

        OutputStream outputStream = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public ArrayList<String> searchWord(String word) {
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT WORD FROM anh_viet WHERE WORD LIKE '" + word + "%'";
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            arrayList = new ArrayList<>();
            do {
                arrayList.add(cursor.getString(0));
                if (arrayList.size() == 50) {
                    break;
                }
            } while (cursor.moveToNext());
        }
        db.close();
        return arrayList;
    }

    public String getWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT  CONTENT FROM anh_viet Where WORD = '" + word + "'";
        cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        db.close();
        return null;
    }
}
