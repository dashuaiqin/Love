package com.qin.love;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

import com.qin.adapter.ShowPhotoAdapter;
import com.qin.view.ShowPhotoViewPager;

/**
 * 用于显示可放大缩小的照片
 *
 * @author Administrator
 */
public class ShowPhotoActivity extends Activity {
    private ShowPhotoViewPager mViewPager;
    private TextView txtPosition;
    /**
     * 适配器.
     */
    private ShowPhotoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 界面初始化
     */
    private void initView() {
        mViewPager = (ShowPhotoViewPager) findViewById(R.id.viewpager);
        adapter = new ShowPhotoAdapter(this, (String) getIntent().getStringExtra("paths"));
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
        txtPosition = (TextView) findViewById(R.id.txt_position);
        txtPosition.setText(getIntent().getIntExtra("position", 1) + 1 + "/" + adapter.getCount());
        mViewPager.addOnPageChangeListener(listener);
    }

    /**
     * viewpager翻页监听
     */
    OnPageChangeListener listener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            txtPosition.setText(position + 1 + "/" + adapter.getCount());

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    };
}