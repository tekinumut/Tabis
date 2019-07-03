package com.umonsoft.tabis.fragments.homepagetabs;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.umonsoft.tabis.HelperClasses.HelperMethods;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.phpvalues.PhpValues;

public class Tab1DepartRecords extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
  
  private View rootView;
  private Context mContext;
  private RecyclerView _recyclerView;
  private SwipeRefreshLayout _recyclerSwipeRefresh;
  private PhpValues phpValues;
  private Spinner _spinnerDeparts;
  private String sqlcode;
  private HelperMethods helperMethods;
  
  @NonNull
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	 
	 rootView = inflater.inflate(R.layout.tab_homepage, container, false);
	 
	 helperMethods = new HelperMethods(mContext);
	 helperMethods.ShowProgressDialog("Yükleniyor");
	 buildrecyclerview();
	 
	 final SharedPreferences preferencesLogin = mContext.getSharedPreferences(mContext.getString(R.string.loginvalues), Context.MODE_PRIVATE);
	 
	 sqlcode = "Select records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
				"(Select name from state where id =state) as state,statedesc,addingdate from records where isdelete =0 and department IN (SELECT department_id " +
				"from userdeparts where user_id = " + preferencesLogin.getInt("user_id", 0) + ") ORDER BY records.id DESC";
	 
	 phpValues.loadRecordValues(mContext, _recyclerView, 1, sqlcode, size -> {
		if (Integer.parseInt(size) > 0) {
		  String sqlcodeSpinner = "Select (Select name from departments where id = department_id) as departments from userdeparts where " +
					 "user_id =" + preferencesLogin.getInt("user_id", 0) + "";
		  
		  new PhpValues().loadRecordSpinnerValues(mContext, _spinnerDeparts, sqlcodeSpinner, response -> {
			 int length = Integer.parseInt(response);
			 if (length > 1) {
				_spinnerDeparts.setVisibility(View.VISIBLE);
				
				_spinnerDeparts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				  @Override
				  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					 if (position == 0) {
						sqlcode = "Select records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
								  "(Select name from state where id =state) as state,statedesc,addingdate from records where isdelete =0 and department IN (SELECT department_id " +
								  "from userdeparts where user_id = " + preferencesLogin.getInt("user_id", 0) + ") ORDER BY records.id DESC";
						
						phpValues.loadRecordValues(mContext, _recyclerView, 1, sqlcode, null);
					 } else {
						
						sqlcode = "Select records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
								  "(Select name from state where id =state) as state,statedesc,addingdate from records where isdelete =0 and department =(Select id from departments " +
								  "where name = '" + parent.getSelectedItem().toString() + "')  ORDER BY records.id DESC";
						phpValues.loadRecordValues(mContext, _recyclerView, 1, sqlcode, null);
					 }
				  }
				  
				  @Override
				  public void onNothingSelected(AdapterView<?> parent) {
					 
					 Log.e("umut", "hehe");
				  }
				});
			 }
			 
			 new Handler().postDelayed(() -> helperMethods.HideProgressDialog(), 500);
			 
		  });
		}
	 });
	 
	 return rootView;
	 
  }//end of onCreateView
  
  private void buildrecyclerview() {
	 
	 
	 _recyclerView = rootView.findViewById(R.id.recyclerView);
	 _recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
	 _recyclerView.setHasFixedSize(true);
	 _recyclerSwipeRefresh = rootView.findViewById(R.id.recyclerSwipeRefresh);
	 _spinnerDeparts = rootView.findViewById(R.id.homepageSpinnerDeparts);
	 _recyclerSwipeRefresh.setOnRefreshListener(this);
	 phpValues = new PhpValues();
	 
  }
  
  
  @Override
  public void onRefresh() {
	 _recyclerSwipeRefresh.postDelayed(() -> {
		phpValues.loadRecordValues(mContext, _recyclerView, 1, sqlcode, null);
		Toast.makeText(mContext, getString(R.string.kayitlaryenilendi), Toast.LENGTH_SHORT).show();
		_recyclerSwipeRefresh.setRefreshing(false);
	 }, 500);
	 
	 
  }
  
  @Override
  public void onAttach(Context context) {
	 super.onAttach(context);
	 mContext = context;
  }
}
