package doortodoor.easyshot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by noble on 2017-01-14.
 */

public class ImageDatabaseManager {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_IMAGE_CREATE_ENTRIES =
            "CREATE TABLE " + ImageTableEntry.TABLE_NAME + " (" +
                    ImageTableEntry._ID + " INTEGER PRIMARY KEY," +
                    ImageTableEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ImageTableEntry.COLUMN_FOLDER_ID + TEXT_TYPE + COMMA_SEP +
                    ImageTableEntry.COLUMN_IMAGE_LOCATION + TEXT_TYPE + COMMA_SEP +
                    ImageTableEntry.COLUMN_TAG + TEXT_TYPE + COMMA_SEP +
                    ImageTableEntry.COLUMN_URL + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ImageTableEntry.TABLE_NAME;
    public static Context mContext;
    private SQLiteDatabase mDatabase;
    private ImageDatabaseHelper mHelper;
    private final FolderDatabaseManager mFolder;
    private ArrayList<Folder> mFolder_list;

    public ImageDatabaseManager(Context context) {
        mContext = context;
        mHelper = new ImageDatabaseHelper(context);
        mFolder = new FolderDatabaseManager(context);
        mFolder_list = mFolder.getAll();
        mDatabase = mHelper.getWritableDatabase();
    }

    /*
    * 데이터베이스에 주어진 값으로 이루어진 Row를 삽입한다.
    *
    * Input
    *   name    : 상품의 이름
    *   folder_name : 상품이 저장될 폴더의 이름
    *   price   : 상품의 가격
    *   img_loc : 이미지의 경로
    *   tag     : 상품의 태그 (비어있을 수 있다.)
    *   url     : 상품의 URL (null일 수 있다.)
    *
    * Output
    *   boolean type : 성공하면 true, 실패하면
    *   */
    public int insert(String name, String folder_name, String img_loc, ArrayList<String> tag, String url) {
        long folder_id = -1;
        for (Folder folder : mFolder_list) {
            if (folder.getFolderName().equals(folder_name)) {
                folder_id = folder.getId();
                break;
            }
        }
        if (folder_id == -1) { // 폴더가 발견되지 않음. 새로 만듦.
            folder_id = mFolder.insert(folder_name);
        }

        ContentValues values = new ContentValues();
        values.put(ImageTableEntry.COLUMN_NAME, name);
        values.put(ImageTableEntry.COLUMN_FOLDER_ID, folder_id);
        values.put(ImageTableEntry.COLUMN_IMAGE_LOCATION, img_loc);
        values.put(ImageTableEntry.COLUMN_TAG, tag.toString());
        values.put(ImageTableEntry.COLUMN_URL, url);

        //데이터베이스에 삽입
        long newRowId = mDatabase.insert(ImageTableEntry.TABLE_NAME, null, values);

        //insert가 실패하면
        return (int) newRowId;
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

    public ArrayList<DataItem> search(String entry, ArrayList<String> value) {
        String[] projection = {
                ImageTableEntry.COLUMN_NAME,
                ImageTableEntry.COLUMN_FOLDER_ID,
                ImageTableEntry.COLUMN_IMAGE_LOCATION,
                ImageTableEntry.COLUMN_TAG,
                ImageTableEntry.COLUMN_URL
        };
        String selection = entry + "= ?";
        String[] selectionArgs = (String[]) value.toArray();

        ArrayList<DataItem> list = new ArrayList<DataItem>();


        Cursor c = mDatabase.query(ImageTableEntry.TABLE_NAME, projection, selection, selectionArgs
                , null, null, null);

        c.moveToFirst();
        while (c.isAfterLast() == false) {
            //ArrayList of tag 얻기
            ArrayList<String> tags = new ArrayList<String>();
            String tagString = c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_TAG));
            tags = parseTagString(tagString);

            DataItem dataItem = new DataItem(c.getInt(c.getColumnIndexOrThrow(ImageTableEntry._ID)),
                    c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_NAME)),
                    Integer.parseInt(c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_FOLDER_ID))),
                    c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_IMAGE_LOCATION)),
                    tags,
                    c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_URL)));

            list.add(dataItem);
            c.moveToNext();
        }

        return list;
    }

    public ArrayList<DataItem> getAll() {
        ArrayList<DataItem> list = new ArrayList<DataItem>();
        Cursor c = mDatabase.rawQuery("select * from " + ImageTableEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (c.isAfterLast() == false) {
                ArrayList<String> tags = new ArrayList<String>();
                String tagString = c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_TAG));
                tags = parseTagString(tagString);
                DataItem dataItem = new DataItem(c.getInt(c.getColumnIndexOrThrow(ImageTableEntry._ID)),
                        c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_NAME)),
                        Integer.parseInt(c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_FOLDER_ID))),
                        c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_IMAGE_LOCATION)),
                        tags,
                        c.getString(c.getColumnIndexOrThrow(ImageTableEntry.COLUMN_URL)));

                list.add(dataItem);
                c.moveToNext();
            }
        }

        return list;
    }

    public void updateURLColumn(String rowId, String newValue) {
        String sql = "UPDATE "+ImageTableEntry.TABLE_NAME +" SET " + ImageTableEntry.COLUMN_URL+ " = '"+newValue+"' WHERE "+ImageTableEntry._ID+ " = "+rowId;
        mDatabase.beginTransaction();
        SQLiteStatement stmt = mDatabase.compileStatement(sql);
        try{
            stmt.execute();
            mDatabase.setTransactionSuccessful();
        }finally{
            mDatabase.endTransaction();
        }
//        ContentValues cv = new ContentValues();
//        cv.put(ImageTableEntry.COLUMN_URL, newValue);
//        mDatabase.update(ImageTableEntry.TABLE_NAME, cv, ImageTableEntry._ID + "= ?", new String[]{rowId});

    }

    public ArrayList<String> parseTagString(String tagString) {
        //[,] 제거
        tagString = tagString.substring(1, tagString.length() - 1);
        return new ArrayList<String>(Arrays.asList(tagString.split(",")));
    }

    //데이터베이스를 만들기 위한 기본적인 Columns
    public static class ImageTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "image__db";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FOLDER_ID = "folder_id";
        public static final String COLUMN_IMAGE_LOCATION = "image_location";
        public static final String COLUMN_TAG = "tag";
        public static final String COLUMN_URL = "url";
    }

    //SQL 작업을 위한 SQLiteOpenHelper
    public class ImageDatabaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 6;
        public static final String DATABASE_NAME = "Database.db";

        public ImageDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.e("this", "database image addeddd");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("this", "database image added");
            db.execSQL(SQL_IMAGE_CREATE_ENTRIES);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
