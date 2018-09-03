package com.umonsoft.tabis.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.umonsoft.tabis.HelperClasses.HelperMethods;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.phpvalues.PhpValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private CheckBox _loginCheckBox;
    private EditText _email;
    private EditText _password;
    private Button _buttonLogin;
    private HelperMethods helperMethods;
    private SharedPreferences preferencesLogin,preferencesRememberMe;
    private SharedPreferences.Editor editorLogin,editorRememberMe;
    private boolean hatirla;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {  //keyboard gizler.
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrooges[] = new int[2];
            view.getLocationOnScreen(scrooges);
            float x = ev.getRawX() + view.getLeft() - scrooges[0];
            float y = ev.getRawY() + view.getTop() - scrooges[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _email=findViewById(R.id.emailedittext);
        _password=findViewById(R.id.sifreedittext);
        _loginCheckBox=findViewById(R.id.loginCheckBox);
        _buttonLogin=findViewById(R.id.girisyapbutton);

        preferencesRememberMe=getSharedPreferences(getString(R.string.remembermevalues),Context.MODE_PRIVATE);
        preferencesLogin=getSharedPreferences(getString(R.string.loginvalues),Context.MODE_PRIVATE);
        editorLogin=preferencesLogin.edit();            editorLogin.apply();
        editorRememberMe=preferencesRememberMe.edit();  editorRememberMe.apply();

        hatirla=preferencesRememberMe.getBoolean(getString(R.string.rememberme_hatirla),false);
        helperMethods=new HelperMethods(Login.this);

        loginFile();

    } //end of onCreate

    private void loginFile(){
    //    File fileShared = new File("/data/data/com.umonsoft.tabis/shared_prefs/loginrememberme.xml");

        if(hatirla) {

            _loginCheckBox.setChecked(true);
            _email.setText(preferencesRememberMe.getString(getString(R.string.rememberme_email),""));
            _password.setText(preferencesRememberMe.getString(getString(R.string.rememberme_password),""));
            _buttonLogin.performClick();

        }
    }

    public void girisyapClick (View view){
        _buttonLogin.setEnabled(false);
        String email = this._email.getText().toString();
        String password = this._password.getText().toString();

        helperMethods.ShowProgressDialog(getString(R.string.dialog_baglaniyor));

         if(email.isEmpty()  && password.isEmpty()){        //ikisi de boş ise
             _email.setError(getString(R.string.mailbosbirakma));
             _password.setError(getString(R.string.sifrebosbirakma));
             _buttonLogin.setEnabled(true);
             helperMethods.HideProgressDialog();
         }
         else if(email.isEmpty()){
             _email.setError(getString(R.string.mailbosbirakma));
             _buttonLogin.setEnabled(true);
             helperMethods.HideProgressDialog();
         }
         else if(password.isEmpty()){
             _password.setError(getString(R.string.sifrebosbirakma));
             _buttonLogin.setEnabled(true);
             helperMethods.HideProgressDialog();
         }
         else{  //sorunsuz giriş yaparsa
                 LoginAcc(email, password);
             _buttonLogin.setEnabled(true);

         }
    }

    private void LoginAcc(final String email, final String password){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, getString(R.string.php_login), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    if (success.equals("1")) {


                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object =jsonArray.getJSONObject(i);
                            int user_id =object.getInt("user_id");
                            String email =object.getString("email").trim();

                            if(i==jsonArray.length()-1){
                                editorLogin.putInt("user_id",user_id);
                                editorLogin.putString("email",email).apply();
                            }
                        }

                        new PhpValues().setLoginDetails(Login.this,preferencesLogin.getInt("user_id",0),editorLogin);

                        String sqlcode = "Update users SET songiris = NOW() where id ="+preferencesLogin.getInt("user_id",0);
                        new PhpValues().sentItem(Login.this,sqlcode,null,"songiris",null);


                        if(_loginCheckBox.isChecked()){

                        editorRememberMe.putBoolean(getString(R.string.rememberme_hatirla),true);
                        editorRememberMe.putString(getString(R.string.rememberme_email),email);
                        editorRememberMe.putString(getString(R.string.rememberme_password),password).apply();

                        }else {
                            editorRememberMe.clear().apply();
                        }

                    //    dialog.dismiss();
                    //    startActivity(new Intent(Login.this,Homepage.class));

                       new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                           startActivity(new Intent(Login.this,Homepage.class));
                                           finish();
                                            helperMethods.HideProgressDialog();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        _email.clearFocus();
                        _password.clearFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, getString(R.string.girisbasarisiz), Toast.LENGTH_SHORT).show();
                    helperMethods.HideProgressDialog();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Login.this, getString(R.string.girisbasarisiz), Toast.LENGTH_SHORT).show();
                helperMethods.HideProgressDialog();
            }
        })
        {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params =new HashMap<>();
                params.put("email",email);
                params.put("password",password);
                return params;
            }
        };
        Volley.newRequestQueue(Login.this).add(stringRequest);

    }

    public void emailClick(View view){

        _email.setError(null);
    }
    public void sifreClick(View view){

        _email.setError(null);
    }

    public void kayitolustur(View view) {

        startActivity(new Intent(Login.this,CreateAccount.class));
        finish();
    }
}
