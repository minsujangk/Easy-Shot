package doortodoor.easyshot.over_lollipop.assistant;

import android.annotation.TargetApi;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.voice.VoiceInteractionSession;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import doortodoor.easyshot.R;
import doortodoor.easyshot.database.ImageDatabaseManager;
import doortodoor.easyshot.under_lollipop.OnCapturedActivity;

/**
 * Created by noble on 2017-01-05.
 */


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AssistSession extends VoiceInteractionSession {

    private final ImageDatabaseManager mManager;
    private Bitmap mScreenshot;
    private Context mContext;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private String mURL;
    private String INTENT_CAPTURE_URL;

    public AssistSession(Context context) {
        super(context);
        mContext = context;
        mManager = new ImageDatabaseManager(mContext);
    }


//    @Override
//    public View onCreateContentView() {
//        View mContentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.view_success_notification, null);
//
//
////        mContentView.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
//
//        textView = (TextView) mContentView.findViewById(R.id.textView);
//        textView2 = (TextView) mContentView.findViewById(R.id.textView2);
//        textView3 = (TextView) mContentView.findViewById(R.id.textView3);
//        textView4 = (TextView) mContentView.findViewById(R.id.textView4);
//        textView5 = (TextView) mContentView.findViewById(R.id.textView5);
//        textView6 = (TextView) mContentView.findViewById(R.id.textView6);
//        return null;
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);
        INTENT_CAPTURE_URL = getContext().getResources().getString(R.string.INTENT_CAPTURE_URL);
        Intent broadcast = new Intent(INTENT_CAPTURE_URL);
        getContext().sendBroadcast(broadcast);
        Uri uri = null;
//        if ((uri = content.getWebUri()) != null) {
//            Toast.makeText(mContext, uri.toString(), Toast.LENGTH_LONG).show();
//            textView.setText(uri.toString());
//        } else {
//            Toast.makeText(mContext, "No", Toast.LENGTH_LONG).show();
//            textView.setText("No");
//        }
//
//        mURL = "";
//
//        textView5.setText(content.getIntent().toString());
//        textView2.setText(content.getIntent().getDataString());
//        StringBuilder str = new StringBuilder();
//        Bundle bundle = content.getIntent().getExtras();
//        if (bundle != null) {
//            Set<String> keys = bundle.keySet();
//            Iterator<String> it = keys.iterator();
//            while (it.hasNext()) {
//                String key = it.next();
//                str.append(key);
//                str.append(":");
//                str.append(bundle.getString(key));
//                str.append("\n\r");
//                if (key != null && key.contains("ITEMNO"))
//                    mURL = "http://itempage3.auction.co.kr/detailview.aspx?itemNo=" + bundle.getString(key);
//                if (bundle.getString(key) != null && bundle.getString(key).contains("http"))
//                    mURL = bundle.getString(key);
//            }
//            textView6.setText(str.toString());
//        }
//        //textView6.setText(content.getIntent().getExtras().);
//        textView3.setText(structure.getActivityComponent().toString());
////        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
////        String mPackageName = mActivityManager.getRunningAppProcesses().get(0).
////        for (int i = 0; i < structure.getWindowNodeCount(); i++) {
////            if (structure.getWindowNodeAt(i).getRootViewNode().getExtras() != null) {
////                textView4.setText(structure.getWindowNodeAt(i).getRootViewNode().getExtras().toString());
////            }
////        }
//        textView4.setText(mURL);
////        textView4.setText(structure.getWindowNodeCount());
//
//        //View view = structure.getWindowNodeAt(0);

    }

    @Override
    public void onHandleScreenshot(Bitmap screenshot) {
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "";
        if (screenshot != null) {
            mScreenshot = screenshot;
            filePath = resolveScreenshot(screenshot);
        }

        Intent intent = new Intent(mContext, OnCapturedActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("img_loc", filePath);
        intent.putExtra("url", mURL);

        mContext.startActivity(intent);
    }

    private String resolveScreenshot(Bitmap captured) {
        String IMAGES_PRODUCED = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String exStorage = Environment.getExternalStorageDirectory().toString();

        //폴더를 찾는다. 존재하지 않을 경우 폴더를 만들어 준다.
        File folder = new File(exStorage, "/easyshot");
        boolean isFolder = true;
        if (!(isFolder = folder.exists())) {
            isFolder = folder.mkdirs();
            if (isFolder) {
                Log.e(mContext.getClass().getSimpleName(), "Creating folder success");
            } else {
                Log.e(mContext.getClass().getSimpleName(), "Creating folder failure");
            }
        }

        //폴더에 파일을 만들어 준다.
        File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            captured.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(mContext.getClass().getSimpleName(), "Saving screeshot success");
            fos.close();
            Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.toString();
    }
}
