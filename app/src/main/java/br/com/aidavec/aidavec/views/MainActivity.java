package br.com.aidavec.aidavec.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.Calendar;

import br.com.aidavec.aidavec.R;
import br.com.aidavec.aidavec.base.BaseActivity;
import br.com.aidavec.aidavec.controls.RoundedImageView;
import br.com.aidavec.aidavec.core.AidavecController;
import br.com.aidavec.aidavec.core.AidavecLocation;
import br.com.aidavec.aidavec.core.Parameters;
import br.com.aidavec.aidavec.fragments.AboutFrag;
import br.com.aidavec.aidavec.core.Globals;
import br.com.aidavec.aidavec.helpers.Utils;
import br.com.aidavec.aidavec.helpers.VolleyHelper;
import br.com.aidavec.aidavec.services.AidavecLocationService;
import br.com.aidavec.aidavec.services.AidavecMotionService;
import br.com.aidavec.aidavec.fragments.ConfigFrag;
import br.com.aidavec.aidavec.fragments.HomeFrag;
import br.com.aidavec.aidavec.fragments.InviteFrag;
import br.com.aidavec.aidavec.fragments.NoteFrag;
import br.com.aidavec.aidavec.fragments.ReportFrag;
import br.com.aidavec.aidavec.services.AidavecSyncBroadcast;

/**
 * Created by Leonardo Saganski on 20/11/16.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FrameLayout mContentFrame;
    private int mCurrentSelectedPosition;
    public static ImageView btnUnreadNotes;
    public static TextView lblUnreadNotes;
    public static FrameLayout flUnreadNotes;

    TextView lblName;
    TextView lblEmail;
    RoundedImageView imgProfile;
    ProgressBar prbProgress;

    ImageLoader imageLoader;

    Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 101) {  // ATUALIZAR BAGDE DE UNREAD NOTIFICATIONS
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (flUnreadNotes != null)
                                flUnreadNotes.setVisibility(Globals.getInstance().countUnreadNotes == 0 ? View.GONE : View.VISIBLE);
                            if (lblUnreadNotes != null)
                                lblUnreadNotes.setText(String.valueOf(Globals.getInstance().countUnreadNotes));

                        }
                    });
                } else if (msg.what == 102) { // ATUALIZAR USER DATA NO NAVIGATION DRAWER HEADER
                    RefreshDrawerHeader();
                } else if (msg.what == 103) { // PROGRESS ON - Sync
                    ProgressOn();
                } else if (msg.what == 104) { // PROGRESS OFF - Sync
                    ProgressOff();
                }
            } catch (Exception e) {
                Utils.getInstance().saveLog("MainActivity handlerUI", e.getMessage().toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            context = this;

            Globals.getInstance().context = this;
            Globals.getInstance().handlerUI = handlerUI;

            setUpToolbar();

            Globals.getInstance().startCountUnreadNotes();

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mNavigationView = (NavigationView) findViewById(R.id.nav_view);

            imageLoader = VolleyHelper.getInstance().getImageLoader();

            View header = LayoutInflater.from(this).inflate(R.layout.nav_drawer_header, mNavigationView);
            lblName = (TextView) mNavigationView.findViewById(R.id.lblName);
            lblEmail = (TextView) mNavigationView.findViewById(R.id.lblEmail);
            imgProfile = (RoundedImageView) mNavigationView.findViewById(R.id.imgProfile);

            setUpNavDrawer();

            // Carrega dados que estavam gravados nos preferences
            RefreshDrawerHeader();

            // Busca dados atualizados do server
            //Api.getInstance().GetUser(handlerGetUser);

            mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);

            if (Globals.getInstance().showThisFrag != null)
                replaceFragment(Globals.getInstance().showThisFrag);
            else
                replaceFragment(new HomeFrag());

            AidavecController.getInstance().getLastWaypoint();

            Globals.getInstance().updateDeviceTokenIfNeeded();

            setupAlarm();
        } catch (Exception e) {
            Utils.getInstance().saveLog("MainActivity onCreate", e.getMessage().toString());
        }

    }

    private void RefreshDrawerHeader() {
        lblName.setText(Globals.getInstance().loggedUser.getUsr_nome());
        lblEmail.setText(Globals.getInstance().loggedUser.getUsr_email());

        VolleyHelper.getInstance().getImageLoader().get(Globals.getInstance().apiPath + "images/" + String.valueOf(Globals.getInstance().loggedUser.getUsr_id()) + ".jpg",
                imageLoader.getImageListener(imgProfile, R.drawable.ico_camera, R.drawable.ico_camera));

    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            btnUnreadNotes = (ImageView) mToolbar.findViewById(R.id.btnUnreadNotes);
            lblUnreadNotes = (TextView) mToolbar.findViewById(R.id.lblUnreadNotes);
            flUnreadNotes = (FrameLayout) mToolbar.findViewById(R.id.flUnreadNotes);
            prbProgress = (ProgressBar) mToolbar.findViewById(R.id.prbProgress);

            btnUnreadNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replaceFragment(new NoteFrag());
                }
            });
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setCheckable(true);
        item.setChecked(true);

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            replaceFragment(new HomeFrag());
            mCurrentSelectedPosition = 0;
        } else if (id == R.id.nav_report) {
            replaceFragment(new ReportFrag());
            mCurrentSelectedPosition = 1;
        } else if (id == R.id.nav_notification) {
            replaceFragment(new NoteFrag());
            mCurrentSelectedPosition = 2;
        } else if (id == R.id.nav_config) {
            replaceFragment(new ConfigFrag());
            mCurrentSelectedPosition = 3;
        } else if (id == R.id.nav_invite) {
            replaceFragment(new InviteFrag());
            mCurrentSelectedPosition = 4;
        } else if (id == R.id.nav_about) {
            replaceFragment(new AboutFrag());
            mCurrentSelectedPosition = 5;
        }

        mDrawerLayout.closeDrawers();

        return true;
    }

    private void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_contentframe, frag, "TAG").addToBackStack(null).commit();
    }

    @Override
    protected void onDestroy() {
        if (Utils.isMyServiceRunning(AidavecLocationService.class)) {
//            AidavecLocationService.getInstance().stopService(new Intent(this, AidavecLocationService.class));
            AidavecLocationService.getInstance().onDestroy();
        }

        if (Utils.isMyServiceRunning(AidavecMotionService.class)) {
 //           AidavecMotionService.getInstance().stopService(new Intent(this, AidavecMotionService.class));
            AidavecMotionService.getInstance().onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void setupAlarm() {
        cancelAlarm();
        Intent intent = new Intent(AidavecSyncBroadcast.ACTION);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AidavecSyncBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every 5 seconds
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY

        long interval = Parameters.SYNC_INTERVAL;
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, getNextTime(),
                interval, pIntent);    // AlarmManager.INTERVAL_FIFTEEN_MINUTES
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AidavecSyncBroadcast.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AidavecSyncBroadcast.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private long getNextTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 5);
        long time = c.getTimeInMillis();
        return time;
    }

    private void ProgressOn() {
        prbProgress.setVisibility(View.VISIBLE);
    }

    private void ProgressOff() {
        prbProgress.setVisibility(View.GONE);
    }

}
