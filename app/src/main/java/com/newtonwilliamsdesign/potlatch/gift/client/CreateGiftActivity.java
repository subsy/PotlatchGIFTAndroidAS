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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.newtonwilliamsdesign.potlatch.gift.client.domain.Gift;
import com.newtonwilliamsdesign.potlatch.gift.client.prefs.prefStore;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.DatabaseHandler;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvc;
import com.newtonwilliamsdesign.potlatch.gift.client.utils.GiftSvcApi;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import it.gmariotti.cardslib.library.cards.material.MaterialLargeImageCard;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class CreateGiftActivity extends ActionBarActivity {

    private static final String SERVER = "https://192.168.56.1:8443";
    private static final int GALLERY_PIC_REQUEST = 1;
    private static final int CAMERA_PIC_REQUEST = 2;
    private final ActionBarActivity current = this;
    @InjectView(R.id.giftTitleEditText)
    protected EditText title_;
    @InjectView(R.id.giftDescriptionEditText)
    protected EditText description_;
    @InjectView(R.id.addFromGalleryButton)
    protected ImageButton addFromGalleryButton_;
    @InjectView(R.id.addFromCameraButton)
    protected ImageButton addFromCameraButton_;
    @InjectView(R.id.createGiftButton)
    protected Button createGiftButton_;
    @InjectView(R.id.selectedImage)
    protected CardViewNative selectedImageView_;

    String imagePathString = "";
    String imageurl = "";
    String thumburl = "";
    Context mContext;
    Uri selectedImageUri;
    InputStream in;
    MaterialLargeImageCard card;
    PreviewThumbnail thumbnail;

    public static boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "giftimages");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("GIFT Image Capture", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gift);
        mContext = this;
        ButterKnife.inject(this);
        card = MaterialLargeImageCard.with(mContext).build();
        thumbnail = new PreviewThumbnail(mContext);

    }

    @OnClick(R.id.createGiftButton)
    public void createGift() {

        // local Editables
        Editable titleCreateable = title_.getText();
        Editable descCreateable = description_.getText();

        String title = String.valueOf(titleCreateable.toString());
        String desc = String.valueOf(descCreateable.toString());
        long parentid = getIntent().getLongExtra("parentid", 0);

        UploadTask uploader = new UploadTask(title, desc, parentid);
        uploader.execute(new String[]{"G>I>F>T"});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_gift, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(mContext, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_PIC_REQUEST:
                    try {
                        selectedImageUri = imageReturnedIntent.getData();
                        in = mContext.getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    imagePathString = selectedImageUri.getPath();
                    try {
                        in = new FileInputStream(imagePathString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            thumbnail.setExternalUsage(true);
            card.addCardThumbnail(thumbnail);
        }
        card.notifyDataSetChanged();
    }

    public class PreviewThumbnail extends CardThumbnail {

        public PreviewThumbnail(Context context) {
            super(context);
        }

        public PreviewThumbnail(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {

            ImageView image = (ImageView) viewImage;
            Bitmap b = null;
            try {
                b = decodeUri(selectedImageUri);
                image.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.addFromCameraButton)
    public void launchCameraIntent() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = getOutputMediaFile();
            selectedImageUri = Uri.fromFile(photoFile);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);

            if (isIntentAvailable(getApplicationContext(), imageCaptureIntent)) {
                startActivityForResult(imageCaptureIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    @OnClick(R.id.addFromGalleryButton)
    public void launchGalleryIntent() {
        Intent imageGalleryIntent = new Intent(Intent.ACTION_PICK);
        imageGalleryIntent.setType("image/*");
        selectedImageUri = null;

        if (isIntentAvailable(getApplicationContext(), imageGalleryIntent)) {
            startActivityForResult(Intent.createChooser(imageGalleryIntent, "Select Image"), GALLERY_PIC_REQUEST);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedImageView_.setCard(card);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedImageUri != null) {
            outState.putString("selectedImageUri", selectedImageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("selectedImageUri")) {
            selectedImageUri = Uri.parse(savedInstanceState.getString("selectedImageUri"));
        }
    }

    private class UploadTask extends AsyncTask<String, Void, String> {
        Cloudinary cloudinary = GiftApplication.getInstance(current).getCloudinary();
        String pubid = "";
        String title = "";
        String desc = "";
        long parentid = 0;

        public UploadTask(String title, String desc, long parentid) {
            super();
            this.title = title;
            this.desc = desc;
            this.parentid = parentid;
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            long currTime = System.currentTimeMillis();
            String user = prefStore.getUsername(mContext, "app"); // get current user to know name of user sharedprefs
            String accessToken = prefStore.getAccessToken(mContext, user); // get access token from user sharedprefs
            final GiftSvcApi svc = GiftSvc.init(getApplicationContext(), accessToken);

                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(
                                    mContext,
                                    "Sending Gift to server.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                    JSONObject cloudinaryResponse = cloudinary.uploader().upload(in, Cloudinary.emptyMap());
                    pubid = cloudinaryResponse.getString("public_id");
                    imageurl = cloudinary.url().format("jpg")
                            .secure(true)
                            .transformation(new Transformation().width(1024).height(768).crop("fill"))
                            .generate(pubid);
                    thumburl = cloudinary.url().format("jpg")
                            .secure(true)
                            .transformation(new Transformation().radius("max").width(168).height(168).crop("fill"))
                            .generate(pubid);

                    Gift newGift = new Gift(parentid, title, desc, imageurl, thumburl, 0, 0, currTime, currTime);

                    if(null != svc.addGift(newGift)) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(
                                        mContext,
                                        "Gift Created.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(
                                        mContext,
                                        "Unable to create Gift. Please try again later.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            long giftid = getIntent().getLongExtra("giftid", 1);
            Intent i = new Intent();
            i.putExtra("giftid", giftid);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private Bitmap decodeUri(Uri imageToDecode) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageToDecode), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 320;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(imageToDecode), null, o2);

    }
}

