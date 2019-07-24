package com.notificationremove.notificationblocker;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.notificationremove.notificationblocker.MODEL.BlockList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Database_Update extends Service {
    String result = "";

    Realm realm;
    BlockList blockList;

    @Override
    public void onCreate() {
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        } catch (Exception e) {

            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        try {
            Log.d("SERVICE", "SERVICE CHECKING");
            result = intent.getStringExtra("toastMessage");
            Log.d("SERVICE", result);
            if (realm != null) {
                Log.d("SERVICE", "realm working");
            } else {
                Log.d("SERVICE", "Realm not working");
            }
            blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
            try {
                Log.d("SERVICE", blockList.getStatus());
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
            }
            realm.beginTransaction();
            if (result.equals("1")) {
                if (blockList == null) {
                    BlockList blockList_new = realm.createObject(BlockList.class);
                    blockList_new.setPackage_name("BLOCK_ALL");
                    blockList_new.setStatus("yes");
                } else {
                    blockList.setStatus("yes");
                }
                Log.d("SERVICE", "BLOCKING NOTIFICATION");
                Toast.makeText(this, "BLOCKING", Toast.LENGTH_SHORT).show();
            } else if (result.equals("0")) {
                if (blockList == null) {
                    BlockList blockList_new = realm.createObject(BlockList.class);
                    blockList_new.setPackage_name("BLOCK_ALL");
                    blockList_new.setStatus("not_at_all");
                } else {
                    blockList.setStatus("not_at_all");
                }
                Log.d("SERVICE", "ALLOW NOTIFICATION");
                Toast.makeText(this, "ALLOW NOTIFICATION", Toast.LENGTH_SHORT).show();
            } else if (result.equals("close")) {
                NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(11);
                Log.d("SERVICE", "REMOVING");
                Toast.makeText(this, "CLOSED", Toast.LENGTH_SHORT).show();
            }
            realm.commitTransaction();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        if (realm != null) {
            realm.close();
        }
        Toast.makeText(this, "REMOVING", Toast.LENGTH_SHORT).show();
    }
}