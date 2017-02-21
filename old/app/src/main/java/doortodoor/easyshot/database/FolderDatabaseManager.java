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

public class FolderDatabaseManager {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FolderTableEntry.TABLE_NAME + " (" +
                    FolderTableEntry._ID + " INTEGER PRIMARY KEY," +
                    FolderTableEntry.COLUMN_FOLDER_NAME + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FolderTableEntry.TABLE_NAME;
    public static Context mContext;
    private final SQLiteDatabase mDatabase;
    private final FolderDatabaseHelper mHelper;

    public FolderDatabaseManager(Context context) {
        mContext = context;
        mHelper = new FolderDatabaseHelper(context);
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
    public long insert(String folder) {
        ContentValues values = new ContentValues();
        values.put(FolderTableEntry.COLUMN_FOLDER_NAME, folder);

        //데이터베이스에 삽입
        long newRowId = mDatabase.insert(FolderTableEntry.TABLE_NAME, null, values);

        //insert가 실패하면
        return newRowId;
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

    public ArrayList<Folder> search(String entry, ArrayList<String> value) {
        String[] projection = {
                FolderTableEntry.COLUMN_FOLDER_NAME
        };
        String selection = entry + "= ?";
        String[] selectionArgs = (String[]) value.toArray();

        ArrayList<Folder> folder_list = new ArrayList<Folder>();


        Cursor c = mDatabase.query(FolderTableEntry.TABLE_NAME, projection, selection, selectionArgs
                , null, null, null);

        c.moveToFirst();
        while (c.isAfterLast() == false) {
            //ArrayList of tag 얻기
            Folder folder = new Folder(c.getInt(c.getColumnIndexOrThrow(FolderTableEntry._ID)),
                    c.getString(c.getColumnIndexOrThrow(FolderTableEntry.COLUMN_FOLDER_NAME)));

            folder_list.add(folder);
            c.moveToNext();
        }

        return folder_list;
    }

    public ArrayList<Folder> getAll() {
        ArrayList<Folder> folder_list = new ArrayList<Folder>();
        Cursor c = mDatabase.rawQuery("select * from " + FolderTableEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            while (c.isAfterLast() == false) {
                Folder folder = new Folder(c.getInt(c.getColumnIndexOrThrow(FolderTableEntry._ID)),
                        c.getString(c.getColumnIndexOrThrow(FolderTableEntry.COLUMN_FOLDER_NAME)));

                folder_list.add(folder);
                c.moveToNext();
            }
        }

        return folder_list;
    }


    public ArrayList<String> parseTagString(String tagString) {
        //[,] 제거
        tagString = tagString.substring(1, tagString.length() - 1);
        return new ArrayList<String>(Arrays.asList(tagString.split(",")));
    }

    //데이터베이스를 만들기 위한 기본적인 Columns
    public static class FolderTableEntry implements BaseColumns {
        public static final String TABLE_NAME = "folder_db";
        public static final String COLUMN_FOLDER_NAME = "folder_name";
    }

    //SQL 작업을 위한 SQLiteOpenHelper
    public class FolderDatabaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME = "ImageDatabase.db";

        public FolderDatabaseHelper(Context context) {
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
