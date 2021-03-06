package com.nuaa.codingwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */

public class CodingAppWidget extends AppWidgetProvider {
    private static final String TAG = "CodingAppWidget";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, CodingAppWidget.class)).length == 0) return;
        Log.e(TAG,intent.getAction());
        switch (intent.getAction()) {
            case "android.appwidget.action.MANUAL_UPDATE":
                Toast.makeText(context,"RefreshingCoding",Toast.LENGTH_SHORT).show();
                updateAll(context, -1);
                break;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAll(context, appWidgetId);
        }
    }

    private void updateAll(Context context, int appWidgetId){
        System.out.println(appWidgetId);
        ComponentName componentName = new ComponentName(context, CodingAppWidget.class);
        RemoteViews view = new RemoteViews(context.getPackageName(),R.layout.coding_app_widget);

        new ContributionsCodingTask(view,context,componentName,appWidgetId,Util.getScreenWidth(context),0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        new AvatarCodingTask(view, context, componentName, appWidgetId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

        Intent clickInt = new Intent(context, SettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickInt, 0);
        view.setOnClickPendingIntent(R.id.contribution, pendingIntent);


        Intent intent = new Intent("android.appwidget.action.MANUAL_UPDATE");
        intent.setPackage(context.getPackageName()); //指定包名
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, R.id.avatar, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.avatar, pendingIntent2);


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetId == -1) {
            appWidgetManager.updateAppWidget(componentName, view);
        } else {
            appWidgetManager.updateAppWidget(appWidgetId, view);
        }



    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

