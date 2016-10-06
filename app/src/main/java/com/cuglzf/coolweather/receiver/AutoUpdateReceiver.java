package com.cuglzf.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cuglzf.coolweather.service.AutoUpdateService;


/**
 * Created by LZF on 2016/10/6.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context , Intent intent){
        Intent i = new Intent(context , AutoUpdateService.class);
        context.startActivity(i);
    }
}
