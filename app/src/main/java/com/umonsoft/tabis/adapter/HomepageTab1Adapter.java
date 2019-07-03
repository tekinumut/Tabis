package com.umonsoft.tabis.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.activities.Homepage;
import com.umonsoft.tabis.fragments.dialogfragments.ChangeState;
import com.umonsoft.tabis.fragments.dialogfragments.RecordsDetails;
import com.umonsoft.tabis.model.RecordsModel;
import com.umonsoft.tabis.phpvalues.PhpValues;

import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class HomepageTab1Adapter extends RecyclerView.Adapter<HomepageTab1Adapter.HomepageTab1ViewHolder> {
  
  private Context mContext;
  private final List<RecordsModel> recyclerList;
  private SharedPreferences preferencesLogin;
  private PhpValues phpValues;
  
  public HomepageTab1Adapter(Context mContext, List<RecordsModel> recyclerList) {
	 this.mContext = mContext;
	 this.recyclerList = recyclerList;
  }
  
  @NonNull
  @Override
  public HomepageTab1ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
	 
	 View mView = View.inflate(mContext, R.layout.homepage_recycler_tab1, null);
	 return new HomepageTab1ViewHolder(mView, mContext);
  }
  
  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(@NonNull final HomepageTab1ViewHolder holder, int position) {
	 
	 final RecordsModel model = recyclerList.get(position);
	 holder._recyclerDepart.setText(model.getDepartment());
	 holder._recyclerState.setText(model.getState());
	 
	 if (model.getState().equals(mContext.getString(R.string.reddedildi))) {
		holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.RedKırmızı));
	 } else if (model.getState().equals(mContext.getString(R.string.inceleniyor))) {
		holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.İnceleSarı));
	 } else if (model.getState().equals(mContext.getString(R.string.duzeltildi))) {
		holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.DuzYesil));
	 }
	 
	 holder._recyclerDescription.setText(model.getDescription());
	 
	 
	 //  holder.imageView.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(model.getImage())));
	 
	 phpValues.get1Parameter(mContext, "Select image from recordimages where record_id = " + model.getId() + " LIMIT 1", response -> {
		
		if (response.equals("null"))
		  holder._recyclerImageView.setImageResource(R.drawable.noimage);
		else {
		  Glide.with(mContext)
					 .load(response)
					 .into(holder._recyclerImageView);
		}
	 });
	 
	 
	 phpValues.get1Parameter(mContext, "Select NOW()", dateStop -> {
		
		try {
		  
		  DateTimeFormatter dateFormat1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		  
		  LocalDateTime date1 = LocalDateTime.parse(model.getAddingdate(), dateFormat1);
		  LocalDateTime date2 = LocalDateTime.parse(dateStop, dateFormat1);
		  
		  // int diffHours = Hours.hoursBetween(date1,date2).getHours();
		  int diffDays = Days.daysBetween(date1, date2).getDays();
		  int diffWeeks = Weeks.weeksBetween(date1, date2).getWeeks();
		  int diffMonths = Months.monthsBetween(date1, date2).getMonths();
		  int diffYears = Years.yearsBetween(date1, date2).getYears();
		  
		  Period period = new Period(date1, date2, PeriodType.yearMonthDayTime());
		  
		  if (diffDays == 0) //Geçen zaman 0 ile 24 saat arasında ise
		  {
			 holder._recyclerRemainingDate.setTextColor(mContext.getResources().getColor(R.color.DuzYesil));
			 // holder._recyclerRemainingDate.setText(String.valueOf(diffHours+" saat"));
			 holder._recyclerRemainingDate.setText(mContext.getString(R.string.yenieklendi));
		  } else if (diffDays >= 1 && diffWeeks < 1) //Geçen zaman 1 gün ile 7 gün arasında ise
		  {
			 holder._recyclerRemainingDate.setTextColor(mContext.getResources().getColor(R.color.DuzYesil));
			 holder._recyclerRemainingDate.setText(diffDays + " gün " + period.getHours() + " saat");
			 if (period.getHours() == 0)
				holder._recyclerRemainingDate.setText(diffDays + " gün");
		  } else if (diffWeeks >= 1 && diffMonths < 1) //Geçen zaman 1 hafta ile 1 ay arasında ise
		  {
			 Period periodWeeks = new Period(date1, date2, PeriodType.standard());
			 holder._recyclerRemainingDate.setTextColor(mContext.getResources().getColor(R.color.İnceleSarı));
			 holder._recyclerRemainingDate.setText(diffWeeks + " hafta " + periodWeeks.getDays() + " gün");
			 if (periodWeeks.getDays() == 0)
				holder._recyclerRemainingDate.setText(diffWeeks + " hafta");
		  } else if (diffMonths >= 1 && diffYears < 1) //Geçen zaman 1 aydan fazla ve 1 yıldan az ise
		  {
			 
			 holder._recyclerRemainingDate.setTextColor(mContext.getResources().getColor(R.color.RedKırmızı));
			 holder._recyclerRemainingDate.setText(diffMonths + " ay " + period.getDays() + " gün");
			 
			 if (period.getDays() == 0)
				holder._recyclerRemainingDate.setText(diffMonths + " ay");
			 
		  } else if (diffYears >= 1)   //Geçen zaman 1 yıldan fazla ise
		  {
			 holder._recyclerRemainingDate.setTextColor(mContext.getResources().getColor(R.color.RedKırmızı));
			 holder._recyclerRemainingDate.setText(diffYears + " yıl " + period.getMonths() + " ay " + period.getDays() + " gün");
			 
			 if (period.getMonths() == 0)
				holder._recyclerRemainingDate.setText(diffYears + " yıl " + period.getDays() + " gün");
			 else if (period.getDays() == 0)
				holder._recyclerRemainingDate.setText(diffYears + " yıl " + period.getMonths() + " ay");
			 else if (period.getMonths() == 0 && period.getDays() == 0)
				holder._recyclerRemainingDate.setText(diffYears + " yıl");
			 
		  }
		  
		  
		} catch (Exception e) {
		  e.printStackTrace();
		}
		
	 }); //get1Parameter NOW END
	 
	 
  }
  
  
  @Override
  public int getItemCount() {
	 return recyclerList.size();
	 //  return Math.min(recyclerList.size(), 10);
  }
  
  @SuppressWarnings("WeakerAccess")
  public class HomepageTab1ViewHolder extends RecyclerView.ViewHolder {
	 
	 final TextView _recyclerDepart;
	 final TextView _recyclerState;
	 final TextView _recyclerDescription;
	 final TextView _recyclerRemainingDate;
	 
	 final ImageView _recyclerImageView;
	 final ImageView _recyclerImageDepartmentForward;
	 
	 
	 public HomepageTab1ViewHolder(final View itemView, final Context context) {
		super(itemView);
		mContext = context;
		phpValues = new PhpValues();
		
		_recyclerDepart = itemView.findViewById(R.id.rec_in_depart);
		_recyclerState = itemView.findViewById(R.id.rec_in_state);
		_recyclerDescription = itemView.findViewById(R.id.rec_in_description);
		_recyclerImageView = itemView.findViewById(R.id.rec_in_image);
		_recyclerImageDepartmentForward = itemView.findViewById(R.id.rec_in_department_forward);
		_recyclerRemainingDate = itemView.findViewById(R.id.rec_in_remaning_date);
		
		preferencesLogin = mContext.getSharedPreferences(mContext.getString(R.string.loginvalues), Context.MODE_PRIVATE);
		
		String userTypeValue = preferencesLogin.getString("type", "3");
		
		if (userTypeValue != null && userTypeValue.equals("2")) {//Eğer giriş yapan personel ise
		  
		  
		  //end of Onclick
		  _recyclerState.setOnClickListener(v -> {
			 
			 final RecordsModel recordsModel = recyclerList.get(getAdapterPosition());
			 FragmentManager manager = ((Homepage) mContext).getSupportFragmentManager();
			 
			 ChangeState changeState = new ChangeState();
			 
			 //    if (recordsModel.getState().equals(mContext.getString(R.string.choosestate_dialog_button_viewing)))
			 if (_recyclerState.getText().equals(mContext.getString(R.string.inceleniyor))) {
				
				_recyclerState.setClickable(false);
				Bundle bundle = new Bundle();
				bundle.putString("id", "" + recordsModel.getId());
				bundle.putString("state", recordsModel.getState());
				
				
				changeState.setArguments(bundle);
				changeState.show(manager, "Kayıt Detay");
				
				
				_recyclerState.postDelayed(new Runnable() {
				  @Override
				  public void run() {
					 _recyclerState.setClickable(true);
					 
				  }
				}, 300); //bitir
				
			 }
		  });
		  
		  
		  _recyclerImageDepartmentForward.setOnClickListener(v -> {
			 
			 if (_recyclerState.getText().equals(mContext.getString(R.string.inceleniyor))) {
				
				final View mViewForward = View.inflate(mContext, R.layout.dialog_departforward, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setView(mViewForward);
				
				final Spinner _spinnerForward = mViewForward.findViewById(R.id.dialogDepartmentForward);
				
				final String sqlcode = "Select name from departments";
				phpValues.loadSpinnerValues(mContext, _spinnerForward, sqlcode);
				
				
				builder.setPositiveButton(mContext.getString(R.string.dialog_onayla), (dialog, which) -> {
				  
				  RecordsModel recordsModel = recyclerList.get(getAdapterPosition());
				  EditText _departDesc = mViewForward.findViewById(R.id.dialogDepartDescEdittext);
				  
				  String sqlcodeForward = "Update records SET department = (SELECT id from departments where name = '" + _spinnerForward.getSelectedItem().toString() + "') where" +
							 " id = " + recordsModel.getId();
				  
				  phpValues.sentItem(mContext, sqlcodeForward, null, "departmentforward", null);
				  
				  String sqlcodeHistoryDepart = "Insert into recordhistory (user_id,record_id,prevdepart,nextdepart,description,addingtype) VALUES " +
							 "( " + preferencesLogin.getInt("user_id", 0) + ", " + recordsModel.getId() +
							 " ,(SELECT id from departments where name = '" + recordsModel.getDepartment() + "'), " +
							 "  (SELECT id from departments where name = '" + _spinnerForward.getSelectedItem().toString() + "')" +
							 ",?,2) ";
				  
				  
				  phpValues.sentItem(mContext, sqlcodeHistoryDepart, _departDesc.getText().toString(), null, null);
				  
				  new Handler().postDelayed(() -> {
					 //
					 mContext.startActivity(new Intent(mContext, Homepage.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
				  }, 300);
				  
				  
				});
				
				builder.setNegativeButton(mContext.getString(R.string.dialog_iptalet), (dialog, which) -> {
				  //
				  dialog.dismiss();
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();
			 }
		  });
		}
		itemView.setOnClickListener(v -> {
		  
		  itemView.setClickable(false);
		  
		  RecordsModel recordsModel = recyclerList.get(getAdapterPosition());
		  
		  FragmentManager manager = ((Homepage) mContext).getSupportFragmentManager();
		  RecordsDetails dialog1 = new RecordsDetails();
		  
		  Bundle bundle = new Bundle();    //veri gönder.
		  bundle.putInt("recordid", recordsModel.getId());
		  bundle.putString("dialogDepart", recordsModel.getDepartment());
		  bundle.putString("dialogDescription", recordsModel.getDescription());
		  bundle.putString("dialogAddress", recordsModel.getAddress());
		  bundle.putString("dialogAdressDescription", recordsModel.getAddressdesc());
		  bundle.putString("dialogLattitude", recordsModel.getLattitude());
		  bundle.putString("dialogLongtitude", recordsModel.getLongitude());
		  bundle.putString("dialogState", _recyclerState.getText().toString());
		  bundle.putString("dialogStateDesc", recordsModel.getStatedesc());
		  
		  dialog1.setArguments(bundle);
		  
		  dialog1.show(manager, "Kayıt Detay");
		  
		  itemView.postDelayed(() -> itemView.setClickable(true), 500);
		  
		});
		
	 }//end of viewholder
  }//end of allviewholder
  
  
}//end of adapter
