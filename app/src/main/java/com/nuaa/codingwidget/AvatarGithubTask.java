package com.nuaa.codingwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by SCY on 2018/3/19/0019.
 */

public class AvatarGithubTask extends AsyncTask<String, Void, Bitmap> {
    private RemoteViews remoteViews;
    private Context context;
    private ComponentName componentName;
    private int appWidgetId;
    SharedPreferences sharedPreferences;

    public AvatarGithubTask(
            RemoteViews remoteViews,
            Context context,
            ComponentName componentName,
            int appWidgetId)
    {
        this.remoteViews = remoteViews;
        this.context = context;
        this.componentName = componentName;
        this.appWidgetId = appWidgetId;
        this.sharedPreferences = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String name = sharedPreferences.getString("githubUserName","LogicJake");
        try {
            String path = "https://api.github.com/users/" + name;
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
                JSONObject jsonObject = new JSONObject(baos.toString());
                Log.d("res",baos.toString());
                int id = jsonObject.getInt("id");
                String urlString = "https://avatars.githubusercontent.com/u/" + id + "?s=" +64;
                Bitmap pic = returnBitMap(urlString);
                return pic;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(result!=null){
            remoteViews.setImageViewBitmap(R.id.avatar2,result);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            if (appWidgetId == -1) {
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            } else {
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
        else
            Toast.makeText(context,"获取头像失败",Toast.LENGTH_SHORT).show();

    }

    public Bitmap returnBitMap(String url) {
        System.out.println(url);
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("bitmap",bitmap.toString());
        return bitmap;
    }
}
