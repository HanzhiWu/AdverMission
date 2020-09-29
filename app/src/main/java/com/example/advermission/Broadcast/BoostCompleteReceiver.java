package com.example.advermission.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.advermission.MainActivity;

/**
 * 使用广播实现开机自启动
 */
public class BoostCompleteReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Wmx logs::", intent.getAction());
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();

        /**
         * 如果 系统 启动的消息，则启动 APP 主页活动
         */
        if (intent.getAction() .equals(Intent.ACTION_BOOT_COMPLETED) ) {
            Intent intentMainActivity = new Intent(context, MainActivity.class);
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();

        }
    }
}
