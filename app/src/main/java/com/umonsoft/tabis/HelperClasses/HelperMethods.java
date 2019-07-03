package com.umonsoft.tabis.HelperClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.umonsoft.tabis.R;

public class HelperMethods {
  private final Context mContext;
  private AlertDialog b;
  
  public HelperMethods(Context mContext) {
	 this.mContext = mContext;
  }
  
  @SuppressLint("InflateParams")
  public void ShowProgressDialog(String message) {
	 
	 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
	 LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 @SuppressLint("ResourceType") View dialogView;
	 if (inflater != null) {
		dialogView = inflater.inflate(R.layout.progress_dialog_gonderiliyor, null);
		TextView textView = dialogView.findViewById(R.id.textViewProgress);
		textView.setText(message);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setCancelable(true);
		b = dialogBuilder.create();
		b.show();
	 }
	 
  }
  
  public void HideProgressDialog() {
	 
	 b.dismiss();
  }
}
