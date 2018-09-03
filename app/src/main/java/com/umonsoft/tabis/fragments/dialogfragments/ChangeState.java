package com.umonsoft.tabis.fragments.dialogfragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.umonsoft.tabis.Abstracts.AdapterValues;
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

public class ChangeState extends DialogFragment {

    private final int CODE_GALLERY_REQUEST=999;
    private final int CODE_TAKEPICTURE_REQUEST = 998;
    private final int REQUEST_EXTERNAL_STORAGE_RESULT=997;
    private SharedPreferences preferencesLogin,preferencesKarisikDegerler;
    private SharedPreferences.Editor editorKarisikDegerler;
    private ArrayList<Uri> path = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private String mImageFileLocation="";
    private ImageView imageupload;
    private Bitmap bitmap;
    private String record_id;
    private Context mContext;
    private HelperMethods helperMethods;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View mView = View.inflate(mContext, R.layout.dialog_choosestate,null);

        final PhpValues phpValues=new PhpValues();
        preferencesLogin = mContext.getSharedPreferences(mContext.getString(R.string.loginvalues), Context.MODE_PRIVATE);
        preferencesKarisikDegerler=mContext.getSharedPreferences(mContext.getString(R.string.karisikdegerlervalues), Context.MODE_PRIVATE);
        editorKarisikDegerler=preferencesKarisikDegerler.edit(); editorKarisikDegerler.apply();
        helperMethods = new HelperMethods(mContext);

        AdapterValues.loadTouch(mView,editorKarisikDegerler);
        editorKarisikDegerler.remove("imageValue").apply();

        Button getgallery=mView.findViewById(R.id.d_seccamera_galeri_state);
        Button getphoto=mView.findViewById(R.id.d_seccamera_photo_state);
        imageupload =mView.findViewById(R.id.imageuploadstate);
        recyclerView= mView.findViewById(R.id.tab3imageRecyclerView);

        if (getArguments() != null) {
            record_id=getArguments().getString("id","0");
        }

        getgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},CODE_GALLERY_REQUEST);

            }
        });

        getphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        final String loginemail =preferencesLogin.getString("email","NA");
                        final int    loginid    =preferencesLogin.getInt("user_id",-1);

                        dialog.dismiss();
                        helperMethods.ShowProgressDialog(getString(R.string.gonderiliyor));

                        EditText _stateDesc = mView.findViewById(R.id.dialogStateDescEdittext);
                        String   choosenstatevalue = preferencesKarisikDegerler.getString("stategonder", "2");
                        String sqlcodeState = "UPDATE records SET state= '" + choosenstatevalue + "', " +
                                "statedesc=? WHERE id= " + record_id;

                        phpValues.sentItem(mContext, sqlcodeState,_stateDesc.getText().toString(), "stategonder", null);

                        String sqlcodeHistoryState = "Insert into statechangehistory (user_id,record_id,prevstate,nextstate,description) VALUES " +
                                "( " + preferencesLogin.getInt("user_id", 0) + ", " + record_id +
                                " ,(SELECT id from state where name = '" + getArguments().getString("state",getString(R.string.verialinamadi)) + "'), " +choosenstatevalue +
                                " ,?) ";

                        phpValues.sentItem(mContext, sqlcodeHistoryState,_stateDesc.getText().toString(),null, null);


                        final StringBuilder sqlcode = new StringBuilder("");

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

                                                sqlcode.append("INSERT INTO recordimages (record_id,image,type) VALUES (").append(record_id).append(",'").append(imagenamesql).append("',2); ");

                                                if (bitmap != null) {
                                                    imageData = imageToString(bitmap);
                                                } else {
                                                    imageData = getString(R.string.file_noimage);
                                                }

                                                if (i < path.size() - 1)
                                                    new PhpValues().sendRecords(mContext, imageData, "Select 2", targetnamesql, null);

                                                if (i == path.size() - 1) {
                                                    new PhpValues().sendRecords(mContext, imageData, String.valueOf(sqlcode), targetnamesql, new VolleyGet1ParameterWithError() {
                                                        @Override
                                                        public void onSuccess(String response) {
                                                            ((Homepage)mContext).recreate();
                                                            helperMethods.HideProgressDialog();
                                                        }

                                                        @Override
                                                        public void onError(String error) {
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
                                sqlcode.append("INSERT INTO recordimages (record_id,image,type) VALUES (@LASTID,'").append(imagenamesql).append("',2); ");

                                new PhpValues().sendRecords(mContext, imageData, String.valueOf(sqlcode), targetnamesql, new VolleyGet1ParameterWithError() {
                                    @Override
                                    public void onSuccess(String response) {
                                        ((Homepage)mContext).recreate();
                                        helperMethods.HideProgressDialog();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        helperMethods.HideProgressDialog();
                                    }
                                });

                                break;

                            default:

                                ((Homepage)mContext).recreate();

                              helperMethods.HideProgressDialog();
                                break;
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Hayıra tıklanınca
                        dialog.dismiss();
                        break;
                }

            }
        };

        return new AlertDialog.Builder(mContext)
                .setTitle("Kayıt Durumu Değiştir")
                .setView(mView)
                .setPositiveButton(getString(R.string.dialog_evet),listener)
                .setNegativeButton(getString(R.string.dialog_hayir),listener)
                .create();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                FishBun.with(ChangeState.this)
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
            }

            imageAdapter.changePath(path);
            // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
            editorKarisikDegerler.putString("imageValue","gallery").apply();

        }


        else if (requestCode == CODE_TAKEPICTURE_REQUEST && resultCode == Activity.RESULT_OK) { //Eğer fotoğraf çekip onay verdiyse.

            recyclerView.setAdapter(null);
            recyclerView.setVisibility(View.GONE);
            setReducedImageSize(mImageFileLocation);
            imageupload.setImageBitmap(bitmap);
            editorKarisikDegerler.putString("imageValue","camera").apply();
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
        mContext = context;
    }
}
