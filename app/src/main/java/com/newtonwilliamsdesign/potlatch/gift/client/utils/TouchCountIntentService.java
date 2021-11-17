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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.newtonwilliamsdesign.potlatch.gift.client.R;
import com.newtonwilliamsdesign.potlatch.gift.client.ViewGiftChainCardsListActivity;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TouchCountIntentService extends IntentService {

    Context mContext = this;
    private DatabaseHandler db = new DatabaseHandler(mContext);
    private int numCounts = 0;
    private int totalCount = 0;

    public TouchCountIntentService() {
        super("TouchCountIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String user = prefStore.getUsername(mContext, "app"); // get current user to know name of user sharedprefs
        String accessToken = prefStore.getAccessToken(mContext, user); // get access token from user sharedprefs
        final GiftSvcApi svc = GiftSvc.init(getApplicationContext(), accessToken);

        Bundle counts = db.getAllCounts();
        numCounts = 0;
        totalCount = 0;

        for (String key : counts.keySet()) {
            totalCount++;
            long id = Long.parseLong(key);
            Integer oldCount = Integer.parseInt((counts.get(key)).toString());
            svc.getGiftById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gift -> {
                        long newCount = gift.getTouches();
                        if (newCount != oldCount) {
                            db.saveCount(id, newCount);
                            numCounts++;
                        }
                    });
        }

        this.sendNotification(this, "Updated: " + numCounts + " of " + totalCount + " locally cached Touch Counts");
    }

    private void sendNotification(Context context, String msg) {

        Intent notificationIntent = new Intent(context, ViewGiftChainCardsListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationManager notificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Refresh")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Touch Count Auto-Update")
                .setContentText(msg).build();
        notificationMgr.notify(0, notification);
    }
}