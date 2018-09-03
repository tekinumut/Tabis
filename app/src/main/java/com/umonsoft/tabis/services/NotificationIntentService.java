package com.umonsoft.tabis.services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.umonsoft.tabis.Interfaces.VolleyGet1Parameter;
import com.umonsoft.tabis.Interfaces.VolleyGet2Parameter;
import com.umonsoft.tabis.Interfaces.VolleyGetRecordHistory;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.activities.Homepage;
import com.umonsoft.tabis.phpvalues.PhpValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationIntentService extends IntentService {
    private SharedPreferences preferencesNotification;
    private SharedPreferences.Editor editorNotification;
    private PhpValues phpValues;
    private int user_id;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        Log.e("umut","ONHANDLEINTENT");

        preferencesNotification=getSharedPreferences(getString(R.string.pref_notification), Context.MODE_PRIVATE);
        SharedPreferences preferencesLogin = getSharedPreferences(getString(R.string.loginvalues), Context.MODE_PRIVATE);
        editorNotification=preferencesNotification.edit(); editorNotification.apply();
        user_id = preferencesLogin.getInt("user_id",0);

        phpValues = new PhpValues();

        String sqlcodeAddingType = "Select addingtype from recordhistory where nextdepart IN (SELECT department_id from userdeparts where user_id ="+user_id+")" +
                " ORDER BY changingdate DESC LIMIT 1";

        phpValues.get1Parameter(NotificationIntentService.this, sqlcodeAddingType, new VolleyGet1Parameter() {
            @Override
            public void onSuccess(String response) {

                int addingtype = Integer.parseInt(response);


                String departcount = "Select count(id),MAX(id) from recordhistory where nextdepart IN (SELECT department_id from userdeparts where user_id =" +
                        " " + user_id + ")  and NOT user_id =  " + user_id + "";

                String statecount = " Select count(id),MAX(id) from statechangehistory where " +
                        "record_id IN(Select id from records where department IN (SELECT department_id from userdeparts " +
                        "where user_id = " + user_id + ") ) and NOT user_id = " + user_id + " and prevstate <> nextstate";


                sendPhpNotify(statecount, getString(R.string.statehistorycountid),getString(R.string.statehistorymaxid),0);


                sendPhpNotify(departcount, getString(R.string.departhistorycountid),getString(R.string.departhistorymaxid),addingtype);
            }
        });


    }

    private void getRecordHistory(final VolleyGetRecordHistory callback)

    {
        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, getString(R.string.php_getrecordhistory), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray values =new JSONArray(response);
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject object=values.getJSONObject(i);

                        //int id =object.getInt("id");
                        String user_name=object.getString("user_name");
                        int record_id=object.getInt("record_id");
                        String prevdepart=object.getString("prevdepart");
                        String nextdepart=object.getString("nextdepart");
                        String description=object.getString("description");
                        int addingtype=object.getInt("addingtype");

                        if(i==values.length()-1)
                        {
                            if(callback!=null)
                                callback.onSuccess(user_name,record_id,prevdepart,nextdepart,description,addingtype);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            protected Map<String, String> getParams(){

                HashMap<String,String> params =new HashMap<>();
                String departcount = "SELECT id,(Select concat(name, ' ', lastname) from users where id =user_id) as user_name ,record_id,(Select departments.name from departments where id =prevdepart) as prevdepart,(Select departments.name from departments where id =nextdepart) as nextdepart,description,addingtype from recordhistory where nextdepart IN (SELECT department_id from userdeparts where user_id ="+user_id+")  and NOT user_id =  "+user_id+"";

                params.put(getString(R.string.sqlcode),departcount);

                return params;
            }
        };
        Volley.newRequestQueue(NotificationIntentService.this).add(stringRequest);
    }

    private void sendPhpNotify(String sqlcode, final String prefInside,final String pref2Inside, final int addingtype)

    {
        phpValues.get2Parameter(NotificationIntentService.this, sqlcode, new VolleyGet2Parameter() {
            @Override
            public void onSuccess(final String parameter1,final String parameter2) {

                Log.e("degerler","parametreler :    "+parameter1+"   "+parameter2);

                int parameter1Int =Integer.parseInt(parameter1);
                int parameter2Int =Integer.parseInt(parameter2);


                    if(preferencesNotification.getInt(prefInside,-1)==-1)       //eğer kayıt sayısı null ise, program telefona ilk eklendiğinde
                    editorNotification.putInt(prefInside,parameter1Int).apply();
                    if(preferencesNotification.getInt(pref2Inside,-1)==-1)
                    editorNotification.putInt(pref2Inside,parameter2Int).apply();



                if (parameter1Int > preferencesNotification.getInt(prefInside,-1) && (parameter2Int > preferencesNotification.getInt(pref2Inside,-1)))
                    //eğer kişinin departmanına yeni kayıt eklenirse ve o kayıt önceki maxid'den büyükse
                {

                    Log.e("egerbuyukse",""+parameter1Int+"   "+parameter2Int);


                    getRecordHistory(new VolleyGetRecordHistory() {
                        @Override
                        public void onSuccess(String user_name, int record_id, String prevdepart, String nextdepart, String description, int addingType) {


                          if(addingtype ==0)
                            {
                                bildirimyolla("Kayıt Durumu Değişti","Kayıdınızın durum bilgisi değişti.");
                            }


                          if(addingtype==1)
                            {
                 bildirimyolla("Yeni Kayıt Eklendi.",""+nextdepart+" departmanına yeni bir kayıt eklendi.");

                            }

                          if(addingtype==2)
                            {
                 bildirimyolla("Yeni Kayıt Yönlendirildi.",""+prevdepart+" departmanından "+nextdepart+" departmanına "+user_name+" tarafından bir " +
                                        "kayıt yönlendirildi. \nAçıklama: \n "+description);

                            }
                        }
                    });

                    editorNotification.putInt(prefInside, parameter1Int).apply();
                    editorNotification.putInt(pref2Inside,parameter2Int).apply();

                }

                if (parameter1Int <preferencesNotification.getInt(prefInside,-1))       //eğer departmandan kayıt silinmişse
                    editorNotification.putInt(prefInside, parameter1Int).apply();

                if (parameter2Int <preferencesNotification.getInt(pref2Inside,-1))       //eğer departmandan kayıt silinmişse
                    editorNotification.putInt(pref2Inside, parameter2Int).apply();


            }
        });

    }

    private void bildirimyolla( final String title, final String bigText)
    {

                NotificationManager notificationManager =
                        (NotificationManager) NotificationIntentService.this.getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        NotificationChannel channel = new NotificationChannel("1", "Tabis", NotificationManager.IMPORTANCE_DEFAULT);
                        channel.setDescription("Kanal 1");
                        notificationManager.createNotificationChannel(channel);
                    }

                    Intent intent = new Intent(NotificationIntentService.this, Homepage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(NotificationIntentService.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NotificationIntentService.this, "1")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                            .setContentIntent(pendingIntent);

                    int m =(int) (System.currentTimeMillis()/10000);
                    notificationManager.notify(m, mBuilder.build());
                }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
