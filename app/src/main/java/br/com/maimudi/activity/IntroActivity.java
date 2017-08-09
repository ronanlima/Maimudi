package br.com.maimudi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;

import br.com.maimudi.R;
import br.com.maimudi.fragment.SlideFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by brunolemgruber on 11/03/16.
 */
public class IntroActivity extends AppIntro {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {

        addSlide(SlideFragment.newInstance(R.layout.intro));
        addSlide(SlideFragment.newInstance(R.layout.intro2));
        addSlide(SlideFragment.newInstance(R.layout.intro3));
        addSlide(SlideFragment.newInstance(R.layout.intro4));
        addSlide(SlideFragment.newInstance(R.layout.intro5));

    }

    private void loadMainActivity(){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSkipPressed() {

        loadMainActivity();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {

        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
