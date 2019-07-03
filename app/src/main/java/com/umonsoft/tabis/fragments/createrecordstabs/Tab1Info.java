package com.umonsoft.tabis.fragments.createrecordstabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.umonsoft.tabis.R;
import com.umonsoft.tabis.phpvalues.PhpValues;


public class Tab1Info extends Fragment {
  private Context mContext;
  
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
									Bundle savedInstanceState) {
	 View rootView = inflater.inflate(R.layout.createrecords_tab1info, container, false);
	 
	 
	 Spinner _spinner = rootView.findViewById(R.id.tab1spinnerdepart);
	 EditText _editText = rootView.findViewById(R.id.tab1description);
	 TextView _tab1mail = rootView.findViewById(R.id.tab1mail);
	 TextView _tab1phone = rootView.findViewById(R.id.tab1telefon);
	 TextView _tab1fullname = rootView.findViewById(R.id.tab1isim);
	 TextView _tab1title = rootView.findViewById(R.id.tab1unvan);
	 
	 _editText.clearFocus();
	 
	 SharedPreferences preferencesLogin = mContext.getSharedPreferences(getString(R.string.loginvalues), Context.MODE_PRIVATE);
	 
	 _tab1title.setText(preferencesLogin.getString("title", getString(R.string.verialinamadi)));
	 _tab1fullname.setText(preferencesLogin.getString("fullname", getString(R.string.verialinamadi)));
	 _tab1mail.setText(preferencesLogin.getString("email", getString(R.string.verialinamadi)));
	 _tab1phone.setText(preferencesLogin.getString("phone", getString(R.string.verialinamadi)));
	 
	 PhpValues phpValues = new PhpValues();
	 
	 String sqlcode = "Select name from departments";
	 
	 phpValues.loadSpinnerValues(mContext, _spinner, sqlcode);
	 
	 
	 return rootView;
	 
  }
  
  @Override
  public void onAttach(Context context) {
	 super.onAttach(context);
	 mContext = context;
  }
}
