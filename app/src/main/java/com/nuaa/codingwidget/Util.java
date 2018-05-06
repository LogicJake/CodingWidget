package com.nuaa.codingwidget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by SCY on 2018/3/19/0019.
 */



public class Util {

    public static class Day {

        public int year = -1;
        public int month = -1;
        public int day = -1;

        // Level is used to record the color of the block
        public int level = -1;
        // Data is used to calculated the height of the pillar
        public int data = -1;

        public Day(int year, int month, int day, int level, int data) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.level = level;
            this.data = data;
        }

        @Override
        public String toString() {
            return "Day{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    ", level=" + level +
                    ", data=" + data +
                    '}';
        }
    }

    public static int getScreenWidth(Context context) {
        Display localDisplay
                = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        Point point = new Point();
        localDisplay.getSize(point);
        return point.x;
    }

    public static int dp2px(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int getContributionsColumnNumber() {
        return 365/7;
    }

    public static int calculateR(int baseR, int level) {
        switch (level) {
            case 0: return 238;
            case 1: return baseR;
            case 2: return (int) (baseR * (9 + 46 + 15) / (37f + 9 + 46 + 15));
            case 3: return (int) (baseR * (46 + 15) / (37f + 9 + 46 + 15));
            case 4: return (int) (baseR * (15) / (37f + 9 + 46 + 15));
            default: return 238;
        }
    }

    public static int calculateG(int baseG, int level) {
        switch (level) {
            case 0: return 238;
            case 1: return baseG;
            case 2: return (int) (baseG * (35 + 59 + 104) / (32f + 35 + 59 + 104));
            case 3: return (int) (baseG * (59 + 104) / (32f + 35 + 59 + 104));
            case 4: return (int) (baseG * (104) / (32f + 35 + 59 + 104));
            default: return 238;
        }
    }

    public static int calculateB(int baseB, int level) {
        switch (level) {
            case 0: return 238;
            case 1: return baseB;
            case 2: return (int) (baseB * (37 + 29 + 35) / (32f + 37 + 29 + 35));
            case 3: return (int) (baseB * (29 + 35) / (32f + 37 + 29 + 35));
            case 4: return (int) (baseB * (35) / (32f + 37 + 29 + 35));
            default: return 238;
        }
    }

    public static int getWeekDayFromDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int calculateLevelColor(int baseColor, int level) {
        return Color.rgb(
                calculateR(Color.red(baseColor), level),
                calculateG(Color.green(baseColor), level),
                calculateB(Color.blue(baseColor), level));
    }

    public static String getShortMonthName(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(Calendar.SECOND, 0);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.US);
        return month_date.format(calendar.getTime());
    }

    public  static ArrayList<Day> getContributionsFromJsonArray(JSONArray jsonArray) {
        ArrayList<Day> contributions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length();i++){
            try {
                JSONObject tmp = jsonArray.getJSONObject(i);
                int count = tmp.getInt("count");
                String dateString = tmp.getString("date");
                int level = 0;
                if (count==0)
                    level = 0;
                else if (count > 0 && count<7)
                    level = 1;
                else if (count>=7&&count<14)
                    level = 2;
                else if (count>=14&&count<16)
                    level = 3;
                else if (count>=16)
                    level = 4;
                contributions.add(new Day(
                    Integer.valueOf(dateString.substring(0, 4)),
                    Integer.valueOf(dateString.substring(5, 7)),
                    Integer.valueOf(dateString.substring(8, 10)),
                    level,
                    count
                ));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("contribuction2", contributions.toString());

        return contributions;
    }

    public final static String FILL_STRING = "fill=\"";
    public final static String DATA_STRING = "data-count=\"";
    public final static String DATE_STRING = "data-date=\"";
    public static int total;
    public static ArrayList<Day> getContributionsFromString(String string) {
        total = 0;
        ArrayList<Day> contributions = new ArrayList<>();
        int fillPos = -1;
        int dataPos = -1;
        int datePos = -1;
        while (true) {
            fillPos = string.indexOf(FILL_STRING, fillPos + 1);
            dataPos = string.indexOf(DATA_STRING, dataPos + 1);
            datePos = string.indexOf(DATE_STRING, datePos + 1);

            if (fillPos == -1) break;

            int level = 0;
            String levelString
                    = string.substring(fillPos + FILL_STRING.length(),
                    fillPos + FILL_STRING.length() + 7);
            switch (levelString) {
                case "#ebedf0": level = 0; break;
                case "#c6e48b": level = 1; break;
                case "#7bc96f": level = 2; break;
                case "#239a3b": level = 3; break;
                case "#196127": level = 4; break;
            }

            int dataEndPos = string.indexOf("\"", dataPos + DATA_STRING.length());
            String dataString = string.substring(dataPos + DATA_STRING.length(), dataEndPos);
            int data = Integer.valueOf(dataString);
            total += data;

            String dateString
                    = string.substring(datePos + DATE_STRING.length(),
                    datePos + DATE_STRING.length() + 11);

            contributions.add(new Day(
                    Integer.valueOf(dateString.substring(0, 4)),
                    Integer.valueOf(dateString.substring(5, 7)),
                    Integer.valueOf(dateString.substring(8, 10)),
                    level,
                    data
            ));
        }
        System.out.print(contributions.toString());
        return contributions;
    }

    public static String getWeekdayFirstLetter(int weekDay) {
        switch (weekDay) {
            case 0: return "S";
            case 1: return "M";
            case 2: return "T";
            case 3: return "W";
            case 4: return "T";
            case 5: return "F";
            case 6: return "S";
            default: return "";
        }
    }

    public static Bitmap get2DBitmap(
            Context  context,
            JSONArray data,
            int baseColor,
            int textColor,
            int bitmapWidth,
            int bitmapHeight) {
        Bitmap bitmap;
        Canvas canvas;
        Paint blockPaint;
        Paint monthTextPaint;
        Paint weekDayTextPaint;
        boolean monthBelow = false;
        int startWeekDay = 0;

        ArrayList<Day> contributions = Util.getContributionsFromJsonArray(data);
        int horizontalBlockNumber = Util.getContributionsColumnNumber();
        int verticalBlockNumber = 7;
        float ADJUST_VALUE = 0.8f;
        float blockWidth = bitmapWidth / (ADJUST_VALUE + horizontalBlockNumber) * (1.0F - 0.1F);
        float spaceWidth = bitmapWidth / (ADJUST_VALUE + horizontalBlockNumber) - blockWidth;
        float monthTextHeight = blockWidth * 1.5F;
        float weekTextHeight = blockWidth;
        float topMargin = 15f;
        bitmapHeight = (int)(monthTextHeight + topMargin
                + verticalBlockNumber * (blockWidth + spaceWidth));

        bitmap = Bitmap.createBitmap(bitmapWidth+40, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint.setStyle(Paint.Style.FILL);

        monthTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        monthTextPaint.setTextSize(monthTextHeight);
        monthTextPaint.setColor(textColor);
        monthTextPaint.setTypeface(
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        weekDayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekDayTextPaint.setTextSize(weekTextHeight);
        weekDayTextPaint.setColor(textColor);
        weekDayTextPaint.setTypeface(
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        // draw the text for weekdays
        float textStartHeight = (monthBelow ? 0 : monthTextHeight + topMargin)
                + blockWidth + spaceWidth;
        Paint.FontMetricsInt fontMetrics = monthTextPaint.getFontMetricsInt();
        float baseline = (
                textStartHeight + blockWidth +
                        textStartHeight -
                        fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 1) % 7),
                0, baseline, weekDayTextPaint);
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 3) % 7),
                0, baseline + 2 * (blockWidth + spaceWidth), weekDayTextPaint);
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 5) % 7),
                0, baseline + 4 * (blockWidth + spaceWidth), weekDayTextPaint);

        // draw the blocks
        int currentWeekDay = Util.getWeekDayFromDate(
                contributions.get(0).year,
                contributions.get(0).month,
                contributions.get(0).day);
        float x = weekTextHeight + topMargin;
        float y = (currentWeekDay - startWeekDay+ 7) % 7
                * (blockWidth + spaceWidth)
                + (monthBelow ? 0 : topMargin + monthTextHeight);
        int lastMonth = contributions.get(0).month - 1;
        for (Day day : contributions) {
            blockPaint.setColor(Util.calculateLevelColor(baseColor, day.level));
            canvas.drawRect(x, y, x + blockWidth, y + blockWidth, blockPaint);

            currentWeekDay = (currentWeekDay + 1) % 7;
            if (currentWeekDay == startWeekDay) {
                // another column
                x += blockWidth + spaceWidth;
                y = monthBelow ? 0 : topMargin + monthTextHeight;
                if (!monthBelow && day.month != lastMonth) {
                    // judge whether we should draw the text of month
                    canvas.drawText(
                            Util.getShortMonthName(day.year, day.month, day.day),
                            x, monthTextHeight, monthTextPaint);
                    lastMonth = day.month;
                }
            } else {
                y += blockWidth + spaceWidth;
                if (monthBelow && currentWeekDay == (startWeekDay + 6) % 7
                        && day.month != lastMonth) {
                    // judge whether we should draw the text of month
                    canvas.drawText(
                            Util.getShortMonthName(day.year, day.month, day.day),
                            x, y + monthTextHeight + topMargin, monthTextPaint);
                    lastMonth = day.month;
                }
            }
        }

        return bitmap;
    }

    public static Map<String, Object> Githubget2DBitmap(
            Context  context,
            String data,
            int baseColor,
            int textColor,
            int bitmapWidth,
            int bitmapHeight) {
        Map<String, Object> res = new HashMap<String, Object>();
        Bitmap bitmap;
        Canvas canvas;
        Paint blockPaint;
        Paint monthTextPaint;
        Paint weekDayTextPaint;
        boolean monthBelow = false;
        int startWeekDay = 0;

        ArrayList<Day> contributions = Util.getContributionsFromString(data);
        int horizontalBlockNumber = Util.getContributionsColumnNumber();
        int verticalBlockNumber = 7;
        float ADJUST_VALUE = 0.8f;
        float blockWidth = bitmapWidth / (ADJUST_VALUE + horizontalBlockNumber) * (1.0F - 0.1F);
        float spaceWidth = bitmapWidth / (ADJUST_VALUE + horizontalBlockNumber) - blockWidth;
        float monthTextHeight = blockWidth * 1.5F;
        float weekTextHeight = blockWidth;
        float topMargin = 15f;
        bitmapHeight = (int)(monthTextHeight + topMargin
                + verticalBlockNumber * (blockWidth + spaceWidth));

        bitmap = Bitmap.createBitmap(bitmapWidth+40, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint.setStyle(Paint.Style.FILL);

        monthTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        monthTextPaint.setTextSize(monthTextHeight);
        monthTextPaint.setColor(textColor);
        monthTextPaint.setTypeface(
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        weekDayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weekDayTextPaint.setTextSize(weekTextHeight);
        weekDayTextPaint.setColor(textColor);
        weekDayTextPaint.setTypeface(
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        // draw the text for weekdays
        float textStartHeight = (monthBelow ? 0 : monthTextHeight + topMargin)
                + blockWidth + spaceWidth;
        Paint.FontMetricsInt fontMetrics = monthTextPaint.getFontMetricsInt();
        float baseline = (
                textStartHeight + blockWidth +
                        textStartHeight -
                        fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 1) % 7),
                0, baseline, weekDayTextPaint);
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 3) % 7),
                0, baseline + 2 * (blockWidth + spaceWidth), weekDayTextPaint);
        canvas.drawText(Util.getWeekdayFirstLetter((startWeekDay + 5) % 7),
                0, baseline + 4 * (blockWidth + spaceWidth), weekDayTextPaint);

        // draw the blocks
        int currentWeekDay = Util.getWeekDayFromDate(
                contributions.get(0).year,
                contributions.get(0).month,
                contributions.get(0).day);
        float x = weekTextHeight + topMargin;
        float y = (currentWeekDay - startWeekDay+ 7) % 7
                * (blockWidth + spaceWidth)
                + (monthBelow ? 0 : topMargin + monthTextHeight);
        int lastMonth = contributions.get(0).month - 1;
        for (Day day : contributions) {
            blockPaint.setColor(Util.calculateLevelColor(baseColor, day.level));
            canvas.drawRect(x, y, x + blockWidth, y + blockWidth, blockPaint);

            currentWeekDay = (currentWeekDay + 1) % 7;
            if (currentWeekDay == startWeekDay) {
                // another column
                x += blockWidth + spaceWidth;
                y = monthBelow ? 0 : topMargin + monthTextHeight;
                if (!monthBelow && day.month != lastMonth) {
                    // judge whether we should draw the text of month
                    canvas.drawText(
                            Util.getShortMonthName(day.year, day.month, day.day),
                            x, monthTextHeight, monthTextPaint);
                    lastMonth = day.month;
                }
            } else {
                y += blockWidth + spaceWidth;
                if (monthBelow && currentWeekDay == (startWeekDay + 6) % 7
                        && day.month != lastMonth) {
                    // judge whether we should draw the text of month
                    canvas.drawText(
                            Util.getShortMonthName(day.year, day.month, day.day),
                            x, y + monthTextHeight + topMargin, monthTextPaint);
                    lastMonth = day.month;
                }
            }
        }
        res.put("bitmap",bitmap);
        res.put("total",total);
        return res;
    }

}
