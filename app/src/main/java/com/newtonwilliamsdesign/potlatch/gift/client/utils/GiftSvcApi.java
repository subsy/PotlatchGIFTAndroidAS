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

import com.newtonwilliamsdesign.potlatch.gift.client.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftServiceUser;
import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftUserPreferences;

import java.util.ArrayList;
import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface GiftSvcApi {

    public static final String PASSWORD_PARAMETER = "password";

    public static final String USERNAME_PARAMETER = "username";

    public static final String TITLE_PARAMETER = "title";

    public static final String TOKEN_PATH = "/oauth/token";

    // The path where we expect the UserSvc to live
    public static final String USER_SVC_PATH = "/user";

    // The path where we expect the VideoSvc to live
    public static final String GIFT_SVC_PATH = "/gift";
    // The path for the Gift (image) data
    public static final String GIFT_IMG_PATH = GIFT_SVC_PATH + "/{id}/image";
    // The path for the Gift (image) thumbnail data
    public static final String GIFT_THUMB_PATH = GIFT_SVC_PATH + "/{id}/thumb";
    // The path to search videos by title
    public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";
    public static final String COUNT_SVC_PATH = "/count";

    @GET(GIFT_SVC_PATH)
    public Observable<Collection<Gift>> getGiftList();

    @GET(GIFT_SVC_PATH + "/chain")
    public Observable<Collection<Gift>> getGiftChainList();

    @GET(GIFT_SVC_PATH + "/chain" + "/{id}")
    public Observable<Collection<Gift>> getGiftChainChildren(@Path("id") long id);

    @GET(GIFT_SVC_PATH + "/top")
    public ArrayList<GiftServiceUser> getTopTenGiftGiversList();

    @GET(GIFT_SVC_PATH + "/{id}")
    public Observable<Gift> getGiftById(@Path("id") long id);

    @DELETE(GIFT_SVC_PATH + "/{id}")
    public Observable<Boolean> deleteGiftById(@Path("id") long id);

    @GET(GIFT_IMG_PATH)
    public Observable<String> getImageUrl(@Path("id") long id);

    @GET(GIFT_IMG_PATH)
    public Observable<String> getThumbUrl(@Path("id") long id);

    @POST(GIFT_SVC_PATH)
    public Gift addGift(@Body Gift v);

    @GET(USER_SVC_PATH + "/prefs")
    public Observable<GiftUserPreferences> getGiftServiceUserPreferences();

    @POST(USER_SVC_PATH + "/prefs")
    public Observable<Boolean> setGiftServiceUserPreferences(@Body GiftUserPreferences prefs);

    @POST(USER_SVC_PATH + "/add")
    public GiftServiceUser addUser(@Body GiftServiceUser u);

    @POST(USER_SVC_PATH)
    public GiftServiceUser createUser(@Body GiftServiceUser u);

    @GET(USER_SVC_PATH + "/me")
    public GiftServiceUser getGiftServiceUser();

    @POST(GIFT_SVC_PATH + "/{id}/touch")
    public Observable<Long> touchGift(@Path("id") long id);

    @POST(GIFT_SVC_PATH + "/{id}/untouch")
    public Observable<Long> untouchGift(@Path("id") long id);

    @POST(GIFT_SVC_PATH + "/{id}/flag")
    public Observable<Long> flagGift(@Path("id") long id);

    @POST(GIFT_SVC_PATH + "/{id}/unflag")
    public Observable<Long> unflagGift(@Path("id") long id);

    @GET(GIFT_TITLE_SEARCH_PATH)
    public Observable<Collection<Gift>> findByTitle(@Query(TITLE_PARAMETER) String title);

    @GET(GIFT_SVC_PATH + "/{id}/touched")
    public Observable<Collection<String>> getUsersTouchedByGift(@Path("id") long id);

    @GET(GIFT_SVC_PATH + "/{id}/flaggedBy")
    public Observable<Collection<String>> getUsersWhoFlaggedGift(@Path("id") long id);

    @GET((COUNT_SVC_PATH) + "/gifts")
    public long getTotalGiftCount();

    @GET((COUNT_SVC_PATH) + "/users")
    public long getTotalUserCount();

}