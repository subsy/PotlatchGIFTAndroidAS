// Generated code from Butter Knife. Do not modify!
package com.newtonwilliamsdesign.potlatch.gift.client;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CreateGiftActivity$$ViewInjector {
  public static void inject(Finder finder, final com.newtonwilliamsdesign.potlatch.gift.client.CreateGiftActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361856, "field 'title_'");
    target.title_ = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361857, "field 'description_'");
    target.description_ = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361859, "field 'addFromGalleryButton_' and method 'launchGalleryIntent'");
    target.addFromGalleryButton_ = (android.widget.ImageButton) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.launchGalleryIntent();
        }
      });
    view = finder.findRequiredView(source, 2131361860, "field 'addFromCameraButton_' and method 'launchCameraIntent'");
    target.addFromCameraButton_ = (android.widget.ImageButton) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.launchCameraIntent();
        }
      });
    view = finder.findRequiredView(source, 2131361858, "field 'createGiftButton_' and method 'createGift'");
    target.createGiftButton_ = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.createGift();
        }
      });
    view = finder.findRequiredView(source, 2131361861, "field 'selectedImageView_'");
    target.selectedImageView_ = (it.gmariotti.cardslib.library.view.CardViewNative) view;
  }

  public static void reset(com.newtonwilliamsdesign.potlatch.gift.client.CreateGiftActivity target) {
    target.title_ = null;
    target.description_ = null;
    target.addFromGalleryButton_ = null;
    target.addFromCameraButton_ = null;
    target.createGiftButton_ = null;
    target.selectedImageView_ = null;
  }
}
