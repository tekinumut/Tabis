package com.umonsoft.tabis.fragments.homepagetabs;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.umonsoft.tabis.R;
import com.umonsoft.tabis.phpvalues.PhpValues;

public class Tab2MyRecords extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
  
  
  private View rootView;
  private Context mContext;
  private RecyclerView _recyclerView;
  private SwipeRefreshLayout _recyclerSwipeRefresh;
  private PhpValues phpValues;
  private Spinner _spinnerDeparts;
  private String sqlcode;
  
  @NonNull
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
									Bundle savedInstanceState) {
	 rootView = inflater.inflate(R.layout.tab_homepage, container, false);
	 
	 buildrecyclerview();
	 
	 final SharedPreferences preferencesLogin = mContext.getSharedPreferences(mContext.getString(R.string.loginvalues), Context.MODE_PRIVATE);
	 
	 //kendine ait departmanların kayıtları
	 sqlcode = "SELECT records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
				"(Select name from state where id =state) as state,statedesc,addingdate from records WHERE isdelete = 0 AND user_id = " +
				"" + preferencesLogin.getInt("user_id", 0) + " ORDER BY records.id DESC ";
	 
	 phpValues.loadRecordValues(mContext, _recyclerView, 2, sqlcode, size -> {
		if (Integer.parseInt(size) > 0) {
		  String spinnerSqlCode = "Select (SELECT name from departments where id = department) from records where user_id =" + preferencesLogin.getInt("user_id", 0) + "";
		  new PhpValues().loadRecordSpinnerValues(mContext, _spinnerDeparts, spinnerSqlCode, null);
		  
		  _spinnerDeparts.setVisibility(View.VISIBLE);
		  
		  _spinnerDeparts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			 @Override
			 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
				  sqlcode = "SELECT records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
							 "(Select name from state where id =state) as state,statedesc,addingdate from records WHERE isdelete = 0 AND user_id = " +
							 "" + preferencesLogin.getInt("user_id", 0) + " ORDER BY records.id DESC ";
				  
				  phpValues.loadRecordValues(mContext, _recyclerView, 2, sqlcode, null);
				} else {
				  
				  sqlcode = "SELECT records.id,(Select name from departments where id = department) as department,description,address,addressdesc,lattitude,longitude," +
							 "(Select name from state where id =state) as state,statedesc,addingdate from records WHERE isdelete = 0 AND user_id = " + preferencesLogin.getInt("user_id", 0) + "" +
							 " and department =(Select id from departments where name = '" + parent.getSelectedItem().toString() + "') ORDER BY records.id DESC ";
				  phpValues.loadRecordValues(mContext, _recyclerView, 2, sqlcode, null);
				}
			 }
			 
			 @Override
			 public void onNothingSelected(AdapterView<?> parent) {
				
				
			 }
		  });
		}
	 });
	 
	 return rootView;
	 
  }
  
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
		phpValues.loadRecordValues(mContext, _recyclerView, 2, sqlcode, null);
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
