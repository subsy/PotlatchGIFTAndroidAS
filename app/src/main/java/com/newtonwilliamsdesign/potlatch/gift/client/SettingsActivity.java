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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.newtonwilliamsdesign.potlatch.gift.client.domain.GiftUserPreferences;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvcApi;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.UpdateManagerBroadcastReceiver;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.Integer.parseInt;

public class SettingsActivity extends ActionBarActivity {

    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;
        ButterKnife.inject(this);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MainSettingsFragment())
                .commit();

        //PreferenceManager.setDefaultValues(SettingsActivity.this, R.xml.preferences, false);

    }

    public static class MainSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.getPreferenceManager().setSharedPreferencesName("app");
            this.getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);

            addPreferencesFromResource(R.xml.preferences);

            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initSummary(getPreferenceScreen().getPreference(i));
            }
        }

        private void initSummary(Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    initSummary(pCat.getPreference(i));
                }
            } else {
                updatePrefSummary(p);
            }
        }

        private void updatePrefSummary(Preference p) {
            if (p instanceof ListPreference) {
                ListPreference listPref = (ListPreference) p;
                p.setSummary(listPref.getEntry());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String user = prefStore.getUsername(getActivity(), "app"); // get current user to know name of user sharedprefs
            String accessToken = prefStore.getAccessToken(getActivity(), user); // get access token from user sharedprefs
            String interval = prefStore.getUpdateFreq(getActivity(), "app"); // get update freq from user sharedprefs
            final GiftSvcApi svc = GiftSvc.init(getActivity().getApplicationContext(), accessToken);

            UpdateManagerBroadcastReceiver br = new UpdateManagerBroadcastReceiver();

            GiftUserPreferences prefs = new GiftUserPreferences();

            if (key.equals("displayFlagged")) {
                prefs.setDisplayFlagged(sharedPreferences.getBoolean(key, false));
            }
            if (key.equals("updateFreq")) {
                prefs.setUpdateFreq(parseInt(sharedPreferences.getString(key, "-1")));
                br.cancelAlarm(getActivity().getApplicationContext());
                long updateFreq = Long.parseLong(interval) * 1000 * 60;
                br.setAlarm(getActivity().getApplicationContext(), updateFreq);
            }

            updatePrefSummary(findPreference(key));

            svc.setGiftServiceUserPreferences(prefs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        Toast.makeText(
                                getActivity(),
                                "Server Preferences Updated.",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
