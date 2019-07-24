package com.notificationremove.notificationblocker.MODEL;

import io.realm.RealmObject;

public class Notification_History extends RealmObject {

    String apkname;

    int notification_count;

    public String getApkname() {
        return this.apkname;
    }

    public void setApkname(String value) {
        this.apkname = value;
    }

    public String getNotification_count() {
        return this.notification_count + "";
    }

    public void setNotification_count(String value) {
        this.notification_count = Integer.parseInt(value);
    }
}
