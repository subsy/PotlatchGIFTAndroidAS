package com.newtonwilliamsdesign.potlatch.gift.client;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.newtonwilliamsdesign.potlatch.gift.client.utils.EasyHttpClient;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SplashScreenActivity extends Activity {

    private static final String userurl = GiftSvc.SERVER + "/count/users";
    private static final String gifturl = GiftSvc.SERVER + "/count/gifts";
    String usercount = "";
    String giftcount = "";

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new GetStats().execute();
    }

    private class GetStats extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            EasyHttpClient client = new EasyHttpClient();
            HttpGet request = new HttpGet();
            try {
                request.setURI(new URI(userurl));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            HttpResponse response = null;
            try {
                response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try {
                    usercount = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                request.setURI(new URI(gifturl));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            response = null;
            try {
                response = client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            entity = response.getEntity();

            if (entity != null) {
                try {
                    giftcount = EntityUtils.toString(entity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(SplashScreenActivity.this, LoginScreenActivity.class);
                    i.putExtra("usercount", usercount);
                    i.putExtra("giftcount", giftcount);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }

    }

}