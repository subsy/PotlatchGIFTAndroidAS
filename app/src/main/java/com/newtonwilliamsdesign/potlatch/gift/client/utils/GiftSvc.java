/* 
 **
 ** Original Copyright 2014, Jules White
 **
 ** 
 */

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

package com.newtonwilliamsdesign.potlatch.gift.client.utils;

import android.content.Context;
import android.content.Intent;

import com.newtonwilliamsdesign.potlatch.gift.client.LoginScreenActivity;
import com.newtonwilliamsdesign.potlatch.gift.client.oauth.SecuredRestBuilder;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

public class GiftSvc {

    public static final String CLIENT_ID = "gift-mobile";
    public static final String CLIENT_SECRET = "giftmobilesecret";
    //public static final String SERVER = "https://192.168.56.1:8443"; // for access from vBox
    public static final String SERVER = "https://192.168.1.166:8443";

    private static GiftSvcApi giftSvc_;

    public static synchronized GiftSvcApi getOrShowLogin(Context ctx) {
        if (giftSvc_ != null) {
            return giftSvc_;
        } else {
            Intent i = new Intent(ctx, LoginScreenActivity.class);
            ctx.startActivity(i);
            return null;
        }
    }

    public static synchronized GiftSvcApi init(Context ctx, String accessToken) {

        giftSvc_ = new SecuredRestBuilder()
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setAccessToken(accessToken)
                .setContext(ctx)
                .setClient(
                        new ApacheClient(new EasyHttpClient()))
                .setEndpoint(SERVER).setLogLevel(LogLevel.FULL).build()
                .create(GiftSvcApi.class);

        return giftSvc_;
    }

    public static synchronized GiftSvcApi init(Context ctx, String user, String pass) {

        giftSvc_ = new SecuredRestBuilder()
                .setLoginEndpoint(SERVER + GiftSvcApi.TOKEN_PATH)
                .setUsername(user)
                .setPassword(pass)
                .setClientId(CLIENT_ID)
                .setClientSecret(CLIENT_SECRET)
                .setContext(ctx)
                .setClient(
                        new ApacheClient(new EasyHttpClient()))
                .setEndpoint(SERVER).setLogLevel(LogLevel.FULL).build()
                .create(GiftSvcApi.class);

        return giftSvc_;
    }
}
