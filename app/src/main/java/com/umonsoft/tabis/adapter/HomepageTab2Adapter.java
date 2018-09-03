package com.umonsoft.tabis.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umonsoft.tabis.Interfaces.VolleyGet1Parameter;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.activities.Homepage;
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

public class HomepageTab2Adapter extends RecyclerView.Adapter<HomepageTab2Adapter.HomepageTab2ViewHolder> {

    private Context mContext;
    private final List<RecordsModel> recyclerList;
    private PhpValues phpValues;

    public HomepageTab2Adapter(Context mContext, List<RecordsModel> recyclerList) {
        this.mContext = mContext;
        this.recyclerList = recyclerList;
    }

    @NonNull
    @Override
    public HomepageTab2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mView = View.inflate(mContext, R.layout.homepage_recycler_tab2, null);
        return new HomepageTab2ViewHolder(mView,mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomepageTab2ViewHolder holder, int position) {

        final RecordsModel model =recyclerList.get(position);
        holder._recyclerDepart.setText(model.getDepartment());
        holder._recyclerState.setText(model.getState());



        if (model.getState().equals(mContext.getString(R.string.reddedildi))){
            holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.RedKırmızı));
        }
        else if(model.getState().equals(mContext.getString(R.string.inceleniyor))){
            holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.İnceleSarı));
        }
        else if (model.getState().equals(mContext.getString(R.string.duzeltildi))){
            holder._recyclerState.setBackgroundColor(mContext.getResources().getColor(R.color.DuzYesil));
        }

        holder._recyclerDescription.setText(model.getDescription());

        //  holder.imageView.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(model.getImage())));

        phpValues.get1Parameter(mContext, "Select image from recordimages where record_id = " + model.getId() + " LIMIT 1", new VolleyGet1Parameter() {
            @Override
            public void onSuccess(String response) {

                if(response.equals("null"))
                    holder._recyclerImageView.setImageResource(R.drawable.noimage);
                else
                {
                    Glide.with(mContext)
                            .load(response)
                            .into(holder._recyclerImageView);
                }

            }
        });

        phpValues.get1Parameter(mContext,"Select NOW()",new VolleyGet1Parameter(){
            @Override
            public void onSuccess(String dateStop){

                try {

                    DateTimeFormatter dateFormat1 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                    LocalDateTime date1 = LocalDateTime.parse(model.getAddingdate(), dateFormat1);
                    LocalDateTime date2 = LocalDateTime.parse(dateStop, dateFormat1);

                   // int diffHours = Hours.hoursBetween(date1,date2).getHours();
                    int diffDays  = Days.daysBetween(date1,date2).getDays();
                    int diffWeeks = Weeks.weeksBetween(date1,date2).getWeeks();
                    int diffMonths = Months.monthsBetween(date1,date2).getMonths();
                    int diffYears = Years.yearsBetween(date1,date2).getYears();

                    Period period = new Period(date1, date2, PeriodType.yearMonthDayTime());

                    if(diffDays==0) //Geçen zaman 0 ile 24 saat arasında ise
                    {
                        holder._recyclerRemainingDate2.setTextColor(mContext.getResources().getColor(R.color.DuzYesil));
                      //  holder._recyclerRemainingDate2.setText(String.valueOf(diffHours+" saat"));

                            holder._recyclerRemainingDate2.setText(String.valueOf(mContext.getString(R.string.yenieklendi)));
                    }
                    else if(diffDays>=1 && diffWeeks<1) //Geçen zaman 1 gün ile 7 gün arasında ise
                    {
                        holder._recyclerRemainingDate2.setTextColor(mContext.getResources().getColor(R.color.DuzYesil));
                        holder._recyclerRemainingDate2.setText(String.valueOf(diffDays+" gün "+period.getHours()+" saat"));
                        if(period.getHours()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffDays+" gün"));
                    }
                    else if(diffWeeks>=1 && diffMonths <1) //Geçen zaman 1 hafta ile 1 ay arasında ise
                    {
                        Period periodWeeks = new Period(date1, date2, PeriodType.standard());
                        holder._recyclerRemainingDate2.setTextColor(mContext.getResources().getColor(R.color.İnceleSarı));
                        holder._recyclerRemainingDate2.setText(String.valueOf(diffWeeks+" hafta "+periodWeeks.getDays()+" gün"));
                        if(periodWeeks.getDays()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffWeeks+" hafta"));
                    }
                    else if(diffMonths>=1 && diffYears<1) //Geçen zaman 1 aydan fazla ve 1 yıldan az ise
                    {

                        holder._recyclerRemainingDate2.setTextColor(mContext.getResources().getColor(R.color.RedKırmızı));
                        holder._recyclerRemainingDate2.setText(String.valueOf(diffMonths+" ay "+period.getDays()+" gün"));

                        if(period.getDays()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffMonths+" ay"));

                    }
                    else if(diffYears>=1)   //Geçen zaman 1 yıldan fazla ise
                    {
                        holder._recyclerRemainingDate2.setTextColor(mContext.getResources().getColor(R.color.RedKırmızı));
                        holder._recyclerRemainingDate2.setText(String.valueOf(diffYears+" yıl "+period.getMonths()+" ay "+period.getDays()+" gün"));

                        if(period.getMonths()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffYears+" yıl "+period.getDays()+" gün"));
                        else if(period.getDays()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffYears+" yıl "+period.getMonths()+" ay"));
                        else if(period.getMonths()==0 && period.getDays()==0)
                            holder._recyclerRemainingDate2.setText(String.valueOf(diffYears+" yıl"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }); //end get1Parameter now

    }

    @Override
    public int getItemCount() {
        return recyclerList.size();
        //  return Math.min(recyclerList.size(), 10);
    }

    @SuppressWarnings("WeakerAccess")
    public class HomepageTab2ViewHolder extends RecyclerView.ViewHolder {

        final TextView _recyclerDepart;
        final TextView _recyclerState;
        final TextView _recyclerDescription;
        final TextView _recyclerRemainingDate2;

        final ImageView _recyclerImageView;
        final ImageView _recyclerImageViewHide;


        public HomepageTab2ViewHolder(final View itemView, final Context context)  {
            super(itemView);
            mContext=context;
            phpValues =new PhpValues();

            _recyclerDepart = itemView.findViewById(R.id.rec_in_depart2);
            _recyclerState = itemView.findViewById(R.id.rec_in_state2);
            _recyclerDescription=itemView.findViewById(R.id.rec_in_description2);
            _recyclerImageView = itemView.findViewById(R.id.rec_in_image2);
            _recyclerImageViewHide=itemView.findViewById(R.id.rec_in_image_hide2);
            _recyclerRemainingDate2=itemView.findViewById(R.id.rec_in_remaning_date2);

            _recyclerImageViewHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    RecordsModel recordsModel = recyclerList.get(getAdapterPosition());
                                    String sqlcode="Update records set isdelete = 1 where id= "+ recordsModel.getId();
                                    phpValues.sentItem(mContext, sqlcode,null, "deleterecord", new VolleyGet1Parameter() {
                                        @Override
                                        public void onSuccess(String response) {
                                            recyclerList.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                            notifyItemRangeChanged(getAdapterPosition(), recyclerList.size());

                                            //Aşağıda ki method yukarıda ki iki methodun yaptığını yapar ama çok kaynak tüketir ve animasyon göremeyiz
                                            //notifyDataSetChanged();
                                        }
                                    });
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(mContext.getString(R.string.kayitsil)).setPositiveButton(mContext.getString(R.string.dialog_evet), dialogClickListener)
                            .setNegativeButton(mContext.getString(R.string.dialog_hayir), dialogClickListener).show();

                }
            });



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
                        bundle.putString("dialogStateDesc",recordsModel.getStatedesc());

                        dialog1.setArguments(bundle);

                        dialog1.show(manager,"Kayıt Detay");

                    itemView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            itemView.setClickable(true);

                        }
                    }, 500);

                }
            });


        }
    }//end of viewholder


}//end of adapter
