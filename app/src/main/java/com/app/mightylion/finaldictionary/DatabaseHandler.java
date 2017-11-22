package com.app.mightylion.finaldictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nminh on 11/21/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    //Database path
    private static String DATABASE_PATH;
    //Database name
    private static final String DATABASE_NAME = "anh_viet";

    private final Context myContext;


    public DatabaseHandler(Context context) {
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


//    public SQLiteDatabase openDatabase() throws SQLException {
//        String myPath = DATABASE_PATH + DATABASE_NAME;
//        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//    }

    public Word getWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {
                "ID", "WORD", "CONTENT"
        };

        String selection = "WORD = ?";
        String[] selectionArgs = {word};

        String sortOrder = "WORD DESC";

        Cursor cursor = db.query(
                "anh_viet",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Word aWord = new Word(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        cursor.close();
        db.close();
        return aWord;
    }

}
