package com.umonsoft.tabis.Abstracts;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.umonsoft.tabis.R;

public abstract class AdapterValues {




    @SuppressLint("ClickableViewAccessibility")
    public static void loadTouch(View mView, final SharedPreferences.Editor editorKarisikDegerler){


        final Button _reddedildi=mView.findViewById(R.id.dialogReddedildiButton);
        final Button    _inceleniyor=mView.findViewById(R.id.dialogInceleniyorButton);
        final Button    _duzeltildi=mView.findViewById(R.id.dialogDuzeltildiButton);
        final LinearLayout _linearLayout= mView.findViewById(R.id.choosestate_linearlayoutofimage);

        _inceleniyor.setBackgroundColor(Color.parseColor("#FF1B6DBF"));
        editorKarisikDegerler.putString("stategonder","2").apply();

        _reddedildi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                editorKarisikDegerler.putString("stategonder","1").apply();
                _reddedildi.setBackgroundColor(Color.parseColor("#FF1B6DBF"));
                _inceleniyor.setBackgroundColor(Color.parseColor("#ffffffff"));
                _duzeltildi.setBackgroundColor(Color.parseColor("#ffffffff"));

                _linearLayout.setVisibility(View.VISIBLE);

                return true;
            }
        });

        _inceleniyor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editorKarisikDegerler.putString("stategonder","2").apply();
                _reddedildi.setBackgroundColor(Color.parseColor("#ffffffff"));
                _inceleniyor.setBackgroundColor(Color.parseColor("#FF1B6DBF"));
                _duzeltildi.setBackgroundColor(Color.parseColor("#ffffffff"));

                _linearLayout.setVisibility(View.GONE);

                return true;
            }
        });

        _duzeltildi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                editorKarisikDegerler.putString("stategonder","3").apply();
                _reddedildi.setBackgroundColor(Color.parseColor("#ffffffff"));
                _inceleniyor.setBackgroundColor(Color.parseColor("#ffffffff"));
                _duzeltildi.setBackgroundColor(Color.parseColor("#FF1B6DBF"));

                _linearLayout.setVisibility(View.VISIBLE);

                return true;
            }
        });
    }




}
