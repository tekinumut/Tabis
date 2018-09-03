package com.umonsoft.tabis.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.umonsoft.tabis.R;
import com.umonsoft.tabis.fragments.homepagetabs.Tab1DepartRecords;
import com.umonsoft.tabis.fragments.homepagetabs.Tab2MyRecords;
import com.umonsoft.tabis.receivers.AlarmReceiver;
import com.umonsoft.tabis.services.NotificationIntentService;
import com.umonsoft.tabis.settings.SettingsActivity;

public class Homepage extends AppCompatActivity {

    private SharedPreferences preferencesLogin;
    private SharedPreferences.Editor editorRememberMe,editorKarisikDegerler,editorLogin;
    private String userTypeValue;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager =findViewById(R.id.container);
        mViewPager.setAdapter(null);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(2);

        if(getIntent().getIntExtra(getString(R.string.opensecondtab),0)==1)
                mViewPager.setCurrentItem(1);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferenceVariables();
        userTypeValue =preferencesLogin.getString("type","0");

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager =findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
         mViewPager.setOffscreenPageLimit(2);

         if(userTypeValue.equals("3")){  //Eğer giren kullanıcı ise

            ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0).setVisibility(View.GONE);

            TabLayout.Tab tab = tabLayout.getTabAt(1);
             if (tab != null) {
                 tab.select();
             }
         }


    } //end of oncreate

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                    case 0:
                if(userTypeValue.equals("2")){
                    return new Tab1DepartRecords();
                        }
                case 1:
                    return new Tab2MyRecords();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            int count;
             switch (userTypeValue)
             {
                 case "2":
                     count =2;
                     return count;
                 case "3":
                     count=1;
                     return count;
                 default:
                     return 0;
             }
        }

    } //end of section

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        switch (id) {
            case R.id.menuSettings:
                startActivity(new Intent(Homepage.this, SettingsActivity.class));
                return true;
            case R.id.menuAbout:
                Intent intent = new Intent(Homepage.this, NotificationIntentService.class);
                startService(intent);
                break;
            case R.id.menuLogout:

                editorRememberMe.clear().apply();
                editorLogin.clear().apply();
                editorKarisikDegerler.clear().apply();

                new AlarmReceiver().cancelAlarm(Homepage.this);

                startActivity(new Intent(Homepage.this, Login.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void kayitekleonClick(View view) {
        startActivity(new Intent(Homepage.this, CreateRecords.class));
    }

    private void preferenceVariables()
    {
        preferencesLogin=getSharedPreferences(getString(R.string.loginvalues),Context.MODE_PRIVATE);
        editorLogin=preferencesLogin.edit();            editorLogin.apply();
        SharedPreferences preferencesRememberMe = getSharedPreferences(getString(R.string.remembermevalues), Context.MODE_PRIVATE);
        editorRememberMe= preferencesRememberMe.edit();  editorRememberMe.apply();
        SharedPreferences preferencesKarisikDegerler = getSharedPreferences(getString(R.string.karisikdegerlervalues), Context.MODE_PRIVATE);
        editorKarisikDegerler= preferencesKarisikDegerler.edit();      editorKarisikDegerler.apply();
        SharedPreferences preferencesNotification = getSharedPreferences(getString(R.string.pref_notification), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorNotification = preferencesNotification.edit();
        editorNotification.apply();
    }

}
