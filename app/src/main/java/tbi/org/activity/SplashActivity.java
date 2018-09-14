package tbi.org.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import tbi.org.R;
import tbi.org.activity.main_activity.CaretakerHomeActivity;
import tbi.org.activity.main_activity.SuffererHomeActivity;
import tbi.org.session.Session;
import tbi.org.util.Constant;

public class SplashActivity extends AppCompatActivity {

    private Animation zoomOut;
    private FrameLayout uppFrame;
    private ImageView inImage;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);
        initView();

        session = new Session(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        session.setSoftKey(hasSoftKeys(getWindowManager()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!session.getIsLogedIn()) {
                    Intent mainIntent = new Intent(SplashActivity.this, UserSelectionActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                } else {
                    if (session.getUserType().equals("1")) {
                        Intent intent = new Intent(SplashActivity.this, SuffererHomeActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, CaretakerHomeActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }
                overridePendingTransition(R.anim.anim_left_to_right, R.anim.anim_right_to_left);
            }
        }, Constant.SPLESH_TIME);

        uppFrame.startAnimation(zoomOut);
        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                inImage.setVisibility(View.VISIBLE);
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(inImage, PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                        PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
                scaleDown.setDuration(800);
                scaleDown.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initView() {
        uppFrame = findViewById(R.id.uppFrame);
        inImage = findViewById(R.id.inImage);
        zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
    }

    public boolean hasSoftKeys(WindowManager windowManager) {
        boolean hasSoftwareKeys;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = windowManager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }
}