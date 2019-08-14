package com.zyn.lib.view.viewpage;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;


public class TabInfo implements Parcelable
{

    private int id;
    private int icon;
    private String name = null;
    public boolean hasTips = false;
    public Fragment fragment = null;
    public boolean notifyChange = false;

    public TabInfo(int id, String name, Fragment f) {
        this(id, name, 0, f);
    }

    public TabInfo(int id, String name, boolean hasTips, Fragment f) {
        this(id, name, 0, f);
        this.hasTips = hasTips;
        fragment = f;
    }



    public TabInfo(int id, String name, int iconid, android.support.v4.app.Fragment f) {
        super();
        this.name = name;
        this.id = id;
        icon = iconid;
        fragment = f;
    }

    public TabInfo(Parcel p) {
        this.id = p.readInt();
        this.name = p.readString();
        this.icon = p.readInt();
        this.notifyChange = p.readInt() == 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int iconid) {
        icon = iconid;
    }

    public int getIcon() {
        return icon;
    }

    public static final Creator<TabInfo> CREATOR = new Creator<TabInfo>() {
        public TabInfo createFromParcel(Parcel p) {
            return new TabInfo(p);
        }

        public TabInfo[] newArray(int size) {
            return new TabInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeString(name);
        p.writeInt(icon);
        p.writeInt(notifyChange ? 1 : 0);
    }
}
