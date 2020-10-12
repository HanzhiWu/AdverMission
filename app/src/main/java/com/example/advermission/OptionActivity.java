package com.example.advermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

public class OptionActivity extends AppCompatActivity {

    private Banner mBanner;
    private ArrayList<String> images;
    private String filePath;
    private int time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_option );
        mBanner = findViewById( R.id.banner );
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent=new Intent(OptionActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        initData();
        initView();
    }
    private void initView() {
        mBanner = findViewById( R.id.banner );
        //设置样式,默认为:Banner.NOT_INDICATOR(不显示指示器和标题)
        //1. Banner.CIRCLE_INDICATOR    显示圆形指示器
        mBanner.setBannerStyle( BannerConfig.NOT_INDICATOR );
        mBanner.setImageLoader( new OptionActivity.GlideImageLoader() );
        mBanner.setViewPagerIsScroll( true );
        mBanner.isAutoPlay( true );
        mBanner.setDelayTime( time*1000 );
        mBanner.setImages( images )
                .start();
    }


    private void initData() {
        //设置图片资源:url或本地资源
        images = new ArrayList<>();
        SharepreferenceUtils sharepreferenceUtils = new SharepreferenceUtils(OptionActivity.this, "picInfo");
        filePath = sharepreferenceUtils.getFilePath();
        time = sharepreferenceUtils.getTime();
        if (filePath == null || time == 0) {
            Toast.makeText(this, "文件路径为空或者是时间为空，请回主页面重新设置", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OptionActivity.this, MainActivity.class);
            startActivity(intent);
        }
        if (ContextCompat.checkSelfPermission(OptionActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OptionActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            String selection = MediaStore.Images.Media.DATA + " like ?";
            String[] selectionArgs = {filePath + "%"};
            Cursor mCursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), null,
                    selection, selectionArgs, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    Uri uri = getUriFromPath(filePath + "/" + displayName);
//                    try{
//                        if(uri!=null)
//                        images.add(uri.toString());
//                        else{
//                            Resources.NotFoundException e = new Resources.NotFoundException();
//                            throw e;
//                        }
//                    }catch (Resources.NotFoundException e){
//                        Toast.makeText(OptionActivity.this,"没找到对应资源",Toast.LENGTH_SHORT).show();
//                    }
                    if(uri==null){
                        continue;
                    }else{
                        images.add(uri.toString());
                    }

                }
            } else {
                Log.i("Error", "-->mCursor is null");
            }
        }
    }
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with( context )
                    .load( (String) path )
                    .into( imageView );
        }

    }
    private Uri getUriFromPath(String filePath){
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else
            return null;
    }
}
