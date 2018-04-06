package tbi.com.activity.main_activity;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tbi.com.R;
import tbi.com.activity.NotificationRead;
import tbi.com.adapter.CaretakerNavigationAdapter;
import tbi.com.broadcastreceiver.activity.NetworkErrorActivity;
import tbi.com.chat.fragment.MessageCaretakerFragment;
import tbi.com.custom_calender.activity.CalanderCaretakerActivity;
import tbi.com.fragment.caretaker.AddSuffererFragment;
import tbi.com.fragment.caretaker.FaqsCaretakerFragment;
import tbi.com.fragment.caretaker.ReminderCaretakerFragment;
import tbi.com.fragment.caretaker.activity.EditProfileCaretakerActivity;
import tbi.com.model.NavigationListModel;
import tbi.com.session.Session;
import tbi.com.util.Constant;
import tbi.com.util.Utils;

public class CaretakerHomeActivity extends NotificationRead implements View.OnClickListener {

    public DrawerLayout drawer;
    public CaretakerNavigationAdapter navigationAdapter;
    public TextView tv_for_tittle;
    public ImageView iv_for_calender, iv_for_menu, iv_for_backIco, iv_for_edit, iv_for_more,
            iv_for_delete, iv_for_deleteChat, iv_for_block;
    BroadcastReceiver netSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnectionAvailable = intent.getExtras().getBoolean("is_connected");
            if (!isConnectionAvailable) {
                if (!NetworkErrorActivity.isOptedToOffline()) {
                    Intent intent1 = new Intent(CaretakerHomeActivity.this, NetworkErrorActivity.class);
                    startActivity(intent1);
                }
            } else {
                NetworkErrorActivity.setOptedToOffline(false);
            }
        }
    };
    private NavigationView navigation_view;
    private Session session;
    private ImageView iv_for_profileImage;
    private ArrayList<NavigationListModel> navigationList;
    private RecyclerView recycler_view;
    private boolean doubleBackToExitPressedOnce = false;
    private LinearLayout layout_for_addDelete;
    private Boolean viewVisble = false;
    private String notification = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caretaker_home);
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
        navigationAdapter = new CaretakerNavigationAdapter(navigationList, this);
        recycler_view.setAdapter(navigationAdapter);

        if (session.getLogin().equals("0")) {
            Fragment fragment = AddSuffererFragment.newInstance();
            addFragment(fragment, false, R.id.framlayout);
            tv_for_tittle.setText(R.string.add_sufferer);
            tv_for_tittle.setText(R.string.add_sufferer);
            iv_for_calender.setVisibility(View.GONE);
            iv_for_menu.setVisibility(View.VISIBLE);
            iv_for_backIco.setVisibility(View.GONE);
            iv_for_edit.setVisibility(View.GONE);
            iv_for_more.setVisibility(View.GONE);
            iv_for_delete.setVisibility(View.GONE);
        } else {
            Fragment fragment = ReminderCaretakerFragment.newInstance();
            addFragment(fragment, false, R.id.framlayout);
            tv_for_tittle.setText(R.string.reminders);
            iv_for_backIco.setVisibility(View.GONE);
            iv_for_edit.setVisibility(View.GONE);
            iv_for_more.setVisibility(View.GONE);
            iv_for_delete.setVisibility(View.GONE);
        }

        if (notification != null && !notification.equals("")) {
            if (notification.equals("addSufferer")) {
                tv_for_tittle.setText(R.string.add_sufferer);
                iv_for_calender.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.VISIBLE);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_delete.setVisibility(View.GONE);
                replaceFragment(AddSuffererFragment.newInstance(), true, R.id.framlayout);
            } else {
                tv_for_tittle.setText(R.string.messages);
                iv_for_calender.setVisibility(View.GONE);
                iv_for_menu.setVisibility(View.VISIBLE);
                iv_for_backIco.setVisibility(View.GONE);
                iv_for_edit.setVisibility(View.GONE);
                iv_for_more.setVisibility(View.GONE);
                iv_for_block.setVisibility(View.VISIBLE);
                iv_for_deleteChat.setVisibility(View.VISIBLE);
                iv_for_delete.setVisibility(View.GONE);
                replaceFragment(MessageCaretakerFragment.newInstance(), true, R.id.framlayout);
            }
        }

        iv_for_edit.setOnClickListener(this);
    }

    private void initView() {
        iv_for_menu = findViewById(R.id.iv_for_menu);
        iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_edit = findViewById(R.id.iv_for_edit);
        iv_for_more = findViewById(R.id.iv_for_more);
        iv_for_delete = findViewById(R.id.iv_for_delete);
        iv_for_block = findViewById(R.id.iv_for_block);
        iv_for_deleteChat = findViewById(R.id.iv_for_deleteChat);
        layout_for_addDelete = findViewById(R.id.layout_for_addDelete);
        findViewById(R.id.iv_for_back).setOnClickListener(this);
        findViewById(R.id.tv_for_logout).setOnClickListener(this);
        tv_for_tittle = findViewById(R.id.tv_for_tittle);
        iv_for_profileImage = findViewById(R.id.iv_for_profileImage);
        TextView tv_for_name = findViewById(R.id.tv_for_name);
        TextView tv_for_email = findViewById(R.id.tv_for_email);
        tv_for_name.setText(session.getFullName());
        tv_for_email.setText(session.getEmail());
        drawer = findViewById(R.id.drawer);
        navigation_view = findViewById(R.id.navigation_view);
        recycler_view = findViewById(R.id.recycler_view);
        iv_for_calender = findViewById(R.id.iv_for_calender);
        iv_for_menu.setOnClickListener(this);
        iv_for_calender.setOnClickListener(this);
        iv_for_more.setOnClickListener(this);
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
            case R.id.iv_for_back:
                drawer.closeDrawers();
                break;
            case R.id.tv_for_logout:
                Constant.logout(this);
                break;
            case R.id.iv_for_edit:
                Intent intent1 = new Intent(this, EditProfileCaretakerActivity.class);
                startActivity(intent1);
                break;
            case R.id.iv_for_calender:
                intent1 = new Intent(this, CalanderCaretakerActivity.class);
                startActivity(intent1);
                break;
            case R.id.iv_for_more:
                iv_for_delete.setVisibility(View.GONE);
                Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
                if (fragment instanceof FaqsCaretakerFragment) {
                    ((FaqsCaretakerFragment) fragment).caretakerFAQAdapter.isShow = false;
                    ((FaqsCaretakerFragment) fragment).caretakerFAQAdapter.notifyDataSetChanged();
                }
                if (!viewVisble) {
                    layout_for_addDelete.setVisibility(View.VISIBLE);
                    viewVisble = true;
                } else {
                    layout_for_addDelete.setVisibility(View.GONE);
                    viewVisble = false;
                }
                break;
        }
    }

    private void addItemInList() {
        NavigationListModel drawerItem;
        for (int i = 0; i <= 5; i++) {
            drawerItem = new NavigationListModel();
            switch (i) {
                case 0:
                    drawerItem.image = R.drawable.ic_user;
                    drawerItem.selectedImage = R.drawable.ic_user_green;
                    drawerItem.name = getString(R.string.my_sufferer);
                    break;
                case 1:
                    drawerItem.image = R.drawable.ic_calendar_gray;
                    drawerItem.selectedImage = R.drawable.ic_calendar_green;
                    drawerItem.name = getString(R.string.reminders);
                    break;
                case 2:
                    drawerItem.image = R.drawable.ic_user;
                    drawerItem.selectedImage = R.drawable.ic_user_green;
                    drawerItem.name = getString(R.string.my_profile);
                    break;
                case 3:
                    drawerItem.image = R.drawable.ic_message;
                    drawerItem.selectedImage = R.drawable.ic_message_green;
                    drawerItem.name = getString(R.string.messages);
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
            }
            navigationList.add(drawerItem);
        }
    }

    @Override
    public void onBackPressed() {
        drawer.closeDrawers();
        if (session.getIsLogedIn()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                navigationAdapter.lastclick = 1;
                navigationAdapter.notifyDataSetChanged();
                getSupportFragmentManager().popBackStack();
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
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(netSwitchReceiver, new IntentFilter(Constant.NETWORK_SWITCH_FILTER));
            if (Constant.NETWORK_CHECK == 1) {
                addFragment(ReminderCaretakerFragment.newInstance(), false, R.id.framlayout);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fragManager = this.getSupportFragmentManager();
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = fragManager.getFragments().get(count > 0 ? count - 1 : count);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
