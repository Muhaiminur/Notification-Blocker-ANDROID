package com.notificationremove.notificationblocker.MODEL;

import io.realm.RealmObject;

public class BlockList extends RealmObject {
    /**
     * Property package_name
     */
    String package_name;
    String status;

    /**
     * Constructor
     */
    public BlockList() {
    }

    /**
     * Gets the package_name
     */
    public String getPackage_name() {
        return this.package_name;
    }

    /**
     * Sets the package_name
     */
    public void setPackage_name(String value) {
        this.package_name = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BlockList(String package_name, String status) {
        this.package_name = package_name;
        this.status = status;
    }
}
