package com.example.book.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.book.bean.BookBean;
import com.example.book.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;


public class SQLiteHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

    //创建数据库
    public SQLiteHelper(Context context) {
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERION);
        sqLiteDatabase = this.getWritableDatabase();
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DBUtils.DATABASE_TABLE + "("+DBUtils.BOOK_ID +" integer primary key autoincrement,"+ DBUtils.BOOK_NAME + " VARCHAR(200)," + DBUtils.BOOK_AUTHOR + " VARCHAR(20) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //添加数据
    public boolean insertData(String userContent, String userTime,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.BOOK_NAME, userContent);
        contentValues.put(DBUtils.BOOK_AUTHOR, userTime);
        return db.insert(DBUtils.DATABASE_TABLE, null, contentValues) > 0;
    }

    //删除数据
    public boolean deleteData(String id,SQLiteDatabase db) {
        String sql = DBUtils.BOOK_ID + "=?";
        String[] contentValuesArray = new String[]{String.valueOf(id)};
        return db.delete(DBUtils.DATABASE_TABLE, sql, contentValuesArray) > 0;
    }

    //修改数据
    public boolean updateData(String id, String content, String userYear,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.BOOK_NAME, content);
        contentValues.put(DBUtils.BOOK_AUTHOR, userYear);
        String sql = DBUtils.BOOK_ID + "=?";
        String[] strings = new String[]{id};
        return db.update(DBUtils.DATABASE_TABLE, contentValues, sql, strings) > 0;
    }



    //查询数据
    public List<BookBean> query(SQLiteDatabase db) {
        List<BookBean> list = new ArrayList<BookBean>();
        Cursor cursor = db.query(DBUtils.DATABASE_TABLE, null, null, null, null, null, DBUtils.BOOK_ID + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BookBean noteInfo = new BookBean();
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DBUtils.BOOK_ID)));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DBUtils.BOOK_NAME));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(DBUtils.BOOK_AUTHOR));
                noteInfo.setId(id);
                noteInfo.setName(content);
                noteInfo.setAuthor(time);
                list.add(noteInfo);
//                getHttp();
            }
            cursor.close();
        }
        return list;
    }
}