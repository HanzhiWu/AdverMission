package com.example.advermission.FolderChooserActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;


import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.advermission.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 文件和文件夹选择器
 * 作者：yedongyang
 * 我的博客：http://blog.csdn.net/sinat_25689603
 * created by ydy on 2016/7/11 11:47
 */
public class FolderChooserActivity extends AppCompatActivity {

    ImageView titleLeft;        //标题栏左边按钮
    TextView titleText;         // 标题栏title
    ImageView titleRight;       //标题栏右边按钮
    TextView savePath;
    RecyclerView recyclerView;
    LinearLayout loading_view;

    //是否为文件夹选择器。true文件夹，false文件
    private boolean isFolderChooser = false;
    private String mimeType = "*/*";
    private String mInitialPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private FolderChooserAdapter mAdapter;
    private List<FolderChooserInfo> mData;

    private File parentFolder;
    private List<FolderChooserInfo> parentContents;
    private boolean canGoUp = true;

    private ExecutorService singleThreadExecutor;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    savePath.setText(parentFolder.getAbsolutePath());
                    mData.clear();
                    mData.addAll(getContentsArray());
                    mAdapter.notifyDataSetChanged();

                    loading_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fold_chooser);


        isFolderChooser = getIntent().getBooleanExtra("isFolderChooser", false);
        String file_path = getIntent().getStringExtra("file_path");
        singleThreadExecutor = Executors.newSingleThreadExecutor();
//        /**
//         * 数据的持续化存储
//         */
//        SharedPreferences sp = this.getSharedPreferences("dqcao", MODE_PRIVATE);
//        //获取到编辑对象
//        SharedPreferences.Editor edit = sp.edit();
//        //添加新的值，可见是键值对的形式添加
//        edit.putString("filePath", file_path);
//        edit.putInt("interval", 2000);
//        //提交.
//        edit.apply();

        mInitialPath = file_path == null ? mInitialPath : file_path;
        parentFolder = new File(mInitialPath);
        initView();
        setData();
    }

    private void setData(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                parentContents = listFiles();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void initView() {
        findViewById();
        setTitleView();
        setRecyclerView();
        savePath.setText(parentFolder.getAbsolutePath());
    }

    //设置标题栏
    private void setTitleView() {
        titleText.setText("请选择目录");
        titleLeft.setVisibility(View.VISIBLE);
        titleRight.setVisibility(View.VISIBLE);
        titleLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        titleRight.setImageResource(R.mipmap.ic_save_white_24dp);

        titleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderChooserActivity.this.finish();
            }
        });
        titleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooserEnd();
            }
        });
    }

    private void setRecyclerView() {
        mData = new ArrayList<>();
        //设置布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        //分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //设置适配器
        mAdapter = new FolderChooserAdapter(this, mData, new ItemClickCallback() {
            @Override
            public void onClick(View view, int position, FolderChooserInfo info) {
                onSelection(view, position, info);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private List<FolderChooserInfo> getContentsArray() {
        List<FolderChooserInfo> results = new ArrayList<>();
        if (parentContents == null) {
            if (canGoUp){
                FolderChooserInfo info = new FolderChooserInfo();
                info.setName("...");
                info.setFile(null);
                info.setImage(R.mipmap.back);
                results.add(info);
            }
            return results;
        }
        if (canGoUp){
            FolderChooserInfo info = new FolderChooserInfo();
            info.setName("...");
            info.setFile(null);
            info.setImage(R.mipmap.back);
            results.add(info);
        }
        results.addAll(parentContents);
        return results;
    }

    public void onSelection( View view, int position, FolderChooserInfo info) {
        if (canGoUp && position == 0) {
            if (parentFolder.isFile()) {
                parentFolder = parentFolder.getParentFile();
            }
            parentFolder = parentFolder.getParentFile();
            canGoUp = parentFolder.getParent() != null;
        } else {
            parentFolder = info.getFile();
            canGoUp = true;
        }
        if (parentFolder.isFile()) {
            ChooserEnd();
        }else{
            loading_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setData();
        }
    }

    private List<FolderChooserInfo> listFiles() {
        File[] contents = parentFolder.listFiles();
        List<FolderChooserInfo> results = new ArrayList<>();
        if (contents != null) {
            for (File fi : contents) {
                if (fi.isDirectory()){
                    FolderChooserInfo info = new FolderChooserInfo();
                    info.setName(fi.getName());
                    info.setFile(fi);
                    info.setImage(fileType(fi));
                    results.add(info);
                }
            }
            Collections.sort(results, new FolderSorter());
            return results;
        }
        return null;
    }


    private static class FolderSorter implements Comparator<FolderChooserInfo> {
        @Override
        public int compare(FolderChooserInfo lhs, FolderChooserInfo rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public interface ItemClickCallback{
        void onClick(View view, int position, FolderChooserInfo info);
    }

    /**
     * 为界面中的所有控件初始化
     */
    private void findViewById(){
        titleLeft = (ImageView) findViewById(R.id.title_left);
        titleRight = (ImageView) findViewById(R.id.title_right);
        titleText = (TextView) findViewById(R.id.title_text);
        savePath = (TextView) findViewById(R.id.save_path);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        loading_view = (LinearLayout) findViewById(R.id.loading_view);
    }

    private void ChooserEnd(){
        File result = parentFolder;
        Intent intent = new Intent();
        intent.putExtra("file_path", result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private int fileType(File file){
        int image = R.mipmap.type_file;
        if(file.isDirectory()){
            image = R.mipmap.type_folder;
        }else{
            try {
//            指定文件类型的图标
                String[] token = file.getName().split("\\.");
                String suffix = token[token.length - 1];
                if (suffix.equalsIgnoreCase("txt")) {
                    image = R.mipmap.type_txt;
                } else if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("gif")) {
                    image = R.mipmap.type_image;
                } else if (suffix.equalsIgnoreCase("mp3")) {
                    image = R.mipmap.type_mp3;
                } else if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("rmvb") || suffix.equalsIgnoreCase("avi")) {
                    image = R.mipmap.type_video;
                } else if (suffix.equalsIgnoreCase("apk")) {
                    image = R.mipmap.type_apk;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return image;
    }
}
