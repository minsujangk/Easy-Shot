package doortodoor.easyshot;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import doortodoor.easyshot.over_lollipop.mediaprojection.ScreenshotService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 309;
    private static String INTENT_SCREENSHOT;
    private ScreenshotService mScreenshotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        INTENT_SCREENSHOT = getResources().getString(R.string.INTENT_SCREENSHOT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startScreenshotService();
                    //           resolveScreenshot(mMediaProjectionService.captureScreen());
                }
            }
        });


        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(INTENT_SCREENSHOT);

                sendBroadcast(intent);
            }
        });

//        try{
//
//            Cursor mCur = null;
//            try{
//                String sortOrder = Browser.BookmarkColumns.TITLE + " ASC";
//                final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
//                final String[] HISTORY_PROJECTION = new String[]{
//                        "_id", // 0
//                        "url", // 1
//                        "visits", // 2
//                        "date", // 3
//                        "bookmark", // 4
//                        "title", // 5
//                        "favicon", // 6
//                        "thumbnail", // 7
//                        "touch_icon", // 8
//                        "user_entered", // 9
//                };
//                 final int HISTORY_PROJECTION_TITLE_INDEX = 5;
//                final int HISTORY_PROJECTION_URL_INDEX = 1;
//
//                mCur = getContentResolver().query(BOOKMARKS_URI,)
//
//                mCur.moveToFirst();
//                if (mCur.moveToFirst() && mCur.getCount() > 0) {
//                    while (mCur.isAfterLast() == false) {
//                        String title = mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);
//                        String url = mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
//                        long date = mCur.getLong(Browser.HISTORY_PROJECTION_DATE_INDEX);
//                        mCur.moveToNext();
//                    }
//                }else{
//                    mCur.close();
//                }
//
//            }catch(Exception e){
//
//            }finally{
//                mCur.close();
//            }
//
//        }catch(Exception e){
//
//        }


//        String assistant =
//                Settings.Secure.getString(getContentResolver(),
//                        Settings.Secure.VOICE_INTERACTION_SERVICE);
//
//        boolean areWeGood = false;
//
//        if (assistant != null) {
//            ComponentName cn = ComponentName.unflattenFromString(assistant);
//
//            if (cn.getPackageName().equals(getPackageName())) {
//                areWeGood = true;z
//            }
//        }
//
//        if (areWeGood) {
//            Toast
//                    .makeText(this, "active", Toast.LENGTH_LONG)
//                    .show();
//        } else {
//            Toast
//                    .makeText(this, "activate", Toast.LENGTH_LONG)
//                    .show();
//            startActivity(new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS));
//        }
//
//        finish();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenshotService() {
        //MediaProjectionManager 생성
        MediaProjectionManager mMediaProjectionManager
                = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //service intent 생성
                Intent intent = new Intent(this, ScreenshotService.class);
                intent.putExtra("PERMISSION", (Intent) data.clone());
                startService(intent);
            }
        }
    }

    private void resolveScreenshot(Bitmap captured) {
        String IMAGES_PRODUCED = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String exStorage = Environment.getExternalStorageDirectory().toString();

        //폴더를 찾는다. 존재하지 않을 경우 폴더를 만들어 준다.
        File folder = new File(exStorage, "/easyshot");
        boolean isFolder = true;
        if (!(isFolder = folder.exists())) {
            isFolder = folder.mkdirs();
            if (isFolder) {
                Log.i(getClass().getSimpleName(), "Creating folder success");
            } else {
                Log.i(getClass().getSimpleName(), "Creating folder failure");
            }
        }

        //폴더에 파일을 만들어 준다.
        File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            captured.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(getClass().getSimpleName(), "Saving screeshot success");
            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
