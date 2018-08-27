package com.example.ready.studytimemanagement.presenter.Controller;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.ready.studytimemanagement.presenter.Activity.BaseActivity;
import com.example.ready.studytimemanagement.presenter.Adapter.AdapterApplock;
import com.example.ready.studytimemanagement.presenter.AppLockList;
import com.example.ready.studytimemanagement.presenter.Item.ItemApplock;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppLockController extends BaseActivity {
    PackageManager pkgm;
    List<ResolveInfo> AppInfos;

    public ArrayList<ItemApplock> LoadAppList(Activity act){

        ArrayList<ItemApplock> itemApplocks = new ArrayList<ItemApplock>();

        pkgm = act.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        AppInfos = pkgm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : AppInfos) {
            ActivityInfo ai = info.activityInfo;
            //Log.d("APP TITLE", ai.loadLabel(pkgm).toString());
            //Log.d("APP Package Name", ai.packageName);
            //Log.d("APP Class Name", ai.name);
            itemApplocks.add(new ItemApplock(ai.loadLabel(pkgm).toString(),ai.loadIcon(pkgm)));
        }
        return itemApplocks;
    }

    public void LoadAppList(Activity act, AdapterApplock adapterApplock){
        pkgm = act.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        AppInfos = pkgm.queryIntentActivities(intent, 0);
        for (ResolveInfo info : AppInfos) {
            ActivityInfo ai = info.activityInfo;
            Log.d("APP TITLE", ai.loadLabel(pkgm).toString());
            Log.d("APP Package Name", ai.packageName);
            //Log.d("APP Class Name", ai.name);
            adapterApplock.addItem(new ItemApplock(ai.loadLabel(pkgm).toString(),ai.loadIcon(pkgm)));
        }
    }

    public boolean CheckRunningApp(Context context, ArrayList<ItemApplock> AppLock) {
        Log.d("App Lock : ","체크함수 시작!");

        // 기타 프로세스 목록 확인
        UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
        if (stats != null)
        {
            SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
            for (UsageStats usageStats : stats) {
                runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                //Log.d("packageName = " ,usageStats.getPackageName());
            }
            Log.d("CurrentApp = " ,runningTask.get(runningTask.lastKey()).getPackageName());

            for(int i=0; i<AppLock.size(); i++){
                if (AppLock.get(i).getAppName().equals(runningTask.get(runningTask.lastKey()).getPackageName()) && AppLock.get(i).getLockFlag()==false){
                    Log.d("Catch ForeGround App : ", AppLock.get(i).getAppName());
                    //AppLock.get(i).setLockFlag(true);
                    return true;
                }
            }
        }
        else
        {
            Log.d("isRooting stats is NULL","");
        }
        return false;

        /*
        ActivityManager am = (ActivityManager) act.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();

        if(AppLock.isEmpty()==false){
            for(int i=0; i<list.size(); i++){
                for(int j=0; j<AppLock.size(); j++) {
                    Log.d("Want Lock : ", AppLock.get(j).toString());
                    if (AppLock.get(j).toString().equals(list.get(i).processName)){
                        Log.d("Catch ForeGr2222 : ", list.get(i).processName);
                        if (list.get(i).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            Log.d("Catch ForeGround App : ", list.get(i).processName);
                            AppLock.remove(j);
                            return true;
                        }
                    }
                }
            }
        }else{
            Log.d("App Lock : ","잠글 앱이 없습네다~");
        }
        return false;
        */


        /*
        ActivityManager AM = (ActivityManager) act.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> Info = AM.getRunningTasks(1);
        ComponentName topActivity = Info.get(0).topActivity;
        String currentPackageName = topActivity.getPackageName();
        if(AppLock.isEmpty()==false) {
            Log.d("Top Process : ", currentPackageName);
            for (int i = 0; i < AppLock.size(); i++) {
                if (AppLock.get(i).toString().equals(currentPackageName)) {
                    Log.d("Catch Process : ", currentPackageName);
                    return true;
                }
                Log.d("App Lock : ","잠글 앱이 안켜졌습니다~");
            }
        }else
            Log.d("App Lock : ","잠글 앱이 없습네다~");
        return false;
        */

    }
}
