package com.iniciacao.android.lucas.design_1.intro;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.iniciacao.android.lucas.design_1.R;

public class IntroActivity extends BaseIntro{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));
        addSlide(SampleSlide.newInstance(R.layout.intro5));


//        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
//
//        askForPermissions(new String[]{Manifest.permission.SEND_SMS}, 4);



    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        loadMainActivity();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        loadMainActivity();

    }

    public void getStarted(View v) {
        loadMainActivity();
    }


}

