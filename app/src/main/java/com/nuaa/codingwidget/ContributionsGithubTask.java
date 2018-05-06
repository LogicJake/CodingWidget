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
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SCY on 2018/3/19/0019.
 */

public class ContributionsGithubTask extends AsyncTask<String, Void, String> {
    private RemoteViews remoteViews;
    private Context context;
    private ComponentName componentName;
    private int appWidgetId;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;
    SharedPreferences sharedPreferences;

    public ContributionsGithubTask(
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
        String name = sharedPreferences.getString("githubUserName","LogicJake");
        try {
            String path = "https://github.com/users/" + name+"/contributions";
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
            Map<String, Object> res  = Util.Githubget2DBitmap(
                    context,
                    result,
                    Color.parseColor("#D6E685"),
                    Color.parseColor("#000000"),
                    bitmapWidth,
                    bitmapHeight);
            remoteViews.setImageViewBitmap(R.id.contribution,(Bitmap) res.get("bitmap"));
            remoteViews.setTextViewText(R.id.num,Integer.toString((int)res.get("total")) );
        }
            catch (Exception e) {
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
