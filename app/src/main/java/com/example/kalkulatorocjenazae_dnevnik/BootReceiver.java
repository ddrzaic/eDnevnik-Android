package com.example.kalkulatorocjenazae_dnevnik;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class BootReceiver extends BroadcastReceiver {
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            PeriodicWorkRequest.Builder WorkBuilder =
                    new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15,
                            TimeUnit.MINUTES);

            PeriodicWorkRequest myWork = WorkBuilder.build();
            WorkManager.getInstance().enqueue(myWork);
        }
    }
}
