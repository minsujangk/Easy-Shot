package doortodoor.neat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

/*
*  OnCapturedActivity extends AppCompatActivity
*  클립보드 변화를 감지했을 때 호출되는 액티비티
* */

public class OnCapturedActivity extends AppCompatActivity {
    private String mURL;
    private Handler handler;
    private Runnable runnable_FinishActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_success_notification);
        setWindowParams();

        //intent에서 url 정보를 받아옴
        Intent intent = getIntent();
        mURL = intent.getStringExtra("link");
        Toast.makeText(this, mURL, Toast.LENGTH_SHORT).show();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // 실행 애니메이션 삭제
        overridePendingTransition(0, 0);

        //레이아웃 크기 설정 : 가로를 가득 채우도록
        final FrameLayout layout_oncaptured = (FrameLayout) findViewById(R.id.layout_oncaptured);
        ViewGroup.LayoutParams params = layout_oncaptured.getLayoutParams();
        params.width = metrics.widthPixels;
        layout_oncaptured.requestLayout();

        //나타나는 애니메이션
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        layout_oncaptured.startAnimation(anim);

        //사라지는 애니메이션
        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(300);

        //텍스트뷰
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(mURL + "을 " + "저장하시겠습니까?");

        //yes 버튼 클릭할 시 데이터베이스에 저장되고 AfterCapturedActivity를 호출
        Button button = (Button) findViewById(R.id.button_yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToRealm(mURL, "hi");
                finish();
                overridePendingTransition(0, 0);
                layout_oncaptured.setAnimation(fadeOut);
                Intent intent = new Intent(OnCapturedActivity.this, AfterCapturedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("link", mURL);
                startActivity(intent);
            }
        });

        //No 버튼, 클릭할 시 액티비티 종료.
        Button button2 = (Button) findViewById(R.id.button_no);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);

                layout_oncaptured.setAnimation(fadeOut);
            }
        });


        //일정 시간 이후 액티비티를 종료하는 handler와 runnable
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
                        addToRealm(mURL, "hi");
                        finish();
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                layout_oncaptured.startAnimation(fadeOut);
            }
        };
        attachRunnable(3000);


    }

    //Realm에 데이터베이스 저장
    public void addToRealm(final String url, String folder) {

// Use the config
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() { // must be in transaction for this to work
            @Override
            public void execute(Realm realm) {
                // increment index
                Number currentIdNum = realm.where(LinkData.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 0;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                LinkData linkData = new LinkData(); // unmanaged
                linkData.setId(nextId);
                linkData.setUrl(url);
                linkData.setFolder("hi");
                realm.insertOrUpdate(linkData); // using insert API
            }
        });
    }

    /*액티비티 윈도우의 각종 파라미터.
    * dimAmount : 윈도우의 빈 부분을 까맣게 처리할지
    * flags : FLAG_NOT_TOUCH_MODAL - 액티비티 레이아웃의 바깥 부분 터치를 뒤에 있는 액티비티로 전달할지
    * gravity : 화면의 어디에 배열할지*/
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

    //Runnable 해제
    private void detachRunnable() {
        handler.removeCallbacks(runnable_FinishActivity);
    }

    //Runnable 설정
    private void attachRunnable(long time) {
        handler.postDelayed(runnable_FinishActivity, time);
    }
}
