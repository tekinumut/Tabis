package com.umonsoft.tabis.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.umonsoft.tabis.R;
import com.umonsoft.tabis.receivers.AlarmReceiver;


public class SettingsNotification extends PreferenceFragmentCompat {

    private Context mContext;
    public static final String
            KEY_PREF_SWITCH_1 = "pref_switch_open_notification";
    public static final String
            KEY_PREF_LIST_1   = "list_pref_check_notification";
    private AlarmReceiver alarmReceiver;

    public SettingsNotification() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.pref_notification,rootKey);

        alarmReceiver=new AlarmReceiver();

        final SwitchPreferenceCompat switch1 = (SwitchPreferenceCompat)findPreference(KEY_PREF_SWITCH_1);

        final ListPreference list1 = (ListPreference) findPreference("list_pref_check_notification");

        list1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                list1.setValue(o.toString());
                alarmReceiver.setAlarm(mContext);

                return true;
            }
        });
        SharedPreferences  preferencesRememberMe=mContext.getSharedPreferences(getString(R.string.remembermevalues), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorRememberMe=preferencesRememberMe.edit();  editorRememberMe.apply();

        final boolean hatirla =preferencesRememberMe.getBoolean(getString(R.string.rememberme_hatirla),false);

        if(!hatirla)
            switch1.setChecked(false);

        if(switch1.isChecked())

        {
            switch1.setTitle(getString(R.string.bildirim_kapat));
            list1.setEnabled(true);
        }
               //Seçildiyse
        else
        {
            switch1.setTitle(getString(R.string.bildirim_ac));
            list1.setEnabled(false);
        }

        switch1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @SuppressLint("BatteryLife")
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(!(switch1.isChecked()))   //seçildiyse
                {
                   if(hatirla) {
                       switch1.setTitle(getString(R.string.bildirim_kapat));
                       list1.setEnabled(true);

                       alarmReceiver.setAlarm(mContext);

                       if (Build.VERSION.SDK_INT >= 23) {

                           DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                               @SuppressLint("InlinedApi")
                               @Override
                               public void onClick(DialogInterface dialog, int which) {

                                   switch (which) {
                                       case DialogInterface.BUTTON_POSITIVE:
                                           //Evete tıklanınca
                                               mContext.startActivity(new Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                                           break;

                                       case DialogInterface.BUTTON_NEGATIVE:
                                           //Hayıra tıklanınca
                                           dialog.dismiss();
                                           break;
                                   }
                               }
                           };

                           AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                           builder.setMessage("Bildirimlerinizin sorunsuz çalışabilmesi için açılacak olan pencerede TABIS uygulamasını seçip batarya optimizasyonunu kapatmanız gerekmektedir.")
                                   .setPositiveButton(getString(R.string.dialog_evet), dialogClickListener)
                                   .setNegativeButton(getString(R.string.dialog_hayir), dialogClickListener);
                           PowerManager pm =(PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                           if (pm != null && !pm.isIgnoringBatteryOptimizations(getContext().getPackageName()))
                               builder.show();

                       }
                   }//hatirla end
                    else
                   {
                       DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                           @SuppressLint("InlinedApi")
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               switch (which) {
                                   case DialogInterface.BUTTON_POSITIVE:
                                      dialog.dismiss();
                                      switch1.setChecked(false);
                                       break;
                               }
                           }
                       };

                       AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                       builder.setMessage("Bildirimleri açabilmeniz için giriş yaparken \"Beni Hatırla\" bölümünü seçmeniz gerekmektedir. ")
                               .setPositiveButton(getString(R.string.dialog_tamam), dialogClickListener).show();
                   }
                }
                else
                {
                    switch1.setTitle(getString(R.string.bildirim_ac));
                    list1.setEnabled(false);
                    alarmReceiver.cancelAlarm(mContext);

                }
                return true;
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }
}
