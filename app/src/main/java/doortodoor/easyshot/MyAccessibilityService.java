package doortodoor.easyshot;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

import static android.content.ContentValues.TAG;

public class MyAccessibilityService extends AccessibilityService {

    public MyAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "Catch Event Package Name : " + event.getPackageName());
        Log.e(TAG, "Catch Event TEXT : " + event.getText());
        Log.e(TAG, "Catch Event ContentDescription  : " + event.getContentDescription());
        Log.e(TAG, "Catch Event getSource : " + event.getSource());
        Log.e(TAG, "Catch Event getChildCount : " + event.getSource().getWindowId());
        Queue<AccessibilityNodeInfo> queue = (Queue) new LinkedList();
        for (int i = 0; i < event.getSource().getChildCount(); i++) {
            queue.add(event.getSource().getChild(i));
        }
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo an = queue.poll();
            if (an != null) {
                //if (an.getContentDescription() != null)
                Log.e(TAG, "Catch Event getChildCount : " + an.getClassName());
                for (int i = 0; i < an.getChildCount(); i++) {
                    queue.add(an.getChild(i));
                }
            }
        }
        Log.e(TAG, "=========================================================================");
        final int eventType = event.getEventType();
        String eventText = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                //Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_SHORT).show();
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Toast.makeText(getApplicationContext(), "ww", Toast.LENGTH_SHORT).show();
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Toast.makeText(getApplicationContext(), "wws", Toast.LENGTH_SHORT).show();
                eventText = "Focused: ";
                break;
        }

    }

    @Override
    public void onInterrupt() {

    }

}
