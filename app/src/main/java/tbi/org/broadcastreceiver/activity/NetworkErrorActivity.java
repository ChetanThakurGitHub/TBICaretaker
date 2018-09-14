package tbi.org.broadcastreceiver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tbi.org.R;
import tbi.org.util.Constant;

public class NetworkErrorActivity extends AppCompatActivity {
    private static boolean optedToOffline = false;
    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (isConnectionAvailable) {
                optedToOffline = false;
                finish();
            }
        }
    };

    public static boolean isOptedToOffline() {
        return optedToOffline;
    }

    public static void setOptedToOffline(boolean optedToOffline) {
        NetworkErrorActivity.optedToOffline = optedToOffline;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        Constant.NETWORK_CHECK = 1;

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(NetworkErrorActivity.this, NetworkErrorActivity.class);
        startActivity(intent);
    }
}
