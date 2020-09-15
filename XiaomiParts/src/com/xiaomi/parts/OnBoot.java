package com.xiaomi.parts;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SELinux;
import android.util.Log;
import android.widget.Toast;

import com.xiaomi.parts.R;

import java.io.IOException;
import java.util.List;

public class OnBoot extends BroadcastReceiver {

    private Context settingsContext = null;
    private static final String TAG = "SettingsOnBoot";
    private boolean mSetupRunning = false;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos =
                activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++) {
            if(procInfos.get(i).processName.equals("com.google.android.setupwizard")) {
                mSetupRunning = true;
            }
        }

        if(!mSetupRunning) {
            try {
                settingsContext = context.createPackageContext("com.xiaomi.parts", 0);
            } catch (Exception e) {
                Log.e(TAG, "Package not found", e);
            }
        }
    }

    private void showToast(String toastString, Context context) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT)
                .show();
    }
}
