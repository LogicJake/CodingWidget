package com.nuaa.codingwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SCY on 2018/3/19/0019.
 */

public class ContributionsCodingTask extends AsyncTask<String, Void, String> {
    private RemoteViews remoteViews;
    private Context context;
    private ComponentName componentName;
    private int appWidgetId;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    SharedPreferences sharedPreferences;

    public ContributionsCodingTask(
            RemoteViews remoteViews,
            Context context,
            ComponentName componentName,
            int appWidgetId,
            int bitmapWidth,
            int bitmapHeight) {
        this.remoteViews = remoteViews;
        this.context = context;
        this.componentName = componentName;
        this.appWidgetId = appWidgetId;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.sharedPreferences = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String name = sharedPreferences.getString("userName","kexijia");
        System.out.println(name);
        try {
            String path = "https://coding.net/api/user/activeness/data/" + name;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                return baos.toString();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result).getJSONObject("data");
            CharSequence total= jsonObject.getString("total");
            CharSequence day = jsonObject.getJSONObject("current_active_duration").getString("days")+" d";
            remoteViews.setTextViewText(R.id.num,total);
            remoteViews.setTextViewText(R.id.day,day);
            Bitmap contribution = Util.get2DBitmap(
                    context,
                    jsonObject.getJSONArray("daily_activeness"),
                    Color.parseColor("#D6E685"),
                    Color.parseColor("#000000"),
                    bitmapWidth,
                    bitmapHeight);
            remoteViews.setImageViewBitmap(R.id.contribution,contribution);
            System.out.println(contribution);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetId == -1) {
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        } else {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}
