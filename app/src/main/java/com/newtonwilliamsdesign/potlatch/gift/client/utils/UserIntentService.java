package com.newtonwilliamsdesign.potlatch.gift.client.utils;

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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.newtonwilliamsdesign.potlatch.gift.client.TopGiftGiversActivity;
import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftServiceUser;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;

import java.util.ArrayList;

public class UserIntentService extends IntentService {

    public static final String PARAM_OUT_MSG = "omsg";
    Context mContext = this;

    public UserIntentService() {
        super("UserIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String user = prefStore.getUsername(mContext, "app"); // get current user to know name of user sharedprefs
        String accessToken = prefStore.getAccessToken(mContext, user); // get access token from user sharedprefs
        final GiftSvcApi svc = GiftSvc.init(getApplicationContext(), accessToken);

        Intent broadcastIntent = new Intent();

        ArrayList<GiftServiceUser> users = svc.getTopTenGiftGiversList();
        broadcastIntent.setAction(TopGiftGiversActivity.ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putParcelableArrayListExtra(PARAM_OUT_MSG, users);
        sendBroadcast(broadcastIntent);
    }
}
