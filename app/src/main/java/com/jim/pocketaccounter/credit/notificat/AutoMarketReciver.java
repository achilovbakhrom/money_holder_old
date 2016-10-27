package com.jim.pocketaccounter.credit.notificat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by root on 9/20/16.
 */
public class AutoMarketReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "ishladi", Toast.LENGTH_SHORT).show();
        intent = new Intent(context, AutoMarketService.class);
        context.startService(intent);
    }
}
