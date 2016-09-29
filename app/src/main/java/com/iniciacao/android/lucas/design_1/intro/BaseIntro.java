package com.iniciacao.android.lucas.design_1.intro;

import android.content.Intent;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.iniciacao.android.lucas.design_1.MainActivity;

/**
 * Created by avluis on 08/08/2016.
 * Shared methods between classes
 */
public class BaseIntro extends AppIntro2 {

    void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
