package org.me.mobilesecurity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class NumberAddressDao {
    public static String findAddress(Context context, String number) {
        File file = new File(context.getFilesDir(), "address.db");
        String path = file.getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        // 使用正则匹配手机号码
        String reg = "^1[3458]\\d{9}$";
        boolean isPohne = number.matches(reg);

        String address = "未知";
        if (isPohne) {
            String prefix = number.substring(0, 7);
            String sql = "select cardtype from info where mobileprefix=?";
            Cursor cursor = db.rawQuery(sql, new String[]{prefix});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    address = cursor.getString(0);
                }
                cursor.close();
            }
        } else {
            switch (number.length()) {
                case 3:
                    address = "警报电话";
                    break;
                case 4:
                    address = "模拟器&亲情号码";
                    break;
                case 5:
                    address = "银行号码";
                    break;
                case 7:
                case 8:
                    address = "本地号码";
                    break;
                case 10:
                case 11:
                case 12:
                    String prefix = number.substring(0, 3);
                    String sql = "select city from info where area=?";
                    Cursor cursor = db.rawQuery(sql, new String[]{prefix});

                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                    } else {
                        prefix = number.substring(0, 4);
                        cursor = db.rawQuery(sql, new String[]{prefix});
                        if (cursor != null) {
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }

                    break;
                default:
                    break;
            }
        }

        db.close();

        return address;
    }
}
