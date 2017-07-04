package org.me.mobilesecurity.business;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import org.me.mobilesecurity.bean.ContactBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactProvider {

    /**
     * 获取系统所有联系人
     * @param context
     * @return List<ContactBean>
     */
    public static List<ContactBean> getAllContacts(Context context) {
        List<ContactBean> list = new ArrayList<ContactBean>();
        // 数据查询-->系统的联系人
        ContentResolver contentResolver = context.getContentResolver();

        // 要查询数据对应的uri地址  通过号码查询  content://com.android.contacts/data/phones
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // 要查询的列
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,  // 名称
                ContactsContract.CommonDataKinds.Phone.NUMBER, // 号码
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 联系人的id
        };

        String selection = null; // 查询的条件
        String[] selectionArgs = null; // 查询条件对应的参数
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " desc"; // 查询的排序

        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean bean = new ContactBean();

                String name = cursor.getString(0);
                String number = cursor.getString(1);
                long contactId = cursor.getLong(2);

                bean.setName(name);
                bean.setNumber(number);
                bean.setContactId(contactId);

                list.add(bean);
            }
            cursor.close();
        }

        return list;
    }

    /**
     * 得到联系人的头像
     * @param context：上下文
     * @param contactId：联系人id
     * @return：bitmap头像
     */
    public static Bitmap getContactPhoto(Context context, long contactId) {

        // content://contacts //所有联系人
        // content://contacts/1 //具体联系人

        // ContactsContract.Contacts.CONTENT_URI;//所有联系人的uri
        Uri uri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));

        ContentResolver contentResolver = context.getContentResolver();
        InputStream stream = ContactsContract.Contacts
                .openContactPhotoInputStream(contentResolver, uri);

        // 将流转换为bitmap
        return BitmapFactory.decodeStream(stream);
    }
}
