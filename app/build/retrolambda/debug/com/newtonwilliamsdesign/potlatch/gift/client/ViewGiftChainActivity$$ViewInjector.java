// Generated code from Butter Knife. Do not modify!
package com.newtonwilliamsdesign.potlatch.gift.client;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class ViewGiftChainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.newtonwilliamsdesign.potlatch.gift.client.ViewGiftChainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361870, "field 'cardView'");
    target.cardView = (it.gmariotti.cardslib.library.view.CardViewNative) view;
    view = finder.findRequiredView(source, 2131361871, "field 'fab'");
    target.fab = (com.melnykov.fab.FloatingActionButton) view;
    view = finder.findRequiredView(source, 2131361908, "field 'mCarouselContainer'");
    target.mCarouselContainer = (android.widget.LinearLayout) view;
  }

  public static void reset(com.newtonwilliamsdesign.potlatch.gift.client.ViewGiftChainActivity target) {
    target.cardView = null;
    target.fab = null;
    target.mCarouselContainer = null;
  }
}
