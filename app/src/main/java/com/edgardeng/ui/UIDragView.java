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

import com.edgardeng.widget.DragImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import com.edgardeng.R;

/**
 *
 * 放大 拖动 图片
 *
 * @author Edgar Deng (http:weibo.com/edgardeng)
 * @date 16/4/25
 */
public class UIDragView extends Activity {
    private int window_width, window_height;
    private DragImageView dragImageView;
    private int state_height;

    private ViewTreeObserver viewTreeObserver;
    private  BitmapUtil bitmapUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.uidragview);
        /**  **/
        WindowManager manager = getWindowManager();
        window_width = manager.getDefaultDisplay().getWidth();
        window_height = manager.getDefaultDisplay().getHeight();

        dragImageView = (DragImageView) findViewById(R.id.dragImageView);
        bitmapUtil = new BitmapUtil();
        Bitmap bmp = bitmapUtil.ReadBitmapById(this, R.drawable.scene,
                window_width, window_height);
        dragImageView.setImageBitmap(bmp);
        dragImageView.setmActivity(this);

        viewTreeObserver = dragImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (state_height == 0) {
                            Rect frame = new Rect();
                            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                            state_height = frame.top;
                            dragImageView.setScreen_H(window_height-state_height);
                            dragImageView.setScreen_W(window_width);
                        }
                    }
                });

    }




    class BitmapUtil {

        public  Bitmap ReadBitmapById(Context context, int resId) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;

            InputStream is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        }

        public  Bitmap ReadBitmapById(Context context, int drawableId,
                                            int screenWidth, int screenHight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inInputShareable = true;
            options.inPurgeable = true;
            InputStream stream = context.getResources().openRawResource(drawableId);
            Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
            return getBitmap(bitmap, screenWidth, screenHight);
        }


        public  Bitmap getBitmap(Bitmap bitmap, int screenWidth,
                                       int screenHight) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Log.e("jj", "ͼƬ���" + w + ",screenWidth=" + screenWidth);
            Matrix matrix = new Matrix();
            float scale = (float) screenWidth / w;
            float scale2 = (float) screenHight / h;

            // scale = scale < scale2 ? scale : scale2;

            // ��֤ͼƬ������.
            matrix.postScale(scale, scale);
            // w,h��ԭͼ������.
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }


        private  int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
        private  int MB = 1024 * 1024;
        public final static String DIR = "/sdcard/hypers";

        public  void saveBmpToSd(Bitmap bm, String url, int quantity) {

            if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
                return;
            }
            if (!Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState()))
                return;
            String filename = url;
            // Ŀ¼�����ھʹ���
            File dirPath = new File(DIR);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }

            File file = new File(DIR + "/" + filename);
            try {
                file.createNewFile();
                OutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, quantity, outStream);
                outStream.flush();
                outStream.close();

            } catch (FileNotFoundException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public  Bitmap GetBitmap(String url, int quantity) {
            InputStream inputStream = null;
            String filename = "";
            Bitmap map = null;
            URL url_Image = null;
            String LOCALURL = "";
            if (url == null)
                return null;
            try {
                filename = url;
            } catch (Exception err) {
            }

            LOCALURL = URLEncoder.encode(filename);
            if (Exist(DIR + "/" + LOCALURL)) {
                map = BitmapFactory.decodeFile(DIR + "/" + LOCALURL);
            } else {
                try {
                    url_Image = new URL(url);
                    inputStream = url_Image.openStream();
                    map = BitmapFactory.decodeStream(inputStream);
                    // url = URLEncoder.encode(url, "UTF-8");
                    if (map != null) {
                        saveBmpToSd(map, LOCALURL, quantity);
                    }
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return map;
        }


        public  boolean Exist(String url) {
            File file = new File(DIR + url);
            return file.exists();
        }


        private  int freeSpaceOnSd() {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                    .getPath());
            double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                    .getBlockSize()) / MB;

            return (int) sdFreeMB;
        }

    }



}