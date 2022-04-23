package com.smlz.yisounews.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {
    //base64编码
    public static String encode(byte[] bytes){
        return new String(Base64.getUrlEncoder().encode(bytes));
    }
    //base64解码
    public static byte[] decode(String encodeStr) throws IOException{
        byte[] bt = null;
        Base64.Decoder decoder = Base64.getUrlDecoder();
        bt = decoder.decode(encodeStr);
        return bt;
    }

    public static String encodeImage(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        byte[] rs = new byte[fis.available()];
        fis.read(rs);
        fis.close();
        return encode(rs);
    }

    public static Bitmap getBitmapByBytes(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void showImage(ImageView imageView,byte[] bytes){
        imageView.setImageBitmap(getBitmapByBytes(bytes));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String handleImageOnKitKat(Intent data, Context context) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,context);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null,context);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如或是content类型的URI就使用普通方法处理
            imagePath = getImagePath(uri, null,context);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的直接获取图片路径就行
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    public static String handleImageBeforeKitKat(Intent data,Context context) {
        Uri uri = data.getData();
        return getImagePath(uri, null,context);
    }

    @SuppressLint("Range")
    private static String getImagePath(Uri uri, String selection, Context context) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
