// Generated code from Butter Knife. Do not modify!
package com.newtonwilliamsdesign.potlatch.gift.client;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class LoginScreenActivity$$ViewInjector {
  public static void inject(Finder finder, final com.newtonwilliamsdesign.potlatch.gift.client.LoginScreenActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361863, "field 'userName_'");
    target.userName_ = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361864, "field 'password_'");
    target.password_ = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361862, "field 'stats_'");
    target.stats_ = (it.gmariotti.cardslib.library.view.CardViewNative) view;
    view = finder.findRequiredView(source, 2131361865, "method 'login'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.login();
        }
      });
  }

  public static void reset(com.newtonwilliamsdesign.potlatch.gift.client.LoginScreenActivity target) {
    target.userName_ = null;
    target.password_ = null;
    target.stats_ = null;
  }
}
