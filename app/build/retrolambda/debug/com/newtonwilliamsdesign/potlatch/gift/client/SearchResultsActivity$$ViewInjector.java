// Generated code from Butter Knife. Do not modify!
package com.newtonwilliamsdesign.potlatch.gift.client;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SearchResultsActivity$$ViewInjector {
  public static void inject(Finder finder, final com.newtonwilliamsdesign.potlatch.gift.client.SearchResultsActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361866, "field 'listView'");
    target.listView = (it.gmariotti.cardslib.library.view.CardListView) view;
  }

  public static void reset(com.newtonwilliamsdesign.potlatch.gift.client.SearchResultsActivity target) {
    target.listView = null;
  }
}
