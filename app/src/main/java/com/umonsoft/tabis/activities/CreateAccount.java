package com.umonsoft.tabis.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.umonsoft.tabis.HelperClasses.HelperMethods;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.phpvalues.PhpValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {
    private EditText _email;
    private EditText _password;
    private HelperMethods helperMethods;
    private Button _buttonCreateAccount;
    private Spinner _spinnerMail;

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
        setContentView(R.layout.activity_create_account);
        _email=findViewById(R.id.createacc_email);
        _password=findViewById(R.id.createacc_password);
        _buttonCreateAccount=findViewById(R.id.createacc_buttonCreate);
        _spinnerMail=findViewById(R.id.createacc_spinner_getmail);
        helperMethods=new HelperMethods(CreateAccount.this);

        PhpValues phpValues=new PhpValues();

        String sqlcode ="Select mailuzanti as parameter1 from mailuzanti";
        phpValues.loadSpinnerValues(CreateAccount.this,_spinnerMail,sqlcode);

        _buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helperMethods.ShowProgressDialog(getString(R.string.hesapolusturuluyor));

                String email=_email.getText().toString();
                String password=_password.getText().toString();

                if(email.isEmpty()  && password.isEmpty()){        //ikisi de boş ise
                    _email.setError(getString(R.string.mailbosbirakma));
                    _password.setError(getString(R.string.sifrebosbirakma));
                    _buttonCreateAccount.setEnabled(true);
                    helperMethods.HideProgressDialog();
                }
                else if(email.isEmpty()){
                    _email.setError(getString(R.string.mailbosbirakma));
                    _buttonCreateAccount.setEnabled(true);
                    helperMethods.HideProgressDialog();
                }
                else if(password.isEmpty()){
                    _password.setError(getString(R.string.sifrebosbirakma));
                    _buttonCreateAccount.setEnabled(true);
                    helperMethods.HideProgressDialog();
                }
                else{  //sorunsuz giriş yaparsa
                    createAccount();
                    _buttonCreateAccount.setEnabled(true);

                }
            }
        });

    }

    public void loginedonclick(View view) {
        startActivity(new Intent(CreateAccount.this,Login.class));
        finish();
    }

    private void createAccount()

    {
        final String email=_email.getText().toString()+_spinnerMail.getSelectedItem().toString();
        final String password=_password.getText().toString();

        final String sqlcode ="Insert into login (email,password) VALUES (?,?)";
        final String sqllogin="SELECT email FROM login WHERE email= ?";

        StringRequest stringRequest =new StringRequest(Request.Method.POST, getString(R.string.php_createaccount), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    switch (message) {
                        case "success":
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    helperMethods.HideProgressDialog();
                                    Toast.makeText(CreateAccount.this, getString(R.string.hesapolusturuldu), Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(CreateAccount.this, Login.class));
                                    finish();
                                }
                            }, 500);

                            break;
                        case "duplicate":
                            helperMethods.HideProgressDialog();

                            AlertDialog alertDialog = new AlertDialog.Builder(CreateAccount.this).create();
                            alertDialog.setTitle("Dikkat!");
                            alertDialog.setMessage(getString(R.string.emailkullanılıyor));
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_tamam),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                            break;
                        case "error":
                            helperMethods.HideProgressDialog();
                            Toast.makeText(CreateAccount.this, getString(R.string.hesapolusturulamadı), Toast.LENGTH_LONG).show();
                            break;
                    }

                }catch (JSONException e)
                {
                    Toast.makeText(CreateAccount.this, getString(R.string.hesapolusturulamadı), Toast.LENGTH_LONG).show();
                    helperMethods.HideProgressDialog();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateAccount.this, getString(R.string.hesapolusturulamadı), Toast.LENGTH_LONG).show();
                helperMethods.HideProgressDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params=new HashMap<>();
                params.put("sqlcode",sqlcode);
                params.put("sqllogin",sqllogin);
                params.put("email",email);
                params.put("password",password);

                return params;
            }
        };
        Volley.newRequestQueue(CreateAccount.this).add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(CreateAccount.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }
}
