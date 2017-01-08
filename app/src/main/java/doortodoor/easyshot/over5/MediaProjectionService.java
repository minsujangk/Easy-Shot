package doortodoor.easyshot.over5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import static android.content.ContentValues.TAG;

/**
 * Created by noble on 2017-01-07.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MediaProjectionService {
    private static MediaProjectionManager mMediaProjectionManager;
    private static Context mContext;
    private static int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static Intent screenshotPermission = null;
    private MediaProjection mMediaProjection;
    //private static ImageReader mImageReader;
    private int mHeight;
    private int mWidth;
    private int mDensity;
    private Handler mHandler;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;

    public MediaProjectionService(Context context) {
        mContext = context;
        mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
        mDensity = displayMetrics.densityDpi;

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();

    }

    protected static void setScreenshotPermission(final Intent permissionIntent) {
        screenshotPermission = permissionIntent;
    }

    private void createVirtualDisplay() {


        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("sc", mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    public void captureScreen() {

        getScreenshotPermission();
        startMediaProjection();
        //startMediaProjection();

        //final Bitmap[] bmp = new Bitmap[1];

        // start capture handling thread


//        ImageReader imageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
//        Log.e(TAG, "111");
//        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-capture", mWidth, mHeight, mDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
//                , imageReader.getSurface(), null, mHandler);
//        Log.e(TAG, "222");

//
//        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                reader.setOnImageAvailableListener(null, null);
//                Log.e(TAG, "in OnImageAvailable");
//                Bitmap bmp = null;
//                Image image = null;
//                try {
//                    image = reader.acquireLatestImage();
//                    if (image != null) {
//                        final Image.Plane[] planes = image.getPlanes();
//                        final ByteBuffer buffer = planes[0].getBuffer();
//                        int offset = 0;
//                        int pixelStride = planes[0].getPixelStride();
//                        int rowStride = planes[0].getRowStride();
//                        int rowPadding = rowStride - pixelStride * mWidth;
//
//                        if (bmp == null || bmp.getWidth() != mWidth + rowPadding / pixelStride ||
//                                bmp.getHeight() != mHeight) {
//                            if (bmp != null) {
//                                bmp.recycle();
//                            }
//
//                            bmp = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight,
//                                    Bitmap.Config.ARGB_8888);
//
//                        }
//
//// create bitmap
//                        bmp.copyPixelsFromBuffer(buffer);
//                        if (bmp == null)
//                            Log.e(TAG, "fuck");
//                        resolveScreenshot(bmp);
//
//                        image.close();
//                        stopMediaProjection();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//
//
//                    if (bmp != null) {
//                        bmp.recycle();
//                    }
//
//                    if (image != null) {
//                        image.close();
//                    }
//
//                    //sMediaProjection.stop();
//                    //stopProjection();
//                }
//            }
//        }, mHandler);
        createVirtualDisplay();
        MediaProjection.Callback cb = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                mVirtualDisplay.release();
                mVirtualDisplay = null;
            }
        };
        mMediaProjection.registerCallback(cb, mHandler);

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
            Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void startMediaProjection() {
        if (mMediaProjection == null)
            mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, screenshotPermission);
    }

    public void stopMediaProjection() {
        if (null != mMediaProjection) {
            mMediaProjection.stop();
            //mMediaProjection = null;
        }
    }


    public void getScreenshotPermission() {
        try {
            if (hasScreenshotPermission()) {
//                if (null != mMediaProjection) {
//                    mMediaProjection.stop();
//                    mMediaProjection = null;
//                }
                if (null == mMediaProjection)
                    startMediaProjection();
            } else {
                openScreenshotPermissionRequester();
            }
        } catch (final RuntimeException ignored) {
            openScreenshotPermissionRequester();
        }
    }

    protected boolean hasScreenshotPermission() {
        return screenshotPermission != null;
    }


    protected void openScreenshotPermissionRequester() {
        final Intent intent = new Intent(mContext, AcquireMediaProjectionPermissionIntent.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public static class AcquireMediaProjectionPermissionIntent extends Activity {
        int REQUEST_CODE = 1;

//        public AcquireMediaProjectionPermissionIntent() {
//            super();
//        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
        }

        @Override
        public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (REQUEST_CODE == requestCode) {
                if (Activity.RESULT_OK == resultCode) {
                    setScreenshotPermission(data);
                }
            } else if (Activity.RESULT_CANCELED == resultCode) {
                setScreenshotPermission(null);
                Log.i(mContext.getClass().getSimpleName(), "no access");

            }
            finish();
        }
    }

    public class ImageAvailableListener implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mImageReader.setOnImageAvailableListener(null, null);
            Log.e(TAG, "in OnImageAvailable");
            Bitmap bmp = null;
            Image image = null;
            try {
                image = mImageReader.acquireLatestImage();
                if (image != null) {
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int offset = 0;
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    if (bmp == null || bmp.getWidth() != mWidth + rowPadding / pixelStride ||
                            bmp.getHeight() != mHeight) {
                        if (bmp != null) {
                            bmp.recycle();
                        }

                        bmp = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight,
                                Bitmap.Config.ARGB_8888);

                    }

// create bitmap
                    bmp.copyPixelsFromBuffer(buffer);
                    if (bmp == null)
                        Log.e(TAG, "fuck");
                    resolveScreenshot(bmp);

                    image.close();
                    stopMediaProjection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {


                if (bmp != null) {
                    bmp.recycle();
                }

                if (image != null) {
                    image.close();
                }

                //sMediaProjection.stop();
                //stopProjection();
            }

        }
    }
}