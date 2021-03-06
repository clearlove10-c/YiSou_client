package com.smlz.yisounews.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.google.gson.Gson;
import com.smlz.yisounews.entity.UserInfoBase64;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static void saveUserBase64(UserInfoBase64 userInfoBase64, Context context){
        FileUtil.getFileByBytes(new Gson().toJson(userInfoBase64).getBytes(StandardCharsets.UTF_8), "preUser", context);
    }

    public static Boolean deleteFileByPath(String path){
        File file=new File(path);
        if(file.exists()){
            file.delete();
            return true;
        }
        else
            return false;
    }

    public static String readFile(File file02) {
        FileInputStream is = null;
        StringBuilder stringBuilder = null;
        try {
            if (file02.length() != 0) {
            is = new FileInputStream(file02);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(stringBuilder);
    }

    public static String getStringByFileStream(FileInputStream fis){
        byte[] buffer=null;
        try{
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] b=new byte[1024];
            int n;
            while((n= fis.read(b))!=-1){
                bos.write(b,0,n);
            }
            fis.close();
            bos.close();
            buffer=bos.toByteArray();
        }catch (IOException e){
            Log.d(TAG,"ioexception");
            e.printStackTrace();
        }
        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static String getStringByFile(File file){
        byte[] buffer=null;
        try{
            FileInputStream fis=new FileInputStream(file);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] b=new byte[1024];
            int n;
            while((n= fis.read(b))!=-1){
                bos.write(b,0,n);
            }
            fis.close();
            bos.close();
            buffer=bos.toByteArray();
        }catch (IOException e){
            Log.d("MainActivity","ioexception");
            e.printStackTrace();
        }
        return Arrays.toString(buffer);
    }

    public static byte[] getBytesByPath(String path){
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

    public static void getFileByBytes(byte[] bytes, String fileName,Context context) {
        Log.d(TAG,new String(bytes));
        // ??????FileOutputStream??????
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // ??????BufferedOutputStream??????
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // ??????BufferedOutputStream??????
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // ???????????????????????????????????????byte??????
            bufferedOutputStream.write(bytes);
            // ?????????????????????????????????????????????????????????flush()??????????????????????????????????????????
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // ??????????????????
            e.printStackTrace();
        } finally {
            // ????????????????????????
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    public static String getStringByInputStream(InputStream inputStream, String charset) {
        try {
            //????????????????????????????????????InputStreamReader??? InputStreamReader(InputStream in, String charsetName)
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            //?????????????????????????????????
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = null;
            StringBuilder builder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                builder.append(s);
            }
            //?????????
            bufferedReader.close();
            inputStreamReader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
