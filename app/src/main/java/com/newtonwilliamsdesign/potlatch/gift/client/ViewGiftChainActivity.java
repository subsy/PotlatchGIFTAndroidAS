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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.newtonwilliamsdesign.potlatch.gift.client.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.DatabaseHandler;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvcApi;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.gmariotti.cardslib.library.cards.actions.BaseSupplementalAction;
import it.gmariotti.cardslib.library.cards.actions.IconSupplementalAction;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ViewGiftChainActivity extends ActionBarActivity {

    private static final float INITIAL_ITEMS_COUNT = 3.5F;
    @InjectView(R.id.gift_chain_card_view)
    CardViewNative cardView;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.carousel)
    LinearLayout mCarouselContainer;
    Context mContext;
    MaterialLargeImageCard maincard;
    private String user = "";
    private String accessToken = "";
    private GiftSvcApi svc;
    private Gift selectedGift;
    private long passedGiftid;
    private long newtouchcount;
    private DatabaseHandler db;

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gift_chain);
        mContext = this;
        ButterKnife.inject(this);

        passedGiftid = getIntent().getLongExtra("giftid", 0);

        user = prefStore.getUsername(mContext, "app"); // get current user to know name of user sharedprefs
        accessToken = prefStore.getAccessToken(mContext, user); // get access token from user sharedprefs
        svc = GiftSvc.init(getApplicationContext(), accessToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = new DatabaseHandler(this);
        passedGiftid = getIntent().getLongExtra("giftid", 0);
        buildView(passedGiftid);
    }

    private void buildView(long giftid) {
        // Set supplemental actions as icon
        ArrayList<BaseSupplementalAction> actions = new ArrayList<BaseSupplementalAction>();

        svc.getGiftById(giftid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gift -> {

                    selectedGift = gift;

                    db.saveCount(giftid, selectedGift.getTouches());

                    if (selectedGift.getParentid() != 0) {
                        fab.hide();
                    }

                    IconSupplementalAction buttonTouch = new IconSupplementalAction(mContext, R.id.touchButton);
                    buttonTouch.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {

                        @Override
                        public void onClick(Card card, View view) {

                            svc.getUsersTouchedByGift(giftid)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(users -> {
                                        if (users.contains(user)) {
                                            svc.untouchGift(giftid)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(touchcount -> {
                                                        newtouchcount = touchcount;
                                                        Toast.makeText(
                                                                mContext,
                                                                "Gift Untouched.",
                                                                Toast.LENGTH_SHORT).show();
                                                        updateTouchesOnCard(newtouchcount);
                                                    });
                                        } else {
                                            svc.touchGift(giftid)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(touchcount -> {
                                                        newtouchcount = touchcount;
                                                        Toast.makeText(
                                                                mContext,
                                                                "Gift Touched.",
                                                                Toast.LENGTH_SHORT).show();
                                                        updateTouchesOnCard(newtouchcount);

                                                    });
                                        }

                                    });

                        }
                    });
                    actions.add(buttonTouch);

                    IconSupplementalAction buttonFlag = new IconSupplementalAction(mContext, R.id.flagButton);
                    buttonFlag.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                        @Override
                        public void onClick(Card card, View view) {
                            svc.getUsersWhoFlaggedGift(giftid)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(users -> {
                                        if (users.contains(user)) {
                                            svc.unflagGift(giftid)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(flagged -> {
                                                        Toast.makeText(
                                                                mContext,
                                                                "Gift Unflagged.",
                                                                Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            svc.flagGift(giftid)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(flagged -> {
                                                        Toast.makeText(
                                                                mContext,
                                                                "Gift Flagged.",
                                                                Toast.LENGTH_SHORT).show();
                                                    });
                                        }

                                    });
                        }
                    });
                    actions.add(buttonFlag);

                    IconSupplementalAction buttonDelete = new IconSupplementalAction(mContext, R.id.deleteButton);
                    buttonDelete.setOnActionClickListener(new BaseSupplementalAction.OnActionClickListener() {
                        @Override
                        public void onClick(Card card, View view) {
                            svc.deleteGiftById(giftid)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        if (result == true) {
                                            Toast.makeText(
                                                    mContext,
                                                    "Gift Deleted.",
                                                    Toast.LENGTH_SHORT).show();
                                            db.deleteCount(giftid);
                                            Intent i = new Intent();
                                            i.putExtra("giftid", selectedGift.getParentid());
                                            setResult(RESULT_OK, i);
                                            finish();
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(
                                                            mContext,
                                                            "You can only delete Gifts that you created.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    actions.add(buttonDelete);


                    String createdon = getDate(selectedGift.getCreatedon(), "dd/MM/yyyy hh:mm");
                    int touches = db.getCount(giftid);
                    String subtitle = "Created: " + createdon + "\nTouches: " + touches;

                    maincard = MaterialLargeImageCard.with(mContext)
                            .setTextOverImage(selectedGift.getTitle())
                            .setTitle(selectedGift.getDescription())
                            .setSubTitle(subtitle)
                            .useDrawableUrl(selectedGift.getImageurl())
                            .setupSupplementalActions(R.layout.supplemental_actions, actions)
                            .build();


                    cardView.setCard(maincard);

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getBaseContext(), CreateGiftActivity.class);
                            i.putExtra("parentid", giftid);
                            i.putExtra("giftid", giftid);
                            startActivityForResult(i, GiftApplication.ADD_GIFT);
                        }
                    });
                });


        // Compute the width of a carousel item based on the screen width and number of initial items.
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);

        mCarouselContainer.removeAllViews();
        // Populate the carousel with items
        svc.getGiftChainChildren(giftid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gifts -> {
                    ImageView imageItem, chainLinkItem;
                    for (Gift g : gifts) {

                        imageItem = new ImageView(this);
                        chainLinkItem = new ImageView(this);

                        imageItem.setBackgroundResource(R.drawable.shadow);

                        new ImageLoadTask(g.getThumburl(), imageItem).execute();

                        imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));
                        imageItem.setCropToPadding(true);

                        imageItem.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent i = new Intent(mContext, ViewGiftChainActivity.class);
                                i.putExtra("giftid", g.getId());
                                startActivityForResult(i, GiftApplication.VIEW_GIFT);
                            }
                        });

                        chainLinkItem.setImageDrawable(getResources().getDrawable(R.drawable.chain_link));
                        chainLinkItem.setLayoutParams(new LinearLayout.LayoutParams(50, imageWidth));

                        mCarouselContainer.addView(imageItem);
                        mCarouselContainer.addView(chainLinkItem);
                    }
                });
    }

    private void updateTouchesOnCard(long touchescount) {
        String createdon = getDate(selectedGift.getCreatedon(), "dd/MM/yyyy hh:mm");
        String subtitle = "Created: " + createdon + "\nTouches: " + touchescount;
        maincard.setSubTitle(subtitle);
        maincard.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            long giftid = data.getLongExtra("giftid", 0);
            Intent refresh = new Intent(this, ViewGiftChainActivity.class);
            refresh.putExtra("giftid", giftid);
            startActivity(refresh);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_gift_chain_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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
            case R.id.action_stats:
                Intent topGiversIntent = new Intent(mContext, TopGiftGiversActivity.class);
                startActivity(topGiversIntent);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}