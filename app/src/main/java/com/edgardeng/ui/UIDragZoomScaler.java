package com.edgardeng.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.edgardeng.R;
import com.edgardeng.widget.DragImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * 放大 拖动 图片
 *
 * @author Edgar Deng (http:weibo.com/edgardeng)
 * @date 16/4/25
 */
public class UIDragZoomScaler extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.uidragzoomscaler);
    }


}