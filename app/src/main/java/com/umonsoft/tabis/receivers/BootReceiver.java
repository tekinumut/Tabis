package com.umonsoft.tabis.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umonsoft.tabis.settings.SettingsNotification;

public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean switchPref = sharedPref.getBoolean
                (SettingsNotification.KEY_PREF_SWITCH_1, false);

        if(switchPref) {
            new AlarmReceiver().setAlarm(context);
        }

    }
}
