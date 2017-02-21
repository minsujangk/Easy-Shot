package doortodoor.neat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

/*
* AfterCapturedActivity extends AppCompatActivity
* 캡쳐가 실행되고, 저장을 한 뒤 호출되는 Activity이다.
* OnCapturedActivity에서 yes를 선택할 시 실행된다.
* */

public class AfterCapturedActivity extends AppCompatActivity {
    private String mURL;
    private Handler handler;
    private Runnable runnable_FinishActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.after_captured_notification);
        setWindowParams();

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        //intent로 전달받은 url 데이터를 mURL에 넣음.
        mURL = getIntent().getStringExtra("link");

        //레이아웃 크기 설정 : 가로를 가득 채우도록
        final FrameLayout layout_after_captured = (FrameLayout) findViewById(R.id.after_captured_layout_oncaptured);
        ViewGroup.LayoutParams params = layout_after_captured.getLayoutParams();
        params.width = metrics.widthPixels;
        layout_after_captured.requestLayout();

        //시작 애니메이션 설정정
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        layout_after_captured.startAnimation(anim);

        // 종료 애니메이션
        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(300);

        //텍스트뷰 내용 설정.
        TextView textView = (TextView) findViewById(R.id.after_captured_textView);
        textView.setText(mURL + "을 " + "확인하러 가시겠습니까?");

        //yes 버튼, 클릭시 종료 애니메이션을 실행하고 MainActivity를 띄운다.
        Button button = (Button) findViewById(R.id.after_captured_button_yes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                layout_after_captured.setAnimation(fadeOut);
                Intent intent = new Intent(AfterCapturedActivity.this, MainActivity.class);
                startActivity(intent);
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
                        finish();
                        overridePendingTransition(0, 0);
                        layout_after_captured.setAnimation(fadeOut);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        };
        attachRunnable(1500);
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

    // Runnable 설정
    private void attachRunnable(long time) {
        handler.postDelayed(runnable_FinishActivity, time);
    }
}
