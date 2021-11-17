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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftUserPreferences;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvcApi;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.UpdateManagerBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;
import it.gmariotti.cardslib.library.view.CardViewNative;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginScreenActivity extends Activity {

    @InjectView(R.id.userName)
    protected EditText userName_;
    @InjectView(R.id.password)
    protected EditText password_;
    @InjectView(R.id.statscard)
    CardViewNative stats_;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mContext = this;
        ButterKnife.inject(this);

        StatsCardWithList statscard = new StatsCardWithList(mContext);
        statscard.init();
        stats_.setCard(statscard);
    }

    @OnClick(R.id.loginButton)
    public void login() {
        String user = userName_.getText().toString();
        String pass = password_.getText().toString();

        final GiftSvcApi svc = GiftSvc.init(getApplicationContext(), user, pass);

        svc.getGiftServiceUserPreferences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GiftUserPreferences>() {
                    @Override
                    public void onCompleted() {
                        // OAuth 2.0 grant was successful and we
                        // can talk to the server, open up the gift listing
                        String interval = prefStore.getUpdateFreq(mContext, "app"); // get update freq from user sharedprefs
                        initDownloadService(interval);
                        startActivity(new Intent(
                                LoginScreenActivity.this,
                                ViewGiftChainCardsListActivity.class));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);

                        Toast.makeText(
                                LoginScreenActivity.this,
                                "Login failed, check your Internet connection and credentials.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(GiftUserPreferences prefs) {
                        prefStore.setUsername(getApplicationContext(), "app", user);
                        prefStore.setUpdateFreq(mContext, "app", String.valueOf(prefs.getUpdateFreq()));
                        prefStore.setDisplayFlagged(mContext, "app", prefs.isDisplayFlagged());
                    }
                });
    }


    public void initDownloadService(String interval) {
        UpdateManagerBroadcastReceiver br = new UpdateManagerBroadcastReceiver();
        long updateFreq = Long.parseLong(interval) * 1000 * 60;
        br.setAlarm(getApplicationContext(), updateFreq);
        Log.d("ALARM", "initDownloadService calling setAlarm with updateFreq = " + updateFreq + "\n");
    }

    public class StatsCardWithList extends CardWithList {

        public StatsCardWithList(Context context) {
            super(context);
        }

        @Override
        protected CardHeader initCardHeader() {

            //Add Header
            CardHeader header = new CardHeader(getContext(), R.layout.stats_with_list_native_inner_header) {

                @Override
                public void setupInnerViewElements(ViewGroup parent, View view) {
                    super.setupInnerViewElements(parent, view);
                    TextView subTitle = (TextView) view.findViewById(R.id.stats_main_inner_lastupdate);
                    if (subTitle != null) {
                        subTitle.setText("Updated: Just Now");  //Should use strings.xml
                    }
                }
            };
            header.setTitle("Latest Statistics"); //should use strings.xml
            return header;
        }

        @Override
        protected void initCard() {

            //Set the whole card as swipeable
            setSwipeable(true);
            setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                    Toast.makeText(getContext(), "Swipe on " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
                }
            });

        }


        @Override
        protected List<ListObject> initChildren() {

            Intent i = getIntent();
            String usercount = i.getStringExtra("usercount");
            String giftcount = i.getStringExtra("giftcount");

            //Init the list
            List<ListObject> mObjects = new ArrayList<ListObject>();

            //Add an object to the list
            StatsObject s1 = new StatsObject(this);
            s1.statName = "Total Gifts";
            s1.value = giftcount;
            mObjects.add(s1);

            StatsObject s2 = new StatsObject(this);
            s2.statName = "Total Users";
            s2.value = usercount;
            mObjects.add(s2);

            return mObjects;
        }

        @Override
        public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

            //Setup the ui elements inside the item
            TextView textViewStatName = (TextView) convertView.findViewById(R.id.textViewName);
            TextView textViewValue = (TextView) convertView.findViewById(R.id.textViewValue);

            //Retrieve the values from the object
            StatsObject stockObject = (StatsObject) object;
            textViewStatName.setText(stockObject.statName);
            textViewValue.setText("" + stockObject.value);

            return convertView;
        }

        @Override
        public int getChildLayoutId() {
            return R.layout.stats_with_list_inner_main;
        }


        // -------------------------------------------------------------
        // Weather Object
        // -------------------------------------------------------------

        public class StatsObject extends DefaultListObject {

            public String statName;
            public String value;


            public StatsObject(Card parentCard) {
                super(parentCard);
                init();
            }

            private void init() {
                //OnClick Listener
                setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                        Toast.makeText(getContext(), "Click on " + getObjectId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public String getObjectId() {
                return statName;
            }
        }

    }

}
