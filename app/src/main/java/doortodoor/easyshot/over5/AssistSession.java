package doortodoor.easyshot.over5;

import android.annotation.TargetApi;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import doortodoor.easyshot.R;

/**
 * Created by noble on 2017-01-05.
 */


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AssistSession extends VoiceInteractionSession {

    private Bitmap mScreenshot;
    private Context mContext;

    public AssistSession(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate() {

    }


    @Override
    public View onCreateContentView() {
        View mContentView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.activity_on_captured, null);
        return mContentView;
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);
        Toast.makeText(mContext, "I'm in", Toast.LENGTH_LONG).show();
        //View view = structure.getWindowNodeAt(0);

    }

    @Override
    public void onHandleScreenshot(Bitmap screenshot) {
        if (screenshot != null) {
            mScreenshot = screenshot;
            resolveScreenshot(screenshot);
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
                Log.i(mContext.getClass().getSimpleName(), "Creating folder success");
            } else {
                Log.i(mContext.getClass().getSimpleName(), "Creating folder failure");
            }
        }

        //폴더에 파일을 만들어 준다.
        File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            captured.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(mContext.getClass().getSimpleName(), "Saving screeshot success");
            Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
