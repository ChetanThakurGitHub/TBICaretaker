package tbi.com.activity.main_activity;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tbi.com.R;
import tbi.com.activity.NotificationRead;
import tbi.com.adapter.SuffererNavigationAdapter;
import tbi.com.broadcastreceiver.activity.NetworkErrorActivity;
import tbi.com.chat.fragment.MessageSuffererFragment;
import tbi.com.custom_calender.activity.CalanderSuffererActivity;
import tbi.com.fragment.sufferer.FaqsSuffererFragment;
import tbi.com.fragment.sufferer.MyCaretakerFragment;
import tbi.com.fragment.sufferer.ReminderSuffererFragment;
import tbi.com.fragment.sufferer.activity.EditProfileSuffererActivity;
import tbi.com.helper.PermissionAll;
import tbi.com.model.NavigationListModel;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;

import static tbi.com.util.Constant.CALLING;

public class SuffererHomeActivity extends NotificationRead implements View.OnClickListener {

    public DrawerLayout drawer;
    public TextView tv_for_tittle;
    public ImageView iv_for_profileImage, iv_for_calender, iv_for_menu, iv_for_backIco, iv_for_edit,
            iv_for_more, iv_for_delete, iv_for_deleteChat, iv_for_block;
    public SuffererNavigationAdapter navigationAdapter;
    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (!NetworkErrorActivity.isOptedToOffline()) {
                    Intent intent1 = new Intent(SuffererHomeActivity.this, NetworkErrorActivity.class);
                    startActivity(intent1);
                }
            } else {
                NetworkErrorActivity.setOptedToOffline(false);
            }
        }
    };
    private NavigationView navigation_view;
    private Session session;
    private ArrayList<NavigationListModel> navigationList;
    private RecyclerView recycler_view;
    private boolean doubleBackToExitPressedOnce = false;
    private String notification = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sufferer_home);
        session = new Session(this);
        initView();

        Constant.NETWORK_CHECK = 0;
        if (!Utils.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, NetworkErrorActivity.class);
            startActivity(intent);
        }

        try {
            notification = getIntent().getStringExtra("NOTIFICATION");
            String notification_id = getIntent().getStringExtra("notification_id");
            setNotificationId(notification_id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String image = session.getProfileImage();
        if (image != null && !image.equals("")) {
            Picasso.with(this).load(image).into(iv_for_profileImage);
        }

        navigationList = new ArrayList<>();
        addItemInList();
        navigationAdapter = new SuffererNavigationAdapter(navigationList, this);
        recycler_view.setAdapter(navigationAdapter);
        addFragment(ReminderSuffererFragment.newInstance(), false, R.id.framlayout);

        if (notification != null && !notification.equals("")) {
            if (notification.equals("data")) {
                tv_for_tittle.setText(R.string.my_caretaker);
                iv_for_calender.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.VISIBLE);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_delete.setVisibility(View.GONE);
                replaceFragment(MyCaretakerFragment.newInstance(), true, R.id.framlayout);
            } else if (notification.equals("faqSufferer")) {
                tv_for_tittle.setText(R.string.faq_s);
                iv_for_calender.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.VISIBLE);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_delete.setVisibility(View.GONE);
                replaceFragment(FaqsSuffererFragment.newInstance(), true, R.id.framlayout);
            } else if (notification.equals("chat")) {
                tv_for_tittle.setText(R.string.messages);
                iv_for_calender.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.VISIBLE);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_block.setVisibility(View.VISIBLE);
                iv_for_deleteChat.setVisibility(View.VISIBLE);
                iv_for_delete.setVisibility(View.GONE);
                replaceFragment(MessageSuffererFragment.newInstance(), true, R.id.framlayout);
            }
        }
    }

    private void initView() {
        findViewById(R.id.iv_for_back).setOnClickListener(this);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        iv_for_profileImage = findViewById(R.id.iv_for_profileImage);
        TextView tv_for_name = findViewById(R.id.tv_for_name);
        TextView tv_for_email = findViewById(R.id.tv_for_email);
        tv_for_name.setText(session.getFullName());
        tv_for_email.setText(session.getEmail());
        drawer = findViewById(R.id.drawer);
        navigation_view = findViewById(R.id.navigation_view);
        recycler_view = findViewById(R.id.recycler_view);
        tv_for_tittle.setText(R.string.reminders);
        iv_for_calender = findViewById(R.id.iv_for_calender);
        iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_more = findViewById(R.id.iv_for_more);
        iv_for_delete = findViewById(R.id.iv_for_delete);
        iv_for_block = findViewById(R.id.iv_for_block);
        iv_for_deleteChat = findViewById(R.id.iv_for_deleteChat);
        findViewById(R.id.layout_for_calling).setOnClickListener(this);
        iv_for_menu.setOnClickListener(this);
        iv_for_calender.setOnClickListener(this);
        iv_for_backIco.setVisibility(View.GONE);
        iv_for_edit.setVisibility(View.GONE);
        iv_for_more.setVisibility(View.GONE);
        iv_for_delete.setVisibility(View.GONE);
        iv_for_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyboard(this);
        switch (v.getId()) {
            case R.id.iv_for_menu:
                if (drawer.isDrawerOpen(navigation_view)) {
                    drawer.closeDrawers();
                } else {
                    drawer.openDrawer(navigation_view);
                }
                break;
            case R.id.iv_for_calender:
                Intent intent = new Intent(SuffererHomeActivity.this, CalanderSuffererActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_for_back:
                drawer.closeDrawers();
                break;
            case R.id.iv_for_edit:
                intent = new Intent(SuffererHomeActivity.this, EditProfileSuffererActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_for_calling:
                drawer.closeDrawers();
                if (session.getContact() != null && !session.getContact().equals("")) {
                    Intent intent4 = new Intent(Intent.ACTION_CALL);
                    intent4.setData(Uri.parse("tel:" + session.getContact()));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        PermissionAll permissionAll = new PermissionAll();
                        permissionAll.checkCallingPermission(SuffererHomeActivity.this);
                        return;
                    }
                    startActivity(intent4);
                } else {
                    Toast.makeText(this, "No contact added", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void addItemInList() {
        NavigationListModel drawerItem;
        for (int i = 0; i <= 6; i++) {
            drawerItem = new NavigationListModel();
            switch (i) {
                case 0:
                    drawerItem.image = R.drawable.ic_calendar_gray;
                    drawerItem.selectedImage = R.drawable.ic_calendar_green;
                    drawerItem.name = getString(R.string.reminders);
                    break;
                case 1:
                    drawerItem.image = R.drawable.ic_user;
                    drawerItem.selectedImage = R.drawable.ic_user_green;
                    drawerItem.name = getString(R.string.my_profile);
                    break;
                case 2:
                    drawerItem.image = R.drawable.ic_message;
                    drawerItem.selectedImage = R.drawable.ic_message_green;
                    drawerItem.name = getString(R.string.messages);
                    break;
                case 3:
                    drawerItem.image = R.drawable.ic_user;
                    drawerItem.selectedImage = R.drawable.ic_user_green;
                    drawerItem.name = getString(R.string.my_caretaker);
                    break;
                case 4:
                    drawerItem.image = R.drawable.ic_notification;
                    drawerItem.selectedImage = R.drawable.ic_notification_green;
                    drawerItem.name = getString(R.string.notifications);
                    break;
                case 5:
                    drawerItem.image = R.drawable.ic_info;
                    drawerItem.selectedImage = R.drawable.ic_info_green;
                    drawerItem.name = getString(R.string.faq_s);
                    break;
                case 6:
                    drawerItem.image = R.drawable.ic_logout;
                    drawerItem.selectedImage = R.drawable.ic_logout_green;
                    drawerItem.name = getString(R.string.logout_txt);
                    break;
            }
            navigationList.add(drawerItem);
        }
    }

    @Override
    public void onBackPressed() {
        drawer.closeDrawers();
        if (session.getIsLogedIn()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                navigationAdapter.lastclick = 0;
                navigationAdapter.notifyDataSetChanged();
            } else {
                if (!doubleBackToExitPressedOnce) {
                    this.doubleBackToExitPressedOnce = true;
                    Constant.snackbar(drawer, getResources().getString(R.string.for_exit));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, Constant.BackPressed_Exit);
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        int i = fm.getBackStackEntryCount();
        while (i > 0) {
            fm.popBackStackImmediate();
            i--;
        }
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStackName).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = getFragmentManager().popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.add(containerId, fragment, backStackName); //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALLING: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //success permission granted & call Location method
                        Intent intent4 = new Intent(Intent.ACTION_CALL);
                        intent4.setData(Uri.parse("tel:" + session.getContact()));
                        startActivity(intent4);
                    }
                } else {
                    Toast.makeText(SuffererHomeActivity.this, "Deny Calling Permission", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
            if (Constant.NETWORK_CHECK == 1) {
                addFragment(ReminderSuffererFragment.newInstance(), false, R.id.framlayout);
                tv_for_tittle.setText(R.string.reminders);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_delete.setVisibility(View.GONE);
                iv_for_block.setVisibility(View.GONE);
                iv_for_deleteChat.setVisibility(View.GONE);
            }
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
