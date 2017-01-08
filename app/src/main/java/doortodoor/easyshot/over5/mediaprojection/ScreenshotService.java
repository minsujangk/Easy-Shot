package doortodoor.easyshot.over5.mediaprojection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import doortodoor.easyshot.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenshotService extends Service {
    private static final String TAG = ScreenshotService.class.getSimpleName();
    private static final int REQUEST_CODE = 111;
    private static final String SCREENSHOT_NAME = ScreenshotService.class.getName();
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static String INTENT_SCREENSHOT;
    private static Intent PERMISSION;
    private static ScreenshotServiceReceiver mReceiver;
    private MediaProjectionManager mProjectionManager;
    private Handler mHandler;
    private int mWidth;
    private int mHeight;
    private int mDensity;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;

    public ScreenshotService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        INTENT_SCREENSHOT = getResources().getString(R.string.INTENT_SCREENSHOT);

        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_SCREENSHOT);
        mReceiver = new ScreenshotServiceReceiver();
        registerReceiver(mReceiver, filter);


        //displaymetrics로 mWidth, mHeight, mDensity 저장
        DisplayMetrics dmetrics = getResources().getDisplayMetrics();
        mWidth = dmetrics.widthPixels;
        mHeight = dmetrics.heightPixels;
        mDensity = dmetrics.densityDpi;


        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PERMISSION = (Intent) intent.getExtras().get("PERMISSION");
        if (PERMISSION == null) {
            Log.e(TAG, "Permission not exists");
        }
        return flags;
    }

    private void createVirtualDisplay(MediaProjection mediaProjection) {
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mediaProjection.createVirtualDisplay(SCREENSHOT_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    public void captureScreenshot() {
        mMediaProjection = mProjectionManager
                .getMediaProjection(Activity.RESULT_OK, (Intent) PERMISSION.clone());
        createVirtualDisplay(mMediaProjection);
        mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
    }

    private void resolveScreenshot(Bitmap captured) {
        String IMAGES_PRODUCED = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String exStorage = Environment.getExternalStorageDirectory().toString();

        //폴더를 찾는다. 존재하지 않을 경우 폴더를 만들어 준다.
        File folder = new File(exStorage, "/easyshot");
        boolean isFolder = true;
        if (!(isFolder = folder.exists())) {
            isFolder = folder.mkdirs();
            if (isFolder) {
                Log.e(getClass().getSimpleName(), "Creating folder success");
            } else {
                Log.e(getClass().getSimpleName(), "Creating folder failure");
            }
        }

        //폴더에 파일을 만들어 준다.
        File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            captured.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(getClass().getSimpleName(), "Saving screeshot success");
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaProjection != null) {
                    mMediaProjection.stop();
                }
            }
        });
    }

    public class ScreenshotServiceReceiver extends BroadcastReceiver {
        public ScreenshotServiceReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            String action = intent.getAction();
            if (action.equals(INTENT_SCREENSHOT)) {
                //스크린샷 액션 발생
                captureScreenshot();
            }

        }
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            Bitmap bitmap = null;

            mImageReader.setOnImageAvailableListener(null, null);

            try {
                image = mImageReader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    resolveScreenshot(bitmap);
                    Log.e(TAG, "captured image");

//                    // write bitmap to a file
//                    fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//
//
//                    IMAGES_PRODUCED++;
//                    Log.e(TAG, "captured image: " + IMAGES_PRODUCED);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException ioe) {
//                        ioe.printStackTrace();
//                    }
//                }


                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }

                //sMediaProjection.stop();
                stopProjection();
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
//                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }
}
