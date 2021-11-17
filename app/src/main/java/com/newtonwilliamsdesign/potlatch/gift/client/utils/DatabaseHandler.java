package com.newtonwilliamsdesign.potlatch.gift.client.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

/***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************
 G I F T
 A Multi-user Web Application and Android Client Application
 for sharing of image gifts.

 Copyright (C) 2014 Newton Williams Design.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************/

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "giftTouchCountStore";
    private static final String TABLE_COUNTS = "touchcounts";
    private static final String KEY_ID = "id";
    private static final String KEY_COUNT = "count";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TOUCHCOUNT_TABLE = "CREATE TABLE " + TABLE_COUNTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_COUNT + " INTEGER" + ")";
        db.execSQL(CREATE_TOUCHCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTS);
        onCreate(db);
    }

    public void addCount(int id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_COUNT, count);

        db.insert(TABLE_COUNTS, null, values);
        db.close();
    }

    public int updateCount(int id, int count) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_COUNT, count);

        int result = db.update(TABLE_COUNTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public int getCount(long id) {
        int intid = (int) id;
        SQLiteDatabase db = this.getReadableDatabase();
        int count;

        Cursor cursor = db.query(TABLE_COUNTS, new String[] {
                        KEY_ID,
                        KEY_COUNT },
                        KEY_ID + "=?",
                        new String[] { String.valueOf(intid) }, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            count = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_COUNT)));
        } else count = -1;
        cursor.close();
        db.close();
        return count;
    }

    public Bundle getAllCounts() {
        Bundle countBundle = new Bundle();

        String selectQuery = "SELECT  * FROM " + TABLE_COUNTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(KEY_ID));
                int count = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_COUNT)));
                countBundle.putInt(id, count);
            } while (cursor.moveToNext());
        }

        db.close();
        return countBundle;
    }

    public void deleteCount(long id) {
        int intid = (int) id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COUNTS, KEY_ID + " = ?",
                new String[] { String.valueOf(intid) });
        db.close();
    }

    public void saveCount(long id, long count) {
        int intid = (int) id;
        int intcount = (int) count;
        if (updateCount(intid, intcount) == 0) {
            addCount(intid, intcount);
        }
    }
}
