package com.umonsoft.tabis.phpvalues;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.umonsoft.tabis.Interfaces.VolleyGet1Parameter;
import com.umonsoft.tabis.Interfaces.VolleyGet1ParameterWithError;
import com.umonsoft.tabis.Interfaces.VolleyGet1Parameters;
import com.umonsoft.tabis.Interfaces.VolleyGet2Parameter;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.adapter.HomepageTab1Adapter;
import com.umonsoft.tabis.adapter.HomepageTab2Adapter;
import com.umonsoft.tabis.model.RecordsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhpValues {

    public void get1Parameter(final Context mContext, final String sqlcode, final VolleyGet1Parameter callback) {

        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_get1parameter), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray values =new JSONArray(response);

                    if(values.length()!=0) {
                        JSONObject object = values.getJSONObject(0);

                        if (callback != null) {
                            callback.onSuccess(object.getString("parameter1"));
                        }

                    }else if(values.length()==0)
                    {
                        if (callback != null) {
                            callback.onSuccess("null");
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
                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);

                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void get2Parameter(final Context mContext, final String sqlcode, final VolleyGet2Parameter callback) {

        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_get2parameter), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray values =new JSONArray(response);
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject object=values.getJSONObject(i);

                        if(i==values.length()-1)
                        {
                            if(object.getString("parameter1").equals("null")  || object.getString("parameter2").equals("null"))
                            {
                                callback.onSuccess("0","0");
                            }else
                                callback.onSuccess(object.getString("parameter1"),object.getString("parameter2"));
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
                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);

                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void get1Parameters(final Context mContext, final String sqlcode, final VolleyGet1Parameters callback) {


        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_get1parameter), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {
                    JSONArray values =new JSONArray(response);
                    if(values.length()!=0)
                    {
                        for (int i = 0; i < values.length(); i++) {
                            JSONObject object=values.getJSONObject(i);

                            if(callback!=null)
                                callback.onSuccess(object.getString("parameter1"),values.length());
                        }
                    }
                    else if(values.length()==0)
                    {
                        if (callback != null) {
                            callback.onSuccess("null",0);
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
                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);

                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void loadSpinnerValues(final Context mContext,final Spinner spinnerDepart,final String sqlcode) {

        final ArrayList<String> spinnerArrayValues =new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_get1parameter), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinnerArrayValues.clear();
                try {
                    JSONArray values =new JSONArray(response);
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject object=values.getJSONObject(i);

                        String departments=object.getString("parameter1");

                        spinnerArrayValues.add(departments);

                    } spinnerDepart.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArrayValues));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                spinnerArrayValues.clear();
                spinnerArrayValues.add(mContext.getString(R.string.verialinamadi));
                spinnerDepart.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,spinnerArrayValues));
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);

                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void loadRecordSpinnerValues(final Context mContext, final Spinner spinnerDepart, final String sqlcode, final VolleyGet1Parameter callback) {

        final ArrayList<String> spinnerArrayValues =new ArrayList<>();
        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_get1parameter), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinnerArrayValues.clear();
                spinnerArrayValues.add("Bütün Departmanları Göster");
                try {
                    JSONArray values =new JSONArray(response);
                    if(values.length()!=0)
                    {
                        for (int i = 0; i < values.length(); i++) {
                            JSONObject object=values.getJSONObject(i);

                            String departments=object.getString("parameter1");

                            spinnerArrayValues.add(departments);

                            if (i==values.length()-1 && callback != null) {
                                callback.onSuccess(String.valueOf(values.length()));
                            }

                        }
                        spinnerDepart.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArrayValues));
                    }
                    else if(values.length()==0)
                    {
                        if (callback != null) {
                            callback.onSuccess("null");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                spinnerArrayValues.clear();
                spinnerArrayValues.add(mContext.getString(R.string.verialinamadi));
                spinnerDepart.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item,spinnerArrayValues));
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);

                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);

    }


    public void sentItem(final Context mContext,final String sqlcode,final String parameter1,final String deger,final VolleyGet1Parameter callback){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, mContext.getString(R.string.php_sentitem), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(deger!=null)
                {
                switch (deger) {

                    case "deleterecord":
                        Toast.makeText(mContext, mContext.getString(R.string.kayitsilindi), Toast.LENGTH_SHORT).show();
                        break;
                    case "stategonder":
                        Toast.makeText(mContext, mContext.getString(R.string.degisiklikkayitedildi), Toast.LENGTH_SHORT).show();
                        break;
                    case "departmentforward":
                        Toast.makeText(mContext, R.string.departmanyonlendirildi, Toast.LENGTH_SHORT).show();
                        break;
                        default: break;
                        }
                }
                if(callback!=null)
                    callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, mContext.getString(R.string.birhatameydanageldi), Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params =new HashMap<>();

                if(parameter1!=null)
                params.put("parameter1",parameter1);
                params.put(mContext.getString(R.string.sqlcode),sqlcode);
                return params;
            }
        };

        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void setLoginDetails(final Context mContext, final int user_id, final SharedPreferences.Editor editorlogin)
    {
    StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_sentlogindetails), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray values =new JSONArray(response);

                    for (int i = 0; i < values.length(); i++)
                    {
                        JSONObject object =values.getJSONObject(i);

                        String name =object.getString("name").trim();
                        String lastname =object.getString("lastname").trim();
                        String phone =object.getString("phone").trim();
                        String title =object.getString("title").trim();
                        String birthday =object.getString("birthday").trim();
                        String type =object.getString("type").trim();
                        String department =object.getString("department").trim();

                        if(i==values.length()-1)
                        {
                            String fullname = "" + name + " " + lastname;
                            editorlogin.putString("fullname", fullname);
                            editorlogin.putString("phone", phone);
                            editorlogin.putString("title", title);
                            editorlogin.putString("birthday", birthday);
                            editorlogin.putString("type", type);
                            editorlogin.putString("department",department).apply();
                        }
                    }
                    }
                catch (JSONException e) {
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
                Map<String,String> params =new HashMap<>();

                String sqlcode="Select * from users where id= "+user_id;

                params.put(mContext.getString(R.string.sqlcode),sqlcode);
                return params;
            }
        };
        Volley.newRequestQueue(mContext).add(stringRequest);
    }

    public void loadRecordValues(final Context mContext, final RecyclerView recyclerView, final int tabcount, final String sqlcode, final VolleyGet1Parameter callback) {

        final List<RecordsModel> recyclerList=new ArrayList<>();

        StringRequest stringRequest=new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, mContext.getString(R.string.php_getrecords), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                recyclerList.clear();
                recyclerView.setAdapter(null);
                try {
                    JSONArray values =new JSONArray(response);
                    for (int i = 0; i < values.length(); i++) {
                        JSONObject object=values.getJSONObject(i);

                        int id =object.getInt("id");
                        String department=object.getString("department");
                        String description=object.getString("description");
                        String address=object.getString("address");
                        String addressdesc=object.getString("addressdesc");
                        String lattitude=object.getString("lattitude");
                        String longitude=object.getString("longitude");
                        String state=object.getString("state");
                        String statedesc=object.getString("statedesc");
                        String addingdate=object.getString("addingdate");

                        RecordsModel model=new RecordsModel(id,department,description,address,addressdesc,
                                lattitude,longitude,state,statedesc,addingdate);
                        recyclerList.add(model);
                    }

                    if(tabcount==1)
                    {
                        recyclerView.setAdapter(new HomepageTab1Adapter(mContext,recyclerList));
                        new HomepageTab1Adapter(mContext,recyclerList).notifyDataSetChanged();
                    }

                    else if (tabcount==2)
                    {
                        recyclerView.setAdapter(new HomepageTab2Adapter(mContext,recyclerList));
                        new HomepageTab2Adapter(mContext,recyclerList).notifyDataSetChanged();
                    }

                    if(callback!=null)
                    {
                        callback.onSuccess(String.valueOf(recyclerList.size()));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(recyclerList.isEmpty()){
                    if(tabcount==1)
                    Toast.makeText(mContext, mContext.getString(R.string.gorevyok), Toast.LENGTH_LONG).show();
                    else if(tabcount==2)
                    Toast.makeText(mContext, mContext.getString(R.string.gonderiyok), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, mContext.getString(R.string.kayityuklenemedi), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams(){
                HashMap<String,String> params =new HashMap<>();


                params.put(mContext.getString(R.string.sqlcode),sqlcode);
                return params;
            }
        } ;

        Volley.newRequestQueue(mContext).add(stringRequest);

    }

    public void sendRecords(final Context mContext, final String imageData, final String sqlcode, final String targetnamesql, final VolleyGet1ParameterWithError callback)
    {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, mContext.getString(R.string.php_sentrecords), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(callback!=null)
                {
                    callback.onSuccess(response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(callback!=null)
                {
                    callback.onError(error.toString());
                }

            }
        })
        {
            @Override
            protected Map<String, String> getParams(){

                Map<String,String> params =new HashMap<>();

                params.put(mContext.getString(R.string.sqlcode),sqlcode);
                params.put("image",imageData);
                params.put("target_dir",targetnamesql);

                return params;
            }
        };

        Volley.newRequestQueue(mContext).add(stringRequest);
    }

}
