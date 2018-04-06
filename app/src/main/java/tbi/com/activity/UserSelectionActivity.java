package tbi.com.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import tbi.com.R;
import tbi.com.broadcastreceiver.activity.NetworkErrorActivity;
import tbi.com.util.Constant;
import tbi.com.util.Utils;

public class UserSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (!NetworkErrorActivity.isOptedToOffline()) {
                    Intent intent1 = new Intent(UserSelectionActivity.this, NetworkErrorActivity.class);
                    startActivity(intent1);
                }
            } else {
                NetworkErrorActivity.setOptedToOffline(false);
            }
        }
    };
    private boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_selection);
        initView();

        if (!Utils.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, NetworkErrorActivity.class);
            startActivity(intent);
        }

    }

    private void initView() {
        findViewById(R.id.layout_for_sufferer).setOnClickListener(this);
        findViewById(R.id.layout_for_caretaker).setOnClickListener(this);
        mainLayout = findViewById(R.id.mainLayout);
    }

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Constant.snackbar(mainLayout, getResources().getString(R.string.for_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, Constant.BackPressed_Exit);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_for_sufferer:
                Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
                intent.putExtra("userType", "1");
                startActivity(intent);
                break;
            case R.id.layout_for_caretaker:
                intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
                intent.putExtra("userType", "2");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
            //do action
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(netSwitchReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
