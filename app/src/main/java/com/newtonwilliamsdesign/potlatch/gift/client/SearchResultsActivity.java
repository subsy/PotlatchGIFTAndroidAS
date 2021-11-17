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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.newtonwilliamsdesign.potlatch.gift.client.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvcApi;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchResultsActivity extends ActionBarActivity {

    @InjectView(R.id.gift_chain_list_card_view)
    CardListView listView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        ButterKnife.inject(this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            String user = prefStore.getUsername(mContext, "app"); // get current user to know name of user sharedprefs
            String accessToken = prefStore.getAccessToken(mContext, user); // get access token from user sharedprefs
            final GiftSvcApi svc = GiftSvc.init(getApplicationContext(), accessToken);

            svc.findByTitle(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gifts -> {
                        ArrayList<Card> cards = new ArrayList<Card>();
                        for (Gift g : gifts) {

                            // Create a Card
                            MaterialLargeImageCard card = MaterialLargeImageCard.with(getBaseContext())
                                    .setTextOverImage(g.getTitle())
                                    .setTitle(g.getTitle())
                                    .setSubTitle(g.getDescription())
                                    .useDrawableUrl(g.getImageurl())
                                            //.setupSupplementalActions(R.layout.carddemo_native_material_supplemental_actions_large, actions)
                                    .build();

                            card.setOnClickListener(new Card.OnCardClickListener() {
                                @Override
                                public void onClick(Card card, View view) {
                                    Intent i = new Intent(mContext, ViewGiftChainActivity.class);
                                    i.putExtra("giftid", g.getId());
                                    startActivityForResult(i, GiftApplication.VIEW_GIFT);
                                }
                            });

                            cards.add(card);
                        }

                        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getBaseContext(), cards);

                        if (listView != null) {
                            listView.setAdapter(mCardArrayAdapter);

                        }
                    });
        }
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
}
