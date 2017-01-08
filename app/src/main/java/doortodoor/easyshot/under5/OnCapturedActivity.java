package doortodoor.easyshot.under5;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import doortodoor.easyshot.R;

public class OnCapturedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_captured);

        Button button = (Button) findViewById(R.id.button3);
        Button button2 = (Button) findViewById(R.id.button4);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = findViewById(R.id.activity_on_captured);
                view.setDrawingCacheEnabled(true);
                //  Bitmap cachedBitmap = screenShot(view);
//        Bitmap cache = view.getDrawingCache();
                Bitmap cachedBitmap = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);


                String IMAGES_PRODUCED = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String exStorage = Environment.getExternalStorageDirectory().toString();

                //폴더를 찾는다. 존재하지 않을 경우 폴더를 만들어 준다.
                File folder = new File(exStorage, "/ddd2");
                boolean isFolder = true;
                if (!(isFolder = folder.exists())) {
                    isFolder = folder.mkdirs();
                    if (isFolder) {
                        Log.i(getPackageName(), "Creating folder success");
                    } else {
                        Log.e(getPackageName(), "Creating folder failure");
                    }
                }

                //폴더에 파일을 만들어 준다.
                File file = new File(folder, "myscreen_" + IMAGES_PRODUCED + ".png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    cachedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    Toast.makeText(getApplicationContext(), "Success2", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
