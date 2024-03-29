package doortodoor.easyshot;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import doortodoor.easyshot.database.ImageDatabaseManager;

import static android.content.ContentValues.TAG;

/*
* MyAccessibilityService extends AccessibilityService
* 화면에서 URL을 긁어올 때 이용되는 AccessibilityService.
* */
public class MyAccessibilityService extends AccessibilityService {

    private static String INTENT_CAPTURE_URL;
    private String mURL;
    private AccessibilityNodeInfo source;
    private URLServiceReceiver mReceiver;
    private ImageDatabaseManager imageDB;
    private static String INTENT_SAVE_URL;

    public MyAccessibilityService() {
    }

    @Override
    public void onServiceConnected() {
        INTENT_CAPTURE_URL = getResources().getString(R.string.INTENT_CAPTURE_URL);
        INTENT_SAVE_URL = getResources().getString(R.string.INTENT_SAVE_URL);
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_CAPTURE_URL);
        filter.addAction(INTENT_SAVE_URL);
        mReceiver = new URLServiceReceiver();
        registerReceiver(mReceiver, filter);
        imageDB = new ImageDatabaseManager(this);
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.e(TAG, "Catch Event Package Name : " + event.getPackageName());
//        Log.e(TAG, "Catch Event TEXT : " + event.getText());
//        Log.e(TAG, "Catch Event ContentDescription  : " + event.getContentDescription());
//        Log.e(TAG, "Catch Event getSource : " + event.getSource());
//        Log.e(TAG, "Catch Event getChildCount : " + event.getSource().getWindowId());
//        Queue<AccessibilityNodeInfo> queue = (Queue) new LinkedList();
//        for (int i = 0; i < event.getSource().getChildCount(); i++) {
//            queue.add(event.getSource().getChild(i));
//        }
//        while (!queue.isEmpty()) {
//            AccessibilityNodeInfo an = queue.poll();
//            if (an != null) {
//                //if (an.getContentDescription() != null)
//                Log.e(TAG, "Catch Event getChildCount : " + an.getClassName());
//                for (int i = 0; i < an.getChildCount(); i++) {
//                    queue.add(an.getChild(i));
//                }
//            }
//        }
        Log.e(TAG, "=========================================================================");

        final int eventType = event.getEventType();
        String eventText = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT).show();


                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//                Toast.makeText(getApplicationContext(), "ww", Toast.LENGTH_SHORT).show();
                source = event.getSource();
                captureURL();
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Toast.makeText(getApplicationContext(), "wws", Toast.LENGTH_SHORT).show();
                eventText = "Focused: ";
                source = event.getSource();
                captureURL();
                break;
        }

    }

    public boolean isValidURI(String uriStr) {
        try {
            URI uri = new URI(uriStr);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean isValidURL(String urlStr) {
        final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(urlStr);//replace with string to compare
        return m.find();
    }

    public void captureURL() {
        Queue<AccessibilityNodeInfo> queue = (Queue) new LinkedList();
        for (int i = 0; i < source.getChildCount(); i++) {
            queue.add(source.getChild(i));
        }
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo an = queue.poll();
            if (an != null) {
                //if (an.getContentDescription() != null)
                if (an.getText() != null)
                    if (isValidURL(an.getText().toString())) {
                        mURL = an.getText().toString();
                        Toast.makeText(getApplicationContext(), "url = " + mURL, Toast.LENGTH_SHORT).show();
                        break;
                    }
                for (int i = 0; i < an.getChildCount(); i++) {
                    queue.add(an.getChild(i));
                }
            }
        }
    }

    public void saveURL(int id){
        imageDB.updateURLColumn(Integer.toString(id), mURL);
        Toast.makeText(getApplicationContext(), "url = " + mURL, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInterrupt() {

    }

    public class URLServiceReceiver extends BroadcastReceiver {
        public URLServiceReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // an Intent broadcast.
            String action = intent.getAction();
            if (action.equals(INTENT_CAPTURE_URL)) {
                //스크린샷 액션 발생
                Log.e("wow", "broadcast received");
                captureURL();
            } else if (action.equals(INTENT_SAVE_URL)) {
                //스크린샷 액션 발생
                Log.e("wow", "broadcast save received");
                int id = intent.getIntExtra("id", -1);
                saveURL(id);
            }

        }
    }

}
