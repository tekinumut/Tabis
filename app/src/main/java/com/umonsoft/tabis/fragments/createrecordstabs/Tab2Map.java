package com.umonsoft.tabis.fragments.createrecordstabs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.umonsoft.tabis.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Tab2Map extends Fragment implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION = 1;
    private View rootView;
    private GoogleMap mMap;
    public static Double latti = 38.6944616;
    public static Double longi = 35.5480966;
    private int alertmessage = 0;
    private int gpsdurumu = 1;
    private String address;
    private LocationManager locationManager;
    private MyLocationListener listener;


    @Override
    public void onStart() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 25);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 500, listener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 500, listener);
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 4000, 500, listener);
            }
        }
        getLocation();
        address =  "Yenidoğan Mahallesi, Pazar Caddesi No:10 38280 TALAS/KAYSERİ";
        TextView denemetext = rootView.findViewById(R.id.tab2Adres);
        denemetext.setText(address);
        super.onStart();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
            setMarker();
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = View.inflate(getActivity(),R.layout.createrecords_tab2map,null);

        Button yenile = rootView.findViewById(R.id.yenile);
        yenile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsdurumu = 1;
                alertmessage =0;
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    getLocation();
                    setMarker();
                }
            }
        });
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView mMapView = rootView.findViewById(R.id.map);
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        getLocation();
        setMarker();
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                gpsdurumu = 0;
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latti = marker.getPosition().latitude;
                longi = marker.getPosition().longitude;
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(latti, longi, 1);
                    if (addresses != null && addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                    }
                } catch (IOException ignored) {}

                TextView denemetext =rootView.findViewById(R.id.tab2Adres);
                denemetext.setText(address);
            }
        });
    }

    private void setMarker() {
        if (gpsdurumu == 1) {
            try{
                mMap.clear();

            LatLng userLocation = new LatLng(latti, longi);

            mMap.addMarker(new MarkerOptions().position(userLocation).title("setMarker").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latti, longi), 16.0f));
            }  catch (NullPointerException ignored){}
        }
    }


    private void getLocation() {


        if (Build.VERSION.SDK_INT < 23) {

    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        return;
    }
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (location != null) {
                latti = location.getLatitude();
                longi = location.getLongitude();
            } else if (location1 != null) {
                latti = location1.getLatitude();
                longi = location1.getLongitude();
            } else if (location2 != null) {
                latti = location2.getLatitude();
                longi = location2.getLongitude();
            } else {
                latti = 38.6944616;
                longi = 35.5480966;
            }
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try
            {
                addresses = geocoder.getFromLocation(latti, longi, 1);
                if (addresses != null && addresses.size() > 0)
                {
                    address = addresses.get(0).getAddressLine(0);
                }
            }
            catch (IOException ignored) {
            }
            TextView denemetext = rootView.findViewById(R.id.tab2Adres);
            denemetext.setText(address);


        } else {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {
                    latti = location.getLatitude();
                    longi = location.getLongitude();
                } else if (location1 != null) {
                    latti = location1.getLatitude();
                    longi = location1.getLongitude();
                } else if (location2 != null) {
                    latti = location2.getLatitude();
                    longi = location2.getLongitude();
                } else {
                    latti = 38.6944616;
                    longi = 35.5480966;
                }
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try
                {
                    addresses = geocoder.getFromLocation(latti, longi, 1);
                    if (addresses != null && addresses.size() > 0)
                    {
                        address = addresses.get(0).getAddressLine(0);
                    }
                }
                catch (IOException ignored) {
                }
                TextView denemetext =rootView.findViewById(R.id.tab2Adres);
                denemetext.setText(address);
            }
        }
    }

    private void buildAlertMessageNoGps() {
        if(alertmessage==0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Lütfen GPS'inizi açınız.")
                    .setCancelable(false)
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                    })
                    .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();

                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            alertmessage=1;
        }
    }

    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location) {
            getLocation();
            setMarker();
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
        @Override
        public void onProviderEnabled(String s) {
            getLocation();
            setMarker();
        }
        @Override
        public void onProviderDisabled(String s) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            }
        }
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(listener);
    }

    }

