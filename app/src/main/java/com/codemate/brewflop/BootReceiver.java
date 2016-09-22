package com.codemate.brewflop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by iiro on 22.9.2016.
 */
public class BootReceiver extends BroadcastReceiver {
    DayCounter dayCounter;

    @Override
    public void onReceive(Context context, Intent intent) {
        // For testing
        if (dayCounter == null) {
            dayCounter = new DayCounter(context);
        }

        dayCounter.setAlarmIfNotExists();
    }
}
