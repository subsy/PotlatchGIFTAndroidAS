package com.newtonwilliamsdesign.potlatch.gift.client.domain;

/**
 * ********************************************************************************
 * **********************************************************************************
 * **********************************************************************************
 * G I F T
 * A Multi-user Web Application and Android Client Application
 * for sharing of image gifts.
 * <p>
 * Copyright (C) 2014 Newton Williams Design.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * **********************************************************************************
 * **********************************************************************************
 * *********************************************************************************
 */

public class GiftUserPreferences {
    private long id;

    private GiftServiceUser user;
    private boolean displayFlagged;
    private int updateFreq;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public GiftServiceUser getUser() {
        return user;
    }

    public void setUser(GiftServiceUser user) {
        this.user = user;
    }

    public boolean isDisplayFlagged() {
        return displayFlagged;
    }

    public void setDisplayFlagged(boolean displayFlagged) {
        this.displayFlagged = displayFlagged;
    }

    public int getUpdateFreq() {
        return updateFreq;
    }

    public void setUpdateFreq(int updateFreq) {
        this.updateFreq = updateFreq;
    }
}
