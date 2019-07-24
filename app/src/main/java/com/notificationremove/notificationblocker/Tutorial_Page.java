package com.notificationremove.notificationblocker;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class Tutorial_Page extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tutorial__page);
        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            SliderPage sliderPage1 = new SliderPage();
            sliderPage1.setTitle("Welcome!");
            sliderPage1.setDescription("If Notification Blocker is not working then you have to ON/OFF access");
            sliderPage1.setImageDrawable(R.drawable.tutorial1);
            sliderPage1.setBgColor(getResources().getColor(R.color.colorPrimary));
            addSlide(AppIntroFragment.newInstance(sliderPage1));
            //addSlide(new ONE());

            SliderPage sliderPage2 = new SliderPage();
            sliderPage2.setTitle("Unselect/Select ");
            sliderPage2.setDescription("Select and Unselect Access from menu like this");
            sliderPage2.setImageDrawable(R.drawable.tutorial2);
            sliderPage2.setBgColor(getResources().getColor(R.color.colorPrimary));
            addSlide(AppIntroFragment.newInstance(sliderPage2));
            //addSlide(new TWO());
            showSkipButton(false);
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        try {
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            finish();
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }
}