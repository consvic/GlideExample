package com.example.cocoy.glideexample;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private final int RESULT_LOAD_IMAGE = 100;

    int day=1,month=8,year=2017;

    /** The images. */
    private ArrayList<String> images;
    GridView gallery;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = (GridView) findViewById(R.id.galleryGridView);
        requestPermission(); // Code for permission
    }

    private void requestPermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //debemos mostrar un mensaje
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //mostramos una explicacind eque no acepto dar permiso para acceder a la libreria

            } else  {
                //pedimos permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE);

            }
        } else {
            Log.e("value", "Permission Granted, Now you can use local drive .");
            gallery.setAdapter(new ImageAdapter(this));

            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    gallery.setAdapter(new ImageAdapter(this));

                    gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {

                        }
                    });
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    /**
     * The Class ImageAdapter.
     */
    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams(300, 300));

            } else {
                picturesView = (ImageView) convertView;
            }

            Log.d("position: ", position+"");
            GlideApp.with(context).load(images.get(position))
                    .placeholder(R.mipmap.ic_launcher).centerCrop()
                    .into(picturesView);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity
         *            the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            //TODO: DATES
            String stringDate = "2017:08:01 00:00:00";
            DateFormat format = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            Date dateInit, dateFirst;

            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name, column_index_date;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;

            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            //uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            File file = new File("/storage/emulated/0/DCIM/");
            Log.d("check if folder exists:",file.exists() + "");
            //uri = Uri.fromFile(file);
            //uri = MediaStore.Images.Media.getContentUri("internal");
            Log.d("uri: ", uri.toString());

            String[] projection = { MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            //column_index_date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            //TODO: INITIAL DATE August 1,2017

            while (cursor.moveToNext()) {
                String folder = cursor.getString(column_index_folder_name);
                if(folder.contains("CAMERA") || folder.contains("camera") || folder.contains("100MEDIA")) {
                    absolutePathOfImage = cursor.getString(column_index_data);
                    //Log.d("folder name: ", cursor.getString(column_index_folder_name));
                    String datemeta = getExifDate(absolutePathOfImage)+"";
                    //Log.i("dated: ",datemeta);

                    try {
                        //fecha foto
                        dateInit = format.parse(datemeta);
                        //fecha a comparar - 2017:08:01
                        dateFirst = format.parse(stringDate);
                        if(dateInit.after(dateFirst))
                            listOfAllImages.add(absolutePathOfImage);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                /*Log.d("date: ", cursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN) + "of photo" + cursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME));*/
                    //getExifDate(absolutePathOfImage);

                }
            }
            //listOfAllImages.add("/storage/emulated/0/DCIM/100MEDIA/IMAG0882.jpg");
            //Log.d("list of images: ", listOfAllImages.size()+"");
            ArrayList<String> temp = new ArrayList<>();

            for(int i = listOfAllImages.size(); i>0 ;i--) {
                temp.add(listOfAllImages.get(i-1));
            }
            return temp;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public String getExifDate(String filePath) {
        ExifInterface intf = null;
        String dateString ="";
        try {
            intf = new ExifInterface(filePath);
            if (intf != null) {
                dateString = intf.getAttribute(ExifInterface.TAG_DATETIME);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(dateString == "") {
                dateString = "2017:01:01 12:00:00";
            }
        }

        /*if (intf == null) {
            Date lastModDate = new Date(file.lastModified());
            Log.d("Dated : ", lastModDate.toString());//Dispaly lastModDate. You can do/use it your own way
        }*/

        return dateString;
    }

}
