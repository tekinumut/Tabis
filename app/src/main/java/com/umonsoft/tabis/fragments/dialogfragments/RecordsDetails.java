package com.umonsoft.tabis.fragments.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umonsoft.tabis.Interfaces.VolleyGet1Parameters;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.adapter.RecordDetailsViewPagerAdapter;
import com.umonsoft.tabis.phpvalues.PhpValues;

import java.util.ArrayList;
import java.util.List;

public class RecordsDetails extends DialogFragment implements OnMapReadyCallback {

    private Double dialogLattitude,dialogLongtitude;
    private Context mContext;
    private View view;
    private int recordid =0;
    private String state;

    @NonNull
    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = View.inflate(mContext,R.layout.dialog_recordsdetails,null);

        values();

        ArrayList<String> spinnerArrayValues =new ArrayList<>();
        spinnerArrayValues.clear();
            spinnerArrayValues.add("Tüm resimleri göster");
            spinnerArrayValues.add("İşlem öncesi resimleri göster");
            spinnerArrayValues.add("İşlem sonrası resimleri göster");


            if(!state.equals(mContext.getString(R.string.inceleniyor)))
            {
                Spinner _spinnerType =view.findViewById(R.id.spinnerImageType);
                _spinnerType.setVisibility(View.VISIBLE);
                _spinnerType.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArrayValues));

                _spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0)
                        {
                            loadImages("SELECT image from recordimages where record_id = "+recordid+"");
                        }
                        else if (position==1)
                        {
                            loadImages("SELECT image from recordimages where record_id = "+recordid+" and type = 1" );
                        }
                        else if(position==2)
                        {
                            loadImages("SELECT image from recordimages where record_id = "+recordid+" and type = 2");
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else
            {
                loadImages("SELECT image from recordimages where record_id = "+recordid+"");
            }


        MapView _dialogMapView =view.findViewById(R.id.d_summary_MapView);
        _dialogMapView.onCreate(null);
        _dialogMapView.onResume();
        _dialogMapView.getMapAsync(this);

        view.findViewById(R.id.summary_yoltarifial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+dialogLattitude+","+dialogLongtitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        };

        return new AlertDialog.Builder(mContext)
                .setTitle("Rapor Özeti")
                .setView(view)
                .setPositiveButton(android.R.string.ok,listener)
                .create();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.clear();
        LatLng userLocation = new LatLng(dialogLattitude, dialogLongtitude);
        googleMap.addMarker(new MarkerOptions().position(userLocation).title("KONUM").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dialogLattitude, dialogLongtitude), 13.0f));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }


    private void loadImages(String sqlcode)
    {
        PhpValues phpValues=new PhpValues();
        final ViewPager viewPager=view.findViewById(R.id.record_details_view_pager);
        final List<String> imageList =new ArrayList<>();
        imageList.clear();
        viewPager.setAdapter(null);
        phpValues.get1Parameters(mContext, sqlcode, new VolleyGet1Parameters() {
            @Override
            public void onSuccess(String response,int size) {

                if(response.equals("null"))
                {
                    imageList.add(mContext.getString(R.string.file_noimage));
                    RecordDetailsViewPagerAdapter adapter=new RecordDetailsViewPagerAdapter(mContext,imageList);
                    viewPager.setAdapter(adapter);
                }
                else
                { imageList.add(response);
                    if(imageList.size()==size)
                    {
                        RecordDetailsViewPagerAdapter adapter=new RecordDetailsViewPagerAdapter(mContext,imageList);
                        viewPager.setAdapter(adapter);
                    }
                }
            }
        });
    }



    private void values()
    {
        SharedPreferences preferencesLogin = mContext.getSharedPreferences(getString(R.string.loginvalues), Context.MODE_PRIVATE);

        TextView _dialogName = view.findViewById(R.id.d_summary_name);
        TextView _dialogTel = view.findViewById(R.id.d_summary_tel);
        TextView _dialogEmail = view.findViewById(R.id.d_summary_Email);
        TextView _dialogDepart = view.findViewById(R.id.d_summary_department);
        TextView _dialogDescription = view.findViewById(R.id.d_summary_description);
        TextView _dialogAddress = view.findViewById(R.id.d_summary_address);
        TextView _dialogAdressDescription = view.findViewById(R.id.d_summary_addressdesc);
        TextView _dialogState = view.findViewById(R.id.d_summary_state);
        TextView _dialogStateDesc = view.findViewById(R.id.d_summary_state_desc);
        TextView _dialogStateTitle =view.findViewById(R.id.d_summary_yapilan_islem_title);
        View _view_state_title=view.findViewById(R.id.d_summary_view_state_title);


        String name = preferencesLogin.getString("fullname",getString(R.string.verialinamadi));
        String tel =  preferencesLogin.getString("phone",getString(R.string.verialinamadi));
        String email= preferencesLogin.getString("email",getString(R.string.verialinamadi));

        String depart=getArguments().getString("dialogDepart",getString(R.string.verialinamadi));
        String description=getArguments().getString("dialogDescription",getString(R.string.verialinamadi));
        String address=getArguments().getString("dialogAddress",getString(R.string.verialinamadi));
        String addressDescription=getArguments().getString("dialogAdressDescription",getString(R.string.verialinamadi));
        state =getArguments().getString("dialogState",getString(R.string.verialinamadi));
        String stateDesc=getArguments().getString("dialogStateDesc",getString(R.string.verialinamadi));
        dialogLattitude =Double.parseDouble(getArguments().getString("dialogLattitude"));
        dialogLongtitude=Double.parseDouble(getArguments().getString("dialogLongtitude"));
        recordid = getArguments().getInt("recordid", 0);

        _dialogName.setText(name);
        _dialogTel.setText(tel);
        _dialogEmail.setText(email);
        _dialogDepart.setText(depart);
        _dialogDescription.setText(description);
        _dialogAddress.setText(address);
        _dialogAdressDescription.setText(addressDescription);
        _dialogState.setText(state);
        _dialogStateDesc.setText(stateDesc);


        if (state.equals(getString(R.string.reddedildi))) {
            _dialogStateTitle.setTextColor(mContext.getResources().getColor(R.color.RedKırmızı));
            _view_state_title.setBackgroundColor(mContext.getResources().getColor(R.color.RedKırmızı));
            _dialogState.setBackgroundColor(mContext.getResources().getColor(R.color.RedKırmızı));

        } else if (state.equals(getString(R.string.inceleniyor))) {
            _dialogStateTitle.setTextColor(mContext.getResources().getColor(R.color.İnceleSarı));
            _view_state_title.setBackgroundColor(mContext.getResources().getColor(R.color.İnceleSarı));
            _dialogState.setBackgroundColor(mContext.getResources().getColor(R.color.İnceleSarı));

        } else if (state.equals(getString(R.string.duzeltildi))) {
            _dialogStateTitle.setTextColor(mContext.getResources().getColor(R.color.DuzYesil));
            _view_state_title.setBackgroundColor(mContext.getResources().getColor(R.color.DuzYesil));
            _dialogState.setBackgroundColor(mContext.getResources().getColor(R.color.DuzYesil));
        }

    }

}
