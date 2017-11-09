package com.reactnativesaveimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Toast Module
 * Created by majian on 2017/11/9.
 */

public class ImageModule extends ReactContextBaseJavaModule {
    public ImageModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void save(final String uri, final String name, final Promise promise){
        if (getCurrentActivity() == null)
            return;
        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(getReactApplicationContext()).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            promise.resolve(saveImageToGallery(resource, name));
                        } catch (Exception e) {
                            promise.reject(e);
                        }
                    }
                });
            }
        });
    }

    private String saveImageToGallery(Bitmap bmp, String name) throws Exception {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "my_app";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, name);
        FileOutputStream fos = new FileOutputStream(file);
        //通过io流的方式来压缩保存图片
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
        fos.flush();
        fos.close();
        //保存图片后发送广播通知更新数据库
        Uri uri = Uri.fromFile(file);
        getReactApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        return file.getAbsolutePath();
    }

    @Override
    public String getName() {
        return "ImageUtil";
    }
}
