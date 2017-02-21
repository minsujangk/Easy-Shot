package doortodoor.neat;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/*
* ClipboardService extends Service
* 클립보드의 변화를 감지해서 변화가 있을 시
* OnCapturedActivity를 띄운다.
* */
public class ClipboardService extends Service {
    public ClipboardService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //클립보드에 변화가 있고, 그 내용 중 url을 포함하는 부분이 있을 경우 OnCapturedActivity를 실행한다.
        cm.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.i("clipboard", "changed to:" + cm.getPrimaryClip());
                Toast.makeText(getApplicationContext(), "copied " + cm.getPrimaryClip().getItemAt(0).getText(), Toast.LENGTH_SHORT).show();

                String text = cm.getPrimaryClip().getItemAt(0).getText().toString();
                String[] texts = text.split(" ");
                for (String temp : texts) {
                    if (isValidURL(temp)) {
                        Intent intent = new Intent(ClipboardService.this, OnCapturedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.putExtra("link", temp);
                        startActivity(intent);
                    }
                }

            }
        });
        return flags;
    }

    //Valid한 url인지 체크한다.
    public boolean isValidURL(String urlStr) {
        final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(urlStr);//replace with string to compare
        return m.find();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
