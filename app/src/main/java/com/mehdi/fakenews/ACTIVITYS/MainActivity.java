package com.mehdi.fakenews.ACTIVITYS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehdi.fakenews.FRAGMENTS.News;
import com.mehdi.fakenews.FRAGMENTS.Profile;
import com.mehdi.fakenews.R;
import com.mehdi.fakenews.databinding.HomeScreenBinding;

public class MainActivity extends AppCompatActivity implements Profile.clicksProfil, News.clicksNews{


    FirebaseDatabase database;
    DatabaseReference reference;


    HomeScreenBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.home_screen);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                new Fragment[] {new News(), new Profile()});
        binding.pager.setAdapter(pagerAdapter);
        binding.tabs.setupWithViewPager(binding.pager);

    }

    @Override
    public void goOut() {
        startActivity(new Intent(this, SplashScreen.class));
    }

    @Override
    public void commentPr(String id) {
        Intent i = new Intent(this, Comments.class);
        i.putExtra("id", id);
        startActivity(i);
    }

    @Override
    public void add() {
        startActivity(new Intent(this, ActivityPost.class));
    }

    @Override
    public void comment(String idTag) {
        Intent i = new Intent(this, Comments.class);
        i.putExtra("id", idTag);
        startActivity(i);
    }

    @Override
    public void detail(String idTag) {

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments;
        ScreenSlidePagerAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
            {
                title = "News";
            }
            else if (position == 1)
            {
                title = "Profile";
            }
            return title;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}
