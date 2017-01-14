package doortodoor.easyshot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by noble on 2017-01-14.
 */

public class ImageDatabaseManager {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TableEntry.TABLE_NAME + " (" +
                    TableEntry._ID + " INTEGER PRIMARY KEY," +
                    TableEntry.COLUMN_IMAGE_LOCATION + TEXT_TYPE + COMMA_SEP +
                    TableEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    TableEntry.COLUMN_PRICE + TEXT_TYPE + COMMA_SEP +
                    TableEntry.COLUMN_TAG + TEXT_TYPE + COMMA_SEP +
                    TableEntry.COLUMN_URL + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TableEntry.TABLE_NAME;
    public static Context mContext;
    private final SQLiteDatabase mDatabase;
    private final ImageDatabaseHelper mHelper;

    public ImageDatabaseManager(Context context) {
        mContext = context;
        mHelper = new ImageDatabaseHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    /*
    * 데이터베이스에 주어진 값으로 이루어진 Row를 삽입한다.
    *
    * Input
    *   img_loc : 이미지의 경로
    *   name    : 상품의 이름
    *   price   : 상품의 가격
    *   tag     : 상품의 태그 (비어있을 수 있다.)
    *   url     : 상품의 URL (null일 수 있다.)
    *
    * Output
    *   boolean type : 성공하면 true, 실패하면
    *   */
    public boolean insert(String name, int price, String img_loc, ArrayList<String> tag, String url) {
        ContentValues values = new ContentValues();
        values.put(TableEntry.COLUMN_NAME, name);
        values.put(TableEntry.COLUMN_PRICE, price);
        values.put(TableEntry.COLUMN_IMAGE_LOCATION, img_loc);
        values.put(TableEntry.COLUMN_TAG, tag.toString());
        values.put(TableEntry.COLUMN_URL, url);

        //데이터베이스에 삽입
        long newRowId = mDatabase.insert(TableEntry.TABLE_NAME, null, values);

        //insert가 실패하면
        return newRowId != -1;
    }

    public ArrayList<DataItem> search(String entry, ArrayList<String> value) {
        String[] projection = {
                TableEntry.COLUMN_IMAGE_LOCATION,
                TableEntry.COLUMN_NAME,
                TableEntry.COLUMN_PRICE,
                TableEntry.COLUMN_TAG,
                TableEntry.COLUMN_URL
        };
        String selection = entry + "= ?";
        String[] selectionArgs = (String[]) value.toArray();

        ArrayList<DataItem> list = new ArrayList<DataItem>();


        Cursor c = mDatabase.query(TableEntry.TABLE_NAME, projection, selection, selectionArgs
                , null, null, null);

        c.moveToFirst();
        while (c.isAfterLast()) {
            //ArrayList of tag 얻기
            ArrayList<String> tags = new ArrayList<String>();
            String tagString = c.getString(c.getColumnIndexOrThrow(TableEntry.COLUMN_TAG));
            tags = parseTagString(tagString);

            DataItem DataItem = new DataItem(c.getInt(c.getColumnIndexOrThrow(TableEntry._ID)),
                    c.getString(c.getColumnIndexOrThrow(TableEntry.COLUMN_IMAGE_LOCATION)),
                    c.getString(c.getColumnIndexOrThrow(TableEntry.COLUMN_NAME)),
                    Integer.parseInt(c.getString(c.getColumnIndexOrThrow(TableEntry.COLUMN_PRICE))),
                    tags,
                    c.getString(c.getColumnIndexOrThrow(TableEntry.COLUMN_URL)));

            list.add(DataItem);
        }

        return list;
    }

    /*
    * 데이터베이스에서 entry가 value와 일치하는 row를 찾아서 ArrayList<DataItem>으로 반환한다.
    *
    * Input
    *   entry   : 찾을 DataItem의 타입
    *   value   : 값의 ArrayList
    *
    * Output
    *   ArrayList<DataItem> : 찾은 DataItem들의 List
    *   */

    public ArrayList<String> parseTagString(String tagString) {
        //[,] 제거
        tagString = tagString.substring(1, tagString.length() - 1);
        return new ArrayList<String>(Arrays.asList(tagString.split(",")));
    }

    //데이터베이스를 만들기 위한 기본적인 Columns
    public static class TableEntry implements BaseColumns {
        public static final String TABLE_NAME = "image_db";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IMAGE_LOCATION = "image_location";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_URL = "url";
    }

    //SQL 작업을 위한 SQLiteOpenHelper
    public class ImageDatabaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "ImageDatabase.db";

        public ImageDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
