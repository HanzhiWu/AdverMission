package com.example.advermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.advermission.FolderChooserActivity.FolderChooserActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button choose_button,play_button;
    EditText fileRoot,time_interval;
    String filePath;
    int time;
    private static final int REQUEST_FOLDER = 1;
    private static final int REQUEST_FILE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileRoot=findViewById(R.id.fileroot);
        time_interval=findViewById(R.id.time_interval);
        choose_button=findViewById(R.id.choose_button);
        play_button=findViewById(R.id.playbutton);
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);}
        choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, FolderChooserActivity.class);
                intent.putExtra("isFolderChooser", true);
                intent.putExtra("file_path", Environment.getRootDirectory().getAbsolutePath() );
                startActivityForResult(intent, REQUEST_FOLDER);
            }
        });
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fileRoot.length()==0){
                        Toast.makeText(MainActivity.this,"文件路径不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        String filepath=fileRoot.getText().toString();
                        if(time_interval.length()==0){
                            Toast.makeText(MainActivity.this,"时间不能为空",Toast.LENGTH_SHORT).show();
                        }else{
                            int time=new Integer(time_interval.getText().toString()).intValue();
                            //Toast.makeText(MainActivity.this,"文件路径为："+filepath+" 时间间隔为："+time+"秒",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(MainActivity.this, OptionActivity.class);
                            SharepreferenceUtils sharepreferenceUtils=new SharepreferenceUtils(MainActivity.this,"picInfo");
                            sharepreferenceUtils.edit(filepath,time);
                            startActivity(intent);
                        }
                    }
                }
            });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_FOLDER:
                    File folder = (File) data.getSerializableExtra("file_path");
                    Toast.makeText(MainActivity.this, "已选择：" + folder.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    fileRoot=findViewById(R.id.fileroot);
                    fileRoot.setText(folder.getAbsolutePath());
                    break;
                case REQUEST_FILE:
                    File file = (File) data.getSerializableExtra("file_path");
                    Toast.makeText(MainActivity.this, "已选择：" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}