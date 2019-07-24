package com.notificationremove.notificationblocker;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.notificationremove.notificationblocker.MODEL.Notification_History;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 */
public class History_List extends Fragment {

    View view;
    private RecyclerView recyclerView;
    //private History_List_Adapter mAdapter;
    private History_List_Adapter2 mAdapter;
    List<Object> mRecyclerViewItems = new ArrayList<>();

    List<String> stringList = new ArrayList<String>();
    List<String> final_app_list = new ArrayList<String>();
    RealmResults<Notification_History> notification_histories;
    private Realm realm;

    public History_List() {
        // Required empty public constructor
    }


    private AdView adView;
    private InterstitialAd interstitialAd;
    private final String TAG = MainActivity.class.getSimpleName();
    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout nativeadView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //mRecyclerViewItems.addAll(new ApkInfoExtractor(getActivity().getApplication()).GetAllInstalledApkInfo());
            stringList.addAll(new ApkInfoExtractor(getActivity().getApplication()).GetAllInstalledApkInfo());
            try {
                RealmConfiguration config = new RealmConfiguration.Builder()
                        .name("notification.realm")
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                realm = Realm.getInstance(config);
                notification_histories = realm.where(Notification_History.class).findAll().sort("notification_count", Sort.DESCENDING);
                for (Notification_History n : notification_histories) {
                    final_app_list.add(n.getApkname());
                }
                for (Object s : stringList) {
                    if (!final_app_list.contains(s)) {
                        final_app_list.add((String) s);
                    }
                }
            } catch (Exception e) {
                Log.d("Error Line Number", Log.getStackTraceString(e));
                if (realm != null) {
                    realm.close();
                }
            } finally {

            }
        } catch (Exception e) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history__list, container, false);
        try {
            mRecyclerViewItems.addAll(final_app_list);
            recyclerView = view.findViewById(R.id.history_recycler);
            recyclerView.setFocusable(false);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //mAdapter= new History_List_Adapter(getActivity(), new ApkInfoExtractor(getActivity()).GetAllInstalledApkInfo());
            //recyclerView.setAdapter(mAdapter);

            mAdapter = new History_List_Adapter2(getActivity().getApplication(), mRecyclerViewItems);
            recyclerView.setAdapter(mAdapter);
            AudienceNetworkAds.initialize(getActivity());
            bannerad();
            interstellar();
            loadNativeAd();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        if (adView != null) {
            adView.destroy();
        }

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroyView();
    }/*

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }

        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }*/

    public void bannerad() {
        adView = new AdView(getActivity(), getResources().getString(R.string.history_banner), AdSize.BANNER_HEIGHT_50);

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
        interstitialAd = new InterstitialAd(getActivity(), getResources().getString(R.string.history_intersteller));
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
        nativeAd = new NativeAd(getActivity(), getResources().getString(R.string.history_native));

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
