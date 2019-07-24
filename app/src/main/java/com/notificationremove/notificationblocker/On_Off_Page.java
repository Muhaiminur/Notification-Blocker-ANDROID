package com.notificationremove.notificationblocker;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.notificationremove.notificationblocker.MODEL.BlockList;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * A simple {@link Fragment} subclass.
 */
public class On_Off_Page extends Fragment {


    public On_Off_Page() {
        // Required empty public constructor
    }


    //for database
    private Realm realm;
    BlockList blockList;
    View view;
    Button on, off;
    View customView;
    Switch all_block;
    private RecyclerView recyclerView;
    private App_List_Adapter mAdapter;
    LayoutInflater inflater1;
    TextView check_page;


    private AdView adView;
    private InterstitialAd interstitialAd;

    private final String TAG = MainActivity.class.getSimpleName();


    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout nativeadView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_on__off__page, container, false);
        try {
            /*RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();*/
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            on = view.findViewById(R.id.on);
            off = view.findViewById(R.id.off);
            check_page = view.findViewById(R.id.check_tutorial);


            on.setTag("STOP");
            blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
            if (blockList != null) {
                if (blockList.getStatus().equals("no") || blockList.getStatus().equals("yes")) {
                    off.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    off.setBackgroundResource(R.drawable.ic_left_off);
                    on.setTextColor(getResources().getColor(R.color.white));
                    on.setBackgroundResource(R.drawable.ic_right_on);
                    //on.setTag("RUNNING");
                }
            }
            on.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (on.getTag().equals("RUNNING")) {

                    } else {
                        off.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        off.setBackgroundResource(R.drawable.ic_left_off);
                        on.setTextColor(getResources().getColor(R.color.white));
                        on.setBackgroundResource(R.drawable.ic_right_on);
                        //on.setTag("RUNNING");

                        inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        customView = inflater1.inflate(R.layout.activity_main, null);
                        recyclerView = customView.findViewById(R.id.app_list_recycler);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        mAdapter = new App_List_Adapter(getActivity(), new ApkInfoExtractor(getActivity()).GetAllInstalledApkInfo());
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setFocusable(false);

                        show("Select", "", "", "on");
                        all_block = customView.findViewById(R.id.block_all_switch);
                    }
                }
            });

            off.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        off.setTextColor(getResources().getColor(R.color.white));
                        off.setBackgroundResource(R.drawable.ic_left_on);
                        on.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        on.setBackgroundResource(R.drawable.ic_right_off);
                        on.setTag("STOP");
                        Snackbar.make(view, "Ending Notification Block", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
                        realm.beginTransaction();
                        if (blockList == null) {
                            BlockList blockList_new = realm.createObject(BlockList.class);
                            blockList_new.setPackage_name("BLOCK_ALL");
                            blockList_new.setStatus("not_at_all");
                        } else {
                            blockList.setStatus("not_at_all");
                        }
                        realm.commitTransaction();
                    } catch (Exception e) {
                        Log.d("Error Line Number", Log.getStackTraceString(e));
                    }
                }
            });

            //register work
            String myString = "If App is not working, Click here.";
            int i1 = myString.indexOf("C");
            int i2 = myString.lastIndexOf(".");
            check_page.setMovementMethod(LinkMovementMethod.getInstance());
            check_page.setText(myString, TextView.BufferType.SPANNABLE);
            Spannable mySpannable = (Spannable) check_page.getText();
            ClickableSpan myClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(getContext(), Tutorial_Page.class));
                }
            };
            mySpannable.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            AudienceNetworkAds.initialize(getContext());
            loadNativeAd();
            bannerad();
            interstellar();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        }
        return view;
    }

    public void show(String title, String msg, String button, String con) {
        try {
            switch (con) {
                case "on":
                    MaterialStyledDialog.Builder dialog = new MaterialStyledDialog.Builder(getActivity());
                    dialog.setStyle(Style.HEADER_WITH_ICON)
                            //.setTitle(title)
                            //.setDescription("A loooooooooong looooooooooong really loooooooooong content. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar sem nibh, et efficitur massa mattis eget. Phasellus condimentum ligula.")
                            .setCustomView(customView)
                            .setIcon(R.drawable.ic_notification)
                            .setHeaderColor(R.color.colorPrimaryDark)
                            .withDarkerOverlay(true)
                            .withDialogAnimation(true)
                            .setCancelable(false)
                            .setScrollable(true)
                            .setPositiveText(R.string.button_on)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
                                    realm.beginTransaction();
                                    if (all_block.isChecked()) {
                                        Snackbar.make(view, "ALL Notification Blocked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                        if (blockList == null) {
                                            BlockList blockList_new = realm.createObject(BlockList.class);
                                            blockList_new.setPackage_name("BLOCK_ALL");
                                            blockList_new.setStatus("yes");
                                        } else {
                                            blockList.setStatus("yes");
                                        }
                                    } else {
                                        if (blockList == null) {
                                            BlockList blockList_new = realm.createObject(BlockList.class);
                                            blockList_new.setPackage_name("BLOCK_ALL");
                                            blockList_new.setStatus("no");
                                        } else {
                                            blockList.setStatus("no");
                                        }
                                    }
                                    realm.commitTransaction();
                                    NotificationManager nManager = ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE));
                                    nManager.cancelAll();
                                }
                            })
                            .setNegativeText("Dismiss")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Log.d("MaterialStyledDialogs", "Do something!");
                                }
                            })
                            .show();
                default:

            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    public void bannerad() {
        adView = new AdView(getActivity(), getResources().getString(R.string.homepage_banner), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = view.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                /*Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();*/
                Log.d("Banner", "Error: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });
        // Request an ad
        adView.loadAd();
    }

    private void interstellar() {
        interstitialAd = new InterstitialAd(getActivity(), getResources().getString(R.string.homepage_intersteller));
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                //interstitialAd.show();
                showAdWithDelay();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

    private void showAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if interstitialAd has been loaded successfully
                if (interstitialAd == null || !interstitialAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if (interstitialAd.isAdInvalidated()) {
                    return;
                }
                // Show the ad
                interstitialAd.show();
            }
        }, 10000); // Show the ad after 15 minutes
    }


    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(getActivity(), getResources().getString(R.string.homepage_native));

        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

    private void inflateAd(NativeAd nativeAd) {

        try {
            nativeAd.unregisterView();

            // Add the Ad view into the ad container.
            nativeAdLayout = view.findViewById(R.id.native_ad_container);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            nativeadView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
            nativeAdLayout.addView(nativeadView);

            // Add the AdOptionsView
            LinearLayout adChoicesContainer = nativeadView.findViewById(R.id.ad_choices_container);
            AdOptionsView adOptionsView = new AdOptionsView(getActivity(), nativeAd, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);

            // Create native UI using the ad metadata.
            AdIconView nativeAdIcon = nativeadView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = nativeadView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = nativeadView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = nativeadView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = nativeadView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = nativeadView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = nativeadView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            nativeAdBody.setText(nativeAd.getAdBodyText());
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                    nativeadView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews);
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

}
