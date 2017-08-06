package org.me.mobilesecurity.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.me.mobilesecurity.bean.ChildBean;
import org.me.mobilesecurity.bean.GroupBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommonNumberDao {
    /**
     * 获取所有的常用号码数据
     * @param context
     * @return
     */
    public static List<GroupBean> getGroupDatas(Context context) {
        List<GroupBean> list = new ArrayList<GroupBean>();
        File file = new File(context.getFilesDir(), "commonnum.db");
        String path = file.getAbsolutePath();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READONLY);
        // sql查询
        String sql = "select name,idx from classlist";
        Cursor groupCursor = db.rawQuery(sql, null);
        if (groupCursor != null) {
            while (groupCursor.moveToNext()) {
                String title = groupCursor.getString(0);
                int idx = groupCursor.getInt(1);

                GroupBean group = new GroupBean();
                group.setTitle(title);

                group.childrenDatas = new ArrayList<ChildBean>();

                String childSql = "select name, number from table" + idx;

                Cursor childCursor = db.rawQuery(childSql, null);
                if (childCursor != null) {
                    while (childCursor.moveToNext()) {
                        String name = childCursor.getString(0);
                        String number = childCursor.getString(1);

                        ChildBean child = new ChildBean(name, number);
                        group.childrenDatas.add(child);
                    }
                    childCursor.close();
                }
                list.add(group);
            }
            groupCursor.close();
        }
        db.close();
        return list;
    }
}
