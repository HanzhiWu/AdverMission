package com.example.advermission;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharepreferenceUtils {
    private Context context;
    private SharedPreferences sharedPreferences;
    public SharepreferenceUtils(Context context,String name) {
        sharedPreferences = context.getSharedPreferences(name,MODE_PRIVATE);
    }
    public void edit(String filePath,int time_interval){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("filePath",filePath);
        editor.putInt("time_interval",time_interval);
        editor.apply();
    }
    public String getFilePath(){
        if(sharedPreferences.contains("filePath"))
            return sharedPreferences.getString("filePath",null);
        else
            return "/storage/emulated/0/";
    }
    public int getTime(){
        if(sharedPreferences.contains("time_interval"))
            return sharedPreferences.getInt("time_interval",0);
        else
            return 0;
    }
}
