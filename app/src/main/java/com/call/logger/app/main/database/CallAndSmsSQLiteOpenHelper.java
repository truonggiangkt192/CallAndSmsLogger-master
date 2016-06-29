package com.call.logger.app.main.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.call.logger.app.main.model.Call;
import com.call.logger.app.main.model.Sms;

import java.util.ArrayList;
import java.util.List;

public class CallAndSmsSQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {
    private SQLiteDatabase db;
    private ContentValues values;
    private Cursor cursor;

    private static final String DB_NAME = "calls_and_messages.db";
    private static final int VERSION = 1;

    private static final String TABLE_CALL = "calls";
    private static final String TABLE_SMS = "messages";
    private static final String CALL_ID = "call_id";
    private static final String MESSAGE_ID = "message_id";
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String DURATION = "duration";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String MESSAGE = "message";

    public CallAndSmsSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CALL + " (" + CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, " + NUMBER + " TEXT NOT NULL, " + DURATION + " TEXT NOT NULL, " + DATE
                + " INTEGER NOT NULL, " + TYPE + " TEXT NOT NULL);");
        db.execSQL("CREATE TABLE " + TABLE_SMS + " (" + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, " + NUMBER + " TEXT NOT NULL, " + MESSAGE + " TEXT NOT NULL, " + DATE
                + " INTEGER NOT NULL, " + TYPE + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS);
        onCreate(db);
    }

    public void addCall(Call call) {
        db = getWritableDatabase();
        values = new ContentValues();
        values.put("name", call.getName());
        values.put("number", call.getNumber());
        values.put("duration", call.getDuration());
        values.put("date", call.getDate());
        values.put("type", call.getType());
        db.insert(TABLE_CALL, null, values);
        db.close();
    }

    public void addSms(Sms sms) {
        db = getWritableDatabase();
        values = new ContentValues();
        values.put("name", sms.getName());
        values.put("number", sms.getNumber());
        values.put("message", sms.getMessage());
        values.put("date", sms.getDate());
        values.put("type", sms.getType());
        db.insert(TABLE_SMS, null, values);
        db.close();
    }

    public void deleteCall(int id) {
        db = getWritableDatabase();
        db.delete(TABLE_CALL, CALL_ID + "=" + id, null);
    }

    public void deleteSms(int id) {
        db = getWritableDatabase();
        db.delete(TABLE_SMS, MESSAGE_ID + "=" + id, null);
    }

    public int getCount(String tableName) {
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        return cursor.getCount();
    }

    public List<Call> getAllCalls() {
        List<Call> calls = new ArrayList<Call>();
        Call call;
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_CALL, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                call = new Call();
                call.setId(cursor.getInt(0));
                call.setName(cursor.getString(1));
                call.setNumber(cursor.getString(2));
                call.setDuration(cursor.getString(3));
                call.setDate(cursor.getLong(4));
                call.setType(cursor.getString(5));
                calls.add(call);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return calls;
    }

    public List<Sms> getAllSms() {
        List<Sms> smsList = new ArrayList<Sms>();
        Sms sms;
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + TABLE_SMS, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                sms = new Sms();
                sms.setId(cursor.getInt(0));
                sms.setName(cursor.getString(1));
                sms.setNumber(cursor.getString(2));
                sms.setMessage(cursor.getString(3));
                sms.setDate(cursor.getLong(4));
                sms.setType(cursor.getString(5));
                smsList.add(sms);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return smsList;
    }
}
