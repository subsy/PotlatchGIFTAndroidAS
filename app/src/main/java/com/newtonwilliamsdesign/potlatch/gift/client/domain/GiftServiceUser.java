package com.newtonwilliamsdesign.potlatch.gift.client.domain;

/***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************
 G I F T
 A Multi-user Web Application and Android Client Application
 for sharing of image gifts.

 Copyright (C) 2014 Newton Williams Design.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************************
 ***********************************************************************************
 ***********************************************************************************/

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;
import java.util.Set;

public class GiftServiceUser implements Parcelable {

    public static final Parcelable.Creator<GiftServiceUser> CREATOR
            = new Parcelable.Creator<GiftServiceUser>() {
        public GiftServiceUser createFromParcel(Parcel in) {
            return new GiftServiceUser(in);
        }

        public GiftServiceUser[] newArray(int size) {
            return new GiftServiceUser[size];
        }
    };
    private String username;
    private String password;
    private String name;
    private int touchedcount;
    private GiftUserPreferences userprefs;
    private Set<Gift> createdGifts = new HashSet<Gift>();

    public GiftServiceUser() {
        this.username = "";
        this.password = "";
    }

    public GiftServiceUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private GiftServiceUser(Parcel in) {
        this.username = in.readString();
        this.name = in.readString();
        this.touchedcount = in.readInt();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTouchedcount() {
        return touchedcount;
    }

    public void setTouchedcount(int touchedcount) {
        this.touchedcount = touchedcount;
    }

    public Set<Gift> getCreatedGifts() {
        return createdGifts;
    }

    public void setCreatedGifts(Set<Gift> createdGifts) {
        this.createdGifts = createdGifts;
    }

    public GiftUserPreferences getUserprefs() {
        return userprefs;
    }

    public void setUserprefs(GiftUserPreferences userprefs) {
        this.userprefs = userprefs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeString(name);
        out.writeInt(touchedcount);
    }

}
