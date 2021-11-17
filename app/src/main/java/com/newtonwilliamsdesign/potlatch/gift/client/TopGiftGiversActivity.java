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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftServiceUser;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.UserIntentService;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

public class TopGiftGiversActivity extends ActionBarActivity {

    @InjectView(R.id.top_gift_givers_list)
    CardListView listView;

    Context mContext;
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_gift_givers);
        mContext = this;
        ButterKnife.inject(this);

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        Intent msgIntent = new Intent(this, UserIntentService.class);
        startService(msgIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_gift_givers, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_giftlist:
                Intent giftListIntent = new Intent(mContext, ViewGiftChainCardsListActivity.class);
                startActivity(giftListIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {

            ArrayList<GiftServiceUser> users = intent.getParcelableArrayListExtra(UserIntentService.PARAM_OUT_MSG);

            ArrayList<Card> cards = new ArrayList<Card>();

            for (GiftServiceUser u : users) {
                Log.d("GIFTINFO", u.getUsername() + "\n");

                Card card = new Card(getBaseContext());
                CardHeader header = new CardHeader(getBaseContext());
                header.setTitle(u.getUsername() + " (Touched: " + u.getTouchedcount() + ")");
                CardThumbnail thumb = new CardThumbnail(getBaseContext());

                //Set resource
                thumb.setDrawableResource(R.drawable.user_icon);

                card.addCardHeader(header);
                card.setTitle(u.getName());
                card.addCardThumbnail(thumb);


                cards.add(card);
            }
            CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getBaseContext(), cards);

            if (listView != null) {
                listView.setAdapter(mCardArrayAdapter);
            }
        }
    }

}
