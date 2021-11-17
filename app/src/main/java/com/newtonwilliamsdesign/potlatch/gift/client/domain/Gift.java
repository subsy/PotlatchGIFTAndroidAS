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

import com.google.common.base.Objects;

import java.util.HashSet;
import java.util.Set;

public class Gift {

    private long id;
    private long parentid;

    private GiftServiceUser createdby;

    private String title;
    private String description;

    private String imageurl;
    private String thumburl;
    private long flags;
    private long touches;
    private long createdon;
    private long modifiedon;

    private Set<String> touchesUsernames = new HashSet<String>();

    private Set<String> flagsUsernames = new HashSet<String>();

    public Gift() {
        long currTime = System.currentTimeMillis();
        this.setParentid(0);
        this.title = "Title";
        this.description = "Description";
        this.imageurl = "";
        this.thumburl = "";
        this.flags = 0;
        this.touches = 0;
        this.createdon = currTime;
        this.modifiedon = currTime;
    }

    public Gift(long parentid, String title, String description, String imageurl, String thumburl, long flags, long touches, long created, long modified) {
        super();
        this.parentid = parentid;
        this.title = title;
        this.description = description;
        this.imageurl = imageurl;
        this.thumburl = thumburl;
        this.flags = flags;
        this.touches = touches;
        this.createdon = created;
        this.modifiedon = modified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentid() {
        return parentid;
    }

    public void setParentid(long parentid) {
        this.parentid = parentid;
    }

    public GiftServiceUser getCreatedby() {
        return createdby;
    }

    public void setCreatedby(GiftServiceUser createdby) {
        this.createdby = createdby;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getThumburl() {
        return thumburl;
    }

    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public long getTouches() {
        return touches;
    }

    public void setTouches(long touches) {
        this.touches = touches;
    }

    public long getCreatedon() {
        return createdon;
    }

    public void setCreatedon(long createdon) {
        this.createdon = createdon;
    }

    public long getModifiedon() {
        return modifiedon;
    }

    public void setModifiedon(long modifiedon) {
        this.modifiedon = modifiedon;
    }

    public Set<String> getTouchesUsernames() {
        return touchesUsernames;
    }

    public void setTouchesUsernames(Set<String> touchesUsernames) {
        this.touchesUsernames = touchesUsernames;
    }

    public Set<String> getFlagsUsernames() {
        return flagsUsernames;
    }

    public void setFlagsUsernames(Set<String> flagsUsernames) {
        this.flagsUsernames = flagsUsernames;
    }


    /**
     * Two Gifts will generate the same hashcode if they have exactly the same
     * values for their title and url.
     */
    @Override
    public int hashCode() {
        // Google Guava provides great utilities for hashing
        return Objects.hashCode(title);
    }

    /**
     * Two Gifts are considered equal if they have exactly the same values for
     * their title and url.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Gift) {
            Gift other = (Gift) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(title, other.title)
                    && Objects.equal(imageurl, other.imageurl);
        } else {
            return false;
        }
    }

}