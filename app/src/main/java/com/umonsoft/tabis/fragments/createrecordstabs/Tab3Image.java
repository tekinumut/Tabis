package com.umonsoft.tabis.fragments.createrecordstabs;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.umonsoft.tabis.HelperClasses.HelperMethods;
import com.umonsoft.tabis.HelperClasses.ImageController;
import com.umonsoft.tabis.Interfaces.VolleyGet1ParameterWithError;
import com.umonsoft.tabis.R;
import com.umonsoft.tabis.activities.Homepage;
import com.umonsoft.tabis.adapter.ImageAdapter;
import com.umonsoft.tabis.phpvalues.PhpValues;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Tab3Image extends Fragment {

    private Button btnupload;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Bitmap bitmap;
    private ImageView imageupload;
    private String mImageFileLocation = "";
    private final int CODE_GALLERY_REQUEST=999;
    private final int CODE_TAKEPICTURE_REQUEST = 998;
    private final int REQUEST_EXTERNAL_STORAGE_RESULT=997;
    private SharedPreferences preferencesLogin,preferencesKarisikDegerler;
    private SharedPreferences.Editor editorKarisikDegerler;
    private ArrayList<Uri> path = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private HelperMethods helperMethods;

    private Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = View.inflate(mContext, R.layout.createrecords_tab3image, null);

        final Button btnchoose = rootView.findViewById(R.id.buttonpick);
        btnupload= rootView.findViewById(R.id.buttonupload);
        imageupload= rootView.findViewById(R.id.imageupload);
        recyclerView= rootView.findViewById(R.id.tab3imageRecyclerView);

        preferencesLogin = mContext.getSharedPreferences(getString(R.string.loginvalues), Context.MODE_PRIVATE);
        preferencesKarisikDegerler=mContext.getSharedPreferences(mContext.getString(R.string.karisikdegerlervalues), Context.MODE_PRIVATE);
        editorKarisikDegerler=preferencesKarisikDegerler.edit(); editorKarisikDegerler.apply();
        editorKarisikDegerler.remove("imageValue").apply();
        helperMethods=new HelperMethods(mContext);

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnupload.setEnabled(false);

                helperMethods.ShowProgressDialog(getString(R.string.gonderiliyor));
                //------------------------------------- sending values -------------------------------------

                final String loginemail =preferencesLogin.getString("email","NA");
                final int    loginid    =preferencesLogin.getInt("user_id",-1);


                 EditText _tab1description =getActivity().findViewById(R.id.tab1description);
                 Spinner _tab1spinnerdepart =getActivity().findViewById(R.id.tab1spinnerdepart);
                 TextView _tab2adres=getActivity().findViewById(R.id.tab2Adres);
                 EditText _tab2adrestarif =getActivity().findViewById(R.id.tab2AdresTarif);

                 String tab1description = _tab1description.getText().toString().replace("'", "\\'").replace("\"","\\\"");
                 String tab2adrestarif = _tab2adrestarif.getText().toString().replace("'", "\\'").replace("\"","\\\"");


                final StringBuilder sqlcode = new StringBuilder("INSERT INTO records (user_id,department,description,address,addressdesc,lattitude,longitude) VALUES " +
                        "(" + String.valueOf(loginid) + ",(SELECT id from departments where name = '" + _tab1spinnerdepart.getSelectedItem().toString() + "'),'" +tab1description + "'" +
                        ",'" + _tab2adres.getText() + "','" + tab2adrestarif + "','" + String.valueOf(Tab2Map.latti) + "','" + String.valueOf(Tab2Map.longi) + "'); " +
                        "SET @LASTID=LAST_INSERT_ID(); ");

                switch (preferencesKarisikDegerler.getString("imageValue", "null")) {
                    case "gallery":

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                for (int i = 0; i < path.size(); i++) {
                                    try {
                                        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

                                        final String filename;
                                        String imageData;

                                        if (loginemail.length() >= 5)
                                            filename = "" + loginemail.substring(0, 4) + "_" + loginid + "_" + timeStamp + ".jpeg";
                                        else
                                            filename = "" + loginemail + "_" + loginid + "_" + timeStamp + ".jpeg";

                                        String imagenamesql = "http://android.umonsoft.com/pictures/" + filename;
                                        String targetnamesql = "/home/www/android.umonsoft.com/pictures/" + filename;

                                        File finalFile = new File(getRealPathFromURI(path.get(i)));
                                        setReducedImageSize(finalFile.getAbsolutePath());

                                        sqlcode.append("INSERT INTO recordimages (record_id,image,type) VALUES (@LASTID,'").append(imagenamesql).append("',1); ");

                                        if (bitmap != null) {
                                            imageData = imageToString(bitmap);
                                        } else {
                                            imageData = getString(R.string.file_noimage);
                                        }

                                        if (i < path.size() - 1)
                                            new PhpValues().sendRecords(mContext, imageData, "Select 1", targetnamesql, null);

                                        if (i == path.size() - 1) {
                                            new PhpValues().sendRecords(mContext, imageData, String.valueOf(sqlcode), targetnamesql, new VolleyGet1ParameterWithError() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    Toast.makeText(mContext, getString(R.string.kayitgonderildi), Toast.LENGTH_LONG).show();
                                                    helperMethods.HideProgressDialog();
                                                    Intent intent = new Intent(mContext, Homepage.class);
                                                    intent.putExtra(getString(R.string.opensecondtab), 1);
                                                    startActivity(intent);
                                                    getActivity().finish();
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Toast.makeText(mContext, getString(R.string.kayitgonderilemedi), Toast.LENGTH_SHORT).show();
                                                    btnupload.setEnabled(true);
                                                    helperMethods.HideProgressDialog();
                                                }
                                            });
                                        }
                                        Thread.sleep(1001);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        break;
                    case "camera":
                        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

                        final String filename;
                        String imageData;

                        if (loginemail.length() >= 5)
                            filename = "" + loginemail.substring(0, 4) + "_" + loginid + "_" + timeStamp + ".jpeg";
                        else
                            filename = "" + loginemail + "_" + loginid + "_" + timeStamp + ".jpeg";

                        if (bitmap != null) {
                            imageData = imageToString(bitmap);
                        } else {
                            imageData = getString(R.string.file_noimage);
                        }

                        String imagenamesql = "http://android.umonsoft.com/pictures/" + filename;
                        String targetnamesql = "/home/www/android.umonsoft.com/pictures/" + filename;
                        sqlcode.append("INSERT INTO recordimages (record_id,image,type) VALUES (@LASTID,'").append(imagenamesql).append("',1); ");

                        new PhpValues().sendRecords(mContext, imageData, String.valueOf(sqlcode), targetnamesql, new VolleyGet1ParameterWithError() {
                            @Override
                            public void onSuccess(String response) {
                                Toast.makeText(mContext, getString(R.string.kayitgonderildi), Toast.LENGTH_LONG).show();
                                helperMethods.HideProgressDialog();
                                Intent intent = new Intent(mContext, Homepage.class);
                                intent.putExtra(getString(R.string.opensecondtab), 1);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(mContext, getString(R.string.kayitgonderilemedi), Toast.LENGTH_SHORT).show();
                                btnupload.setEnabled(true);
                                helperMethods.HideProgressDialog();
                            }
                        });

                        break;
                    default:

                        new PhpValues().sendRecords(mContext, getString(R.string.file_noimage), String.valueOf(sqlcode), "null", new VolleyGet1ParameterWithError() {
                            @Override
                            public void onSuccess(String response) {
                                Toast.makeText(mContext, getString(R.string.kayitgonderildi), Toast.LENGTH_LONG).show();
                                helperMethods.HideProgressDialog();
                                Intent intent = new Intent(mContext, Homepage.class);
                                intent.putExtra(getString(R.string.opensecondtab), 1);
                                startActivity(intent);
                                getActivity().finish();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(mContext, getString(R.string.kayitgonderilemedi), Toast.LENGTH_SHORT).show();
                                btnupload.setEnabled(true);
                                helperMethods.HideProgressDialog();
                            }
                        });
                        break;
                }

            }
        });

        btnchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnchoose.setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnchoose.setClickable(true);
                    }
                },300);

                View mView =View.inflate(mContext,R.layout.dialog_chooseimagemethod,null);

                final Button getgallery=mView.findViewById(R.id.d_seccamera_galeri);
                final Button getphoto=mView.findViewById(R.id.d_seccamera_photo);
                builder=new AlertDialog.Builder(mContext);

                builder.setView(mView);

                getgallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getgallery.setClickable(false);
                        getgallery.setClickable(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getgallery.setClickable(true);
                            }
                        },300);

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);

                    }
                });

                getphoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        getphoto.setClickable(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getphoto.setClickable(true);
                            }
                        },300);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(mContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                                    mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                                callCameraApp();    //eğer zaten onay verilmişse
                            } else {
                                // onay verilmemişse izin iste
                                String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissionRequest, REQUEST_EXTERNAL_STORAGE_RESULT);
                            }
                        }else{
                            callCameraApp2();
                        }

                    }
                });

                builder.setNegativeButton(getString(R.string.dialog_iptalet), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });

                dialog=builder.create();
                dialog.show();

            }
        });

        return rootView;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                FishBun.with(Tab3Image.this)
                        .setImageAdapter(new GlideAdapter())
                        .setIsUseDetailView(false)
                        .setMaxCount(7)
                        .setMinCount(1)
                        .setPickerSpanCount(7)
                        .setActionBarColor(Color.parseColor("#795548"), Color.parseColor("#5D4037"), false)
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        //.setArrayPaths(path)
                        .setAlbumSpanCount(2, 4)
                        .setButtonInAlbumActivity(false)
                        .setCamera(false)
                        .setReachLimitAutomaticClose(false)
                        //.setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_custom_back_white))
                        //.setOkButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_custom_ok))
                        .setAllViewTitle("Tüm Resimler")
                        .setActionBarTitle("Resimler")
                        .textOnImagesSelectionLimitReached("Seçim limitini geçtiniz!")
                        .textOnNothingSelected("Bir şey seçilmedi.")
                        .startAlbum();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                ImageController mainController = new ImageController(mContext, imageupload);
                imageAdapter = new ImageAdapter(mContext, mainController, path);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(imageAdapter);


            } else {
                Toast.makeText(mContext, getString(R.string.galerierisimreddi), Toast.LENGTH_SHORT).show();
            }
        }

        else if (requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            // we have heard back from our request for camera and write external storage.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                callCameraApp();
            } else {
                Toast.makeText(mContext,getString(R.string.izinalinamadi), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        editorKarisikDegerler.remove("imageValue").apply();
        if (requestCode == Define.ALBUM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
            // you can get an image path(ArrayList<String>) on <0.6.2

            path = data.getParcelableArrayListExtra(Define.INTENT_PATH);

            if(path.size()>0) //bir veya daha fazla resim seçilirse.
            {
                recyclerView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }

            imageAdapter.changePath(path);
            // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
            editorKarisikDegerler.putString("imageValue","gallery").apply();

            dialog.dismiss();
            }


        else if (requestCode == CODE_TAKEPICTURE_REQUEST && resultCode == Activity.RESULT_OK) { //Eğer fotoğraf çekip onay verdiyse.

            recyclerView.setAdapter(null);
            recyclerView.setVisibility(View.GONE);
            setReducedImageSize(mImageFileLocation);
            imageupload.setImageBitmap(bitmap);
            editorKarisikDegerler.putString("imageValue","camera").apply();
            dialog.dismiss();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private  String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        return Base64.encodeToString(imageBytes,Base64.DEFAULT);
    }

    private void callCameraApp(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String authorities= mContext.getApplicationContext().getPackageName() +".fileprovider";
        Uri imageUri = null;
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(mContext,authorities,photoFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);

        startActivityForResult(intent, CODE_TAKEPICTURE_REQUEST);
    }

    private void callCameraApp2(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);

        startActivityForResult(intent, CODE_TAKEPICTURE_REQUEST);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "pic_" + timeStamp;
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();
        return image;

    }

    private void setReducedImageSize(String value) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(value, bmOptions);

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, 512, 384);
        bmOptions.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(value, bmOptions);
        //resim çektikten sonra
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String cursorString =cursor.getString(idx);
        cursor.close();
        return cursorString;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }




 /*   private void sendRecordsAsOkHttp()
    {
        final String loginemail =preferencesLogin.getString("email","NA");
        final int    loginid    =preferencesLogin.getInt("user_id",0);

        EditText _tab1description =getActivity().findViewById(R.id.tab1description);
        Spinner _tab1spinnerdepart =getActivity().findViewById(R.id.tab1spinnerdepart);
        TextView _tab2adres=getActivity().findViewById(R.id.tab2Adres);
        EditText _tab2adrestarif =getActivity().findViewById(R.id.tab2AdresTarif);


        String imageData;
        if(bitmap!=null) {
            imageData = imageToString(bitmap);
        }else{
            imageData="NULL";
        }

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
        String filename;

        if(loginemail.length()>=5)
             filename = ""+loginemail.substring(0,4)+"_"+loginid+"_"+timeStamp+".jpeg";
        else
             filename = ""+loginemail+"_"+loginid+"_"+timeStamp+".jpeg";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .build();
        //load the okhttp library class and create is object

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id",String.valueOf(loginid))
                .addFormDataPart("department",_tab1spinnerdepart.getSelectedItem().toString())
                .addFormDataPart("description",_tab1description.getText().toString())
                .addFormDataPart("address",_tab2adres.getText().toString())
                .addFormDataPart("addressdesc",_tab2adrestarif.getText().toString())
                .addFormDataPart("lattitude",""+Tab2Map.latti)
                .addFormDataPart("longitude",""+Tab2Map.longi)
                .addFormDataPart("image",imageData)
                .addFormDataPart("state","2")
                .addFormDataPart("filename",filename)
                .build();

        Request request  = new Request.Builder()
                .url(getString(R.string.php_sentrecords))
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override

            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(mContext, getString(R.string.kayitgonderilemedi), Toast.LENGTH_SHORT).show();
                btnupload.setEnabled(true);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call call,@NonNull Response response) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(mContext,getString(R.string.kayitgonderildi), Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    Intent intent =new Intent(mContext,Homepage.class);
                                    intent.putExtra(getString(R.string.opensecondtab),1);
                                    startActivity(intent);
                                    getActivity().finish();

                                }
                            });

            }
        });
    }

*/

}
