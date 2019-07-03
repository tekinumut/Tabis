package com.umonsoft.tabis.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.fragments.createrecordstabs.Tab1Info;
import com.umonsoft.tabis.fragments.createrecordstabs.Tab2Map;
import com.umonsoft.tabis.fragments.createrecordstabs.Tab3Image;


public class CreateRecords extends AppCompatActivity {
  
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
	 View view = getCurrentFocus();
	 if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
		int[] scrcoords = new int[2];
		view.getLocationOnScreen(scrcoords);
		float x = ev.getRawX() + view.getLeft() - scrcoords[0];
		float y = ev.getRawY() + view.getTop() - scrcoords[1];
		if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
		  ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
	 }
	 return super.dispatchTouchEvent(ev);
  } //keyboard gizler.
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_create_records);
	 
	 SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
	 // Set up the ViewPager with the sections adapter.
	 ViewPager mViewPager = findViewById(R.id.container);
	 mViewPager.setAdapter(mSectionsPagerAdapter);
	 
	 TabLayout tabLayout = findViewById(R.id.tabs);
	 
	 mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
	 tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
	 mViewPager.setOffscreenPageLimit(3);
	 
	 
  }
  
  class SectionsPagerAdapter extends FragmentPagerAdapter {
	 
	 private SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	 }
	 
	 @Override
	 public Fragment getItem(int position) {
		
		
		switch (position) {
		  
		  case 0:
			 return new Tab1Info();
		  
		  case 1:
			 return new Tab2Map();
		  
		  case 2:
			 return new Tab3Image();
		  
		  default:
			 return null;
		}
	 }
	 
	 @Override
	 public int getCount() {
		return 3;
	 }
  }
  
  @Override
  public void onBackPressed() {
	 super.onBackPressed();
	 finish();
  }
}
