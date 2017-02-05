package doortodoor.easyshot.under_lollipop;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import doortodoor.easyshot.R;
import doortodoor.easyshot.database.ImageDatabaseManager;

public class OnCapturedActivity extends AppCompatActivity implements View.OnTouchListener {

    private static String INTENT_CAPTURE_URL;
    private int _xDelta;
    private int _yDelta;
    private RelativeLayout layout;
    private Handler handler;
    private Runnable runnable_FinishActivity;
    private int layout_height;
    private EditText editText;
    private int initialMargin;
    private ImageDatabaseManager mImageDatabaseManager;
    private String mItemName;
    private String mItemLocation;
    private String mItemURL;

    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_success_notification);
        setWindowParams();

        Intent intent = getIntent();
        mItemName = intent.getStringExtra("name");
        mItemLocation = intent.getStringExtra("img_loc");
        mItemURL = intent.getStringExtra("url");


        mItemURL = getSharedPreferences("url", MODE_PRIVATE).getString("accessibility_url", "");
        Toast.makeText(this, mItemURL, Toast.LENGTH_SHORT).show();


        mImageDatabaseManager = new ImageDatabaseManager(this);

        layout = (RelativeLayout) findViewById(R.id.layout_notification);
        editText = (EditText) findViewById(R.id.editText);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    detachRunnable();
                } else {
                    attachRunnable(1500);
                }
            }
        });

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        layout.startAnimation(anim);

        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(300);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        getWindow().setLayout(screenWidth, WindowManager.LayoutParams.WRAP_CONTENT);

        layout_height = (int) convertDpToPixel(36f, this);

        layout.setOnTouchListener(this);

//        Button button = (Button) findViewById(R.id.button3);
//        Button button2 = (Button) findViewById(R.id.button4);
//
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View view = findViewById(R.id.activity_on_captured);
//                view.setDrawingCacheEnabled(true);
//                //  Bitmap cachedBitmap = screenShot(view);
////        Bitmap cache = view.getDrawingCache();
//                Bitmap cachedBitmap = Bitmap.createBitmap(view.getDrawingCache());
//                view.setDrawingCacheEnabled(false);
//
//
//                String IMAGES_PRODUCED = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                String exStorage = Environment.getExternalStorageDirectory().toString();
//
//                //폴더를 찾는다. 존재하지 않을 경우 폴더를 만들어 준다.
//                File folder = new File(exStorage, "/ddd2");
//                boolean isFolder = true;
//                if (!(isFolder = folder.exists())) {
//                    isFolder = folder.mkdirs();
//                    if (isFolder) {
//                        Log.i(getPackageName(), "Creating folder success");
//                    } else {
//                        Log.e(getPackageName(), "Creating folder failure");
//                    }
//                }
//
//                //폴더에 파일을 만들어 준다.
//                File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(file);
//                    cachedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    Toast.makeText(getApplicationContext(), "Success2", Toast.LENGTH_LONG).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        button2.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        handler = new Handler();
        runnable_FinishActivity = new Runnable() {
            @Override
            public void run() {
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        saveData();
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                layout.startAnimation(fadeOut);
            }
        };
        attachRunnable(3000);


    }

    private void saveData() {
        if (mImageDatabaseManager.insert(mItemName, editText.getText().toString(), mItemLocation, new ArrayList<String>(), mItemURL)) {
            Log.e("OnCapturedActivity", "Item inserted to " + mItemName + " " + editText.getText().toString() + " " + mItemLocation + " " + mItemURL);
        }
    }

    public void setWindowParams() {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
        wlp.dimAmount = 0;
        wlp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wlp.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(wlp);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.layout_notification:
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();
                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        detachRunnable();
                        initialMargin = layoutParams.bottomMargin;
                        _yDelta = Y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int margin = Math.max(0, Math.min(-Y + _yDelta + initialMargin, layout_height));
                        layoutParams.bottomMargin = margin;
                        layout.setLayoutParams(layoutParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        int target = 0;
                        if (layoutParams.bottomMargin < layout_height - layoutParams.bottomMargin) {
                            target = 0;
                        } else {
                            target = layout_height;
                        }
                        final float toYDelta = target - layoutParams.bottomMargin;
                        final float origin = layoutParams.bottomMargin;

                        final int toTarget = target;

                        final int newBottomMargin = target;
                        Animation a = new Animation() {

                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                layoutParams.bottomMargin = (int) (toYDelta * interpolatedTime + origin);
                                layout.setLayoutParams(layoutParams);
                            }
                        };
                        a.setDuration(500); // in ms
                        layout.startAnimation(a);

                        attachRunnable(1500);
                        break;
                }
                layout.invalidate();
                break;

        }
        return true;
    }

    private void detachRunnable() {
        handler.removeCallbacks(runnable_FinishActivity);
    }

    private void attachRunnable(long time) {
        handler.postDelayed(runnable_FinishActivity, time);
    }
}
