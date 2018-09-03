package com.umonsoft.tabis.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.umonsoft.tabis.services.NotificationIntentService;
import com.umonsoft.tabis.settings.SettingsNotification;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }


    public void setAlarm(Context context)

    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getService(context, 100, new Intent(context,NotificationIntentService.class) , PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            final int checknotificationvalue=Integer.parseInt(sharedPref.getString(SettingsNotification.KEY_PREF_LIST_1,"9000000"));

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 2000, checknotificationvalue, pendingIntent);

        }

    }


    public void cancelAlarm(Context context)

    {

        Intent intent=new Intent(context,NotificationIntentService.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        context.stopService(intent);
    }
}
