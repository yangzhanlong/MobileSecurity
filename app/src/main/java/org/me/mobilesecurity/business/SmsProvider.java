package org.me.mobilesecurity.business;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.me.mobilesecurity.bean.SmsBean;
import org.me.mobilesecurity.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SmsProvider {
    public static void smsBackup(final Context context,
                                 final OnSmsListener listener) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                // 显示进度

                // dialog.show();// 准备阶段
                if (listener != null) {
                    listener.onPre();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean flag = true;
                try {
                    // 1.读取系统短信
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    String[] projection = new String[] { "address", "date",
                            "read", "type", "body" };
                    String selection = null;
                    String[] selectionArgs = null;
                    String sortOrder = null;
                    Cursor cursor = cr.query(uri, projection, selection,
                            selectionArgs, sortOrder);

                    List<SmsBean> list = new ArrayList<SmsBean>();
                    if (cursor != null) {
                        int count = cursor.getCount();
                        int progress = 0;
                        // 推出大小
                        publishProgress(count, progress);
                        while (cursor.moveToNext()) {

                            String address = cursor.getString(0);
                            long date = cursor.getLong(1);
                            int read = cursor.getInt(2);
                            int type = cursor.getInt(3);
                            String body = cursor.getString(4);

                            SmsBean bean = new SmsBean();
                            bean.address = address;
                            bean.date = date;
                            bean.read = read;
                            bean.type = type;
                            bean.body = body;

                            list.add(bean);

                            // 推出进度
                            publishProgress(count, ++progress);

                            Thread.sleep(50);
                        }
                        cursor.close();
                    }

                    // 存储-->SD中: json
                    // 将对象转换为json字符串，将字符串存到文件中
                    Gson gson = new Gson();
                    String json = gson.toJson(list);

                    // 存储为文件
                    File file = new File(
                            Environment.getExternalStorageDirectory(),
                            "sms.json");
                    BufferedWriter writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(file));
                        writer.write(json);
                    } finally {
                        StreamUtils.closeIO(writer);
                    }

                } catch (Exception e) {
                    flag = false;
                }
                return flag;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                Integer max = values[0];
                Integer progress = values[1];
                // dialog.setMax(max);// 进度阶段:
                // dialog.setProgress(progress);

                if (listener != null) {
                    listener.onProgress(max, progress);
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                // dialog.dismiss();// 结果阶段:
                if (listener != null) {
                    listener.onFinish(result);
                }
            }
        }.execute();
    }

    public static void smsRestore(final Context context,
                                  final OnSmsListener listener) {
        // 读取短信备份的文件---》对象---》插入数据 --->UI显示

        new AsyncTask<Void, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (listener != null) {
                    listener.onPre();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                List<SmsBean> list = new ArrayList<SmsBean>();

                // 读取短信文件
                File file = new File(Environment.getExternalStorageDirectory(),
                        "sms.json");

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String json = reader.readLine();

                    Gson gson = new Gson();
                    list = gson.fromJson(json, new TypeToken<List<SmsBean>>() {
                    }.getType());

                    // 获得总大小
                    int count = list.size();
                    int progress = 0;
                    publishProgress(count, progress);

                    ContentResolver cr = context.getContentResolver();
                    Uri url = Uri.parse("content://sms");
                    for (int i = 0; i < list.size(); i++) {
                        SmsBean bean = list.get(i);
                        // 逐条插入
                        ContentValues values = new ContentValues();
                        values.put("address", bean.address);
                        values.put("date", bean.date);
                        values.put("read", bean.read);
                        values.put("type", bean.type);
                        values.put("body", bean.body);
                        cr.insert(url, values);

                        publishProgress(count, ++progress);

                        Thread.sleep(50);
                    }

                } catch (Exception e) {
                    return false;
                } finally {
                    StreamUtils.closeIO(reader);
                }

                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

                int max = values[0];
                int progress = values[1];
                if (listener != null) {
                    listener.onProgress(max, progress);
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {

                if (listener != null) {
                    listener.onFinish(result);
                }
            }
        }.execute();

    }

    public interface OnSmsListener {
        void onPre();// 准备阶段

        void onProgress(int max, int progress);// 进度阶段

        void onFinish(boolean sucess);// 结束阶段
    }
}
