package com.example.caadetalhes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtils {

    public static void saveImage(Bitmap bitmap, String path) {
        try (FileOutputStream out = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static Bitmap loadImage(String path) {
        return BitmapFactory.decodeFile(path);
    }

    public static void savePoints(ArrayList<PointF> pontos, String path) {
        JSONArray array = new JSONArray();
        for (PointF p : pontos) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("x", p.x);
                obj.put("y", p.y);
                array.put(obj);
            } catch (JSONException e) { e.printStackTrace(); }
        }
        try (FileWriter file = new FileWriter(path)) {
            file.write(array.toString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static ArrayList<PointF> loadPoints(String path) {
        ArrayList<PointF> pontos = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(path);
            StringBuilder builder = new StringBuilder();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                builder.append(new String(buffer, 0, len));
            }
            fis.close();

            JSONArray array = new JSONArray(builder.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                pontos.add(new PointF((float) obj.getDouble("x"), (float) obj.getDouble("y")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pontos;
    }

}

