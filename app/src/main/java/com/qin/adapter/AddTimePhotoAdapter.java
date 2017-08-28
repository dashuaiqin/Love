package com.qin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.qin.Utils.FileUtils;
import com.qin.Utils.ImageUtils;
import com.qin.Utils.NativeUtil;
import com.qin.Utils.StringUtils;
import com.qin.love.AddTimeActivity;
import com.qin.love.R;
import com.qin.love.ShowPhotoActivity;
import com.qin.myinterface.GetpathJepgListener;
import com.qin.view.BorderImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/18.
 */
public class AddTimePhotoAdapter extends BasePhotoAdapter {

    private ArrayList<String> paths = new ArrayList<String>();//数据源
    private ArrayList<String> sendPaths = new ArrayList<String>();//真正发送的路径（压缩后的路径）
    private Context context;
    private BorderImageView bivPic;//图片
    private ImageButton btnDel;//删除按钮
    private String psString = "";//路径

    public AddTimePhotoAdapter(Context context, String pathString) {
        super();
        if (pathString != null) {
            String[] ps = pathString.split("\0");
            for (int i = 0; i < ps.length; i++) {
                paths.add(ps[i]);
            }
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return paths.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return getItemViewType(position) != 1 ? paths.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_add_time_picture, null);
        }
        bivPic = ViewHolder.get(convertView, R.id.biv_pic);
        btnDel = ViewHolder.get(convertView, R.id.btn_del);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_del:
                        paths.remove(position);
                        sendPaths.remove(position);
                        notifyDataSetChanged();
                        break;
                    case R.id.biv_pic:
                        Intent it = new Intent(context, ShowPhotoActivity.class);
                        it.putExtra("paths", psString);
                        it.putExtra("position", position);
                        context.startActivity(it);
                        break;
                    default:
                        break;
                }

            }
        };
        btnDel.setOnClickListener(listener);
        if (getItemViewType(position) == 1) {
            btnDel.setVisibility(View.GONE);
            bivPic.setImageResource(R.drawable.ico_add_photo);
        } else {
            String path = paths.get(position);
            btnDel.setVisibility(View.VISIBLE);
            bivPic.setOnClickListener(listener);
            showImageByLoaderCacheAll(context, path, bivPic, R.drawable.icon_loading);
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (position == getCount() - 1) {
            result = 1;
        }
        return result;
    }

    /**
     * 添加数据
     *
     * @param pathString
     */
    public void addPaths(String pathString) {
        if (StringUtils.isBlank(pathString)) {
            return;
        }
        psString += pathString;
        String[] ps = pathString.split("\0");
        for (int i = 0; i < ps.length; i++) {
            paths.add(ps[i]);
            sendPaths.add(JpegByAsync(ps[i]));
        }
        notifyDataSetChanged();
    }

    /**
     * 获取路径
     *
     * @return
     */
    public void getPaths(final GetpathJepgListener getpathJepgListener) {
        //这里主要解决卡的问题
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> remainPaths = new ArrayList<>();//用来查是否已经压缩完
                remainPaths.addAll(sendPaths);
                //轮询，看是否压缩完毕
                while (remainPaths.size() > 0) {
                    for (int i = 0; i < remainPaths.size(); i++) {
                        if (FileUtils.isFileExists(remainPaths.get(i))) {
                            remainPaths.remove(i);
                        }
                    }
                    if (remainPaths.size() > 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                final String[] ps = new String[sendPaths.size()];
                for (int i = 0; i < sendPaths.size(); i++) {
                    ps[i] = sendPaths.get(i);
                }
                ((AddTimeActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getpathJepgListener.finishGetPaths(ps);
                    }
                });
            }
        }).start();
    }

    /**
     * 图片压缩(异步)
     */
    private String JpegByAsync(final String path) {
        final String pn = ImageUtils.getPhotoFileName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int quality = 30;//照片质量
                Bitmap bit = ImageUtils.getSuitableBitmap(path);
                File dirFile = context.getExternalCacheDir();
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                File jpegTrueFile = new File(dirFile, pn);
                NativeUtil.compressBitmap(bit, quality,
                        jpegTrueFile.getAbsolutePath(), false);
                if (bit != null && !bit.isRecycled()) {
                    bit.recycle();
                    bit = null;
                }
            }
        }).start();
        return context.getExternalCacheDir().toString() + "/" + pn;
    }

}
