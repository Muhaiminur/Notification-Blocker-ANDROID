package com.notificationremove.notificationblocker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.Set;

import io.realm.Realm;

public class Status_Page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status__page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Realm.init(this);
        Notification_permission_check();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.sacreenarea, new On_Off_Page());
        tx.commit();

        //applyStatusBar("NOTIFICATION BLOCKER",112);

        if (isNotificationServiceRunning()) {
            Log.d("Notification", "Available");
        } else {
            Log.d("Notification", "NOT Available");
            //startActivity(new Intent(Status_Page.this,Tutorial_Page.class));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status__page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment frag = null;

        if (id == R.id.nav_easy) {
            try {
                //dev_notification();
                createNotification2("NOTIFICATION BLOCKER");
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }
        } else if (id == R.id.nav_home) {
            // Handle the camera action
            frag = new On_Off_Page();
        } else if (id == R.id.nav_history) {
            frag = new History_List();
        } else if (id == R.id.nav_oneclick) {
            try {
                Log.d("One click", "Remove");
                NotificationManager nManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                nManager.cancelAll();
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }
        } else if (id == R.id.nav_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "NOTIFICATION BLOCKER");
                String sAux = "\n Notification Blocker can save you from irritating Notification.\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                e.toString();
            }
        } else if (id == R.id.nav_rate_us) {
            try {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            } catch (Exception e) {

            }
        }/* else if (id == R.id.nav_abt) {
            //frag = new fragment_about();
        } */else if (id == R.id.nav_check) {
            createNotification("NOTIFICATION BLOCKER");
        } else if (id == R.id.nav_tutorial) {
            startActivity(new Intent(Status_Page.this, Tutorial_Page.class));
        }
        if (frag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.sacreenarea, frag);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Notification_permission_check() {
        try {
            if (isNotificationAccessGiven()) {
                Log.d("Access", "true");
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enable Notification Access")
                        .setMessage("Enable it otherwise Notification Blocker Will not Work")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                            }
                        })
                        .setCancelable(false).show();
                Log.d("Access", "False");
            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    private boolean isNotificationAccessGiven() {
        try {
            boolean enabled = false;
            Set<String> enabledListenerPackagesSet = NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext());
            for (String string : enabledListenerPackagesSet)
                if (string.contains(getPackageName())) enabled = true;
            return enabled;
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return false;
    }

    public void createNotification(String aMessage) {
        final int NOTIFY_ID = 77;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, Status_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            intent = new Intent(this, Status_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    public void createNotification2(String aMessage) {
        final int NOTIFY_ID = 11;
        String name = getString(R.string.app_name);
        String id = getString(R.string.app_name); // The user-visible name of the channel.
        String description = getString(R.string.app_name); // The user-visible description of the channel.
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setLightColor(getColor(R.color.colorPrimaryDark));
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
        } else {

        }
        Intent Off_broadcastIntent = new Intent(this, Database_Update.class);
        Off_broadcastIntent.setAction("on");
        Off_broadcastIntent.putExtra("toastMessage", "1");
        PendingIntent Off_actionIntent = PendingIntent.getService(this, 0, Off_broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent on_broadcastIntent = new Intent(this, Database_Update.class);
        on_broadcastIntent.setAction("off");
        on_broadcastIntent.putExtra("toastMessage", "0");
        PendingIntent on_actionIntent = PendingIntent.getService(this, 0, on_broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent cancel_broadcastIntent = new Intent(this, Database_Update.class);
        cancel_broadcastIntent.setAction("cancel");
        cancel_broadcastIntent.putExtra("toastMessage", "close");
        PendingIntent cancel_actionIntent = PendingIntent.getService(this, 0, cancel_broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent content_intent = new Intent(this, Status_Page.class);
        content_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, content_intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle(name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .addAction(R.drawable.ic_turn_notifications_off_button, "ON", Off_actionIntent)
                .addAction(R.drawable.ic_notification, "OFF", on_actionIntent)
                .addAction(R.drawable.ic_clear, "CLOSE", cancel_actionIntent);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        notifManager.notify(11, notification);
    }


    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }
}
