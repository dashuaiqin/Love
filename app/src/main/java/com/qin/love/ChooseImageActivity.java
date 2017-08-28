package com.qin.love;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.qin.Application.MyApplication;
import com.qin.Utils.ImageUtils;
import com.qin.adapter.ChooseImageAdapter;
import com.qin.adapter.ImageGvAdapter;
import com.qin.cons.IntegerCons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 选择本地图片
 *
 * @author 123
 */
public class ChooseImageActivity extends BaseActivity {
    ListView lvChooseImage;
    ChooseImageAdapter adapter;
    JSONObject joIsSelected;// 用于保存选择的状态
    public static ChooseImageActivity instance;// 用于供其他调用
    JSONArray js;// 数据来源
    ImageGvAdapter imageGvAdapter;// 用于显示gridview
    public GridView gvChooseImage;// gridview
    String gvFolderName;// 进入某个文件夹后文件夹的名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_img);
        MyApplication.getInstance().addActivity(this);
        joIsSelected = new JSONObject();
        // 获取数据
        js = ImageUtils.getPhotoFolderList(this);
        // 将数据显示到页面上
        lvChooseImage = (ListView) findViewById(R.id.lv_choose_img);
        try {
            adapter = new ChooseImageAdapter(this, js);// 创建自定义适配器
        } catch (Exception e) {
            e.printStackTrace();
        }
        lvChooseImage.setAdapter(adapter);
        isEmpty(adapter);
        gvChooseImage = (GridView) findViewById(R.id.gv_choose_img);
        gvChooseImage.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉点击时的背景
        lvChooseImage.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 隐藏列表，显示gridview
                lvChooseImage.setVisibility(View.GONE);
                gvChooseImage.setVisibility(View.VISIBLE);
                JSONArray jsPhoto;// 数据源
                try {
                    gvFolderName = js.getJSONObject(position).getString(
                            "folderName");
                } catch (JSONException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                jsPhoto = ImageUtils.getPhotoListByFolder(
                        ChooseImageActivity.this, gvFolderName);
                HashMap<String, Boolean> hash = null;
                try {
                    if (joIsSelected.has(gvFolderName))
                        hash = (HashMap<String, Boolean>) joIsSelected
                                .get(gvFolderName);// 获取已选的列表
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 获取数据
                imageGvAdapter = new ImageGvAdapter(ChooseImageActivity.this,
                        jsPhoto, hash, gvChooseImage);
                // 将数据显示到页面上
                gvChooseImage.setAdapter(imageGvAdapter);
                gvChooseImage.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (MyApplication.isSetIcon) {
                            if (MyApplication.isHomeBg) {
                                Intent it = new Intent();
                                it.setClass(ChooseImageActivity.this, MainActivity.class);
                                it.putExtra("path", (String) imageGvAdapter.getItem(position));
                                setResult(IntegerCons.TYPE_HOME_BG, it);
                                finish();
                            } else {
                                Intent it = new Intent();
                                it.setClass(ChooseImageActivity.this, CutPicActivity.class);
                                it.putExtra("path", (String) imageGvAdapter.getItem(position));
                                Log.i("HH", "path:" + getPaths());
                                startActivity(it);
                                finish();
                            }
                        } else {
                            CheckBox cbChoose = (CheckBox) view
                                    .findViewById(R.id.ck_photo);
                            cbChoose.toggle();// 改变checkbox的状态
                            imageGvAdapter.isSelected.put(
                                    (String) imageGvAdapter.getItem(position),
                                    cbChoose.isChecked()); // 同时修改map的值保存状态

                        }
                    }
                });
            }
        });

        Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
        Button btnReChoose = (Button) findViewById(R.id.btn_re_choose);
        if (MyApplication.isSetIcon) {
            btnConfirm.setVisibility(View.GONE);
            btnReChoose.setVisibility(View.GONE);
        }
        btnConfirm.setOnClickListener(listener);
        btnReChoose.setOnClickListener(listener);
    }

    /**
     * 控件的监听事件
     */
    OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_re_choose:
                    if (gvChooseImage.getVisibility() == View.VISIBLE) {
                        gvReChoose();
                    } else {
                        lvReChoose();
                    }
                    break;
                case R.id.btn_confirm:
                    back();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 刷新
     */
    public void refresh() {
        for (int i = 0; i < js.length(); i++) {
            try {
                JSONObject objecte = js.getJSONObject(i);
                String folderName = objecte.getString("folderName");
                if (joIsSelected.has(folderName)) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Boolean> isSelected = (HashMap<String, Boolean>) joIsSelected
                            .get(folderName);
                    int total = 0;
                    for (Map.Entry<String, Boolean> entry : isSelected
                            .entrySet()) { // 遍历hashmap
                        if (entry.getValue().booleanValue()) {
                            total++;
                        }
                    }
                    objecte.put("choosedNum", total);
                } else
                    objecte.put("choosedNum", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * listView的重选
     */
    public void lvReChoose() {
        joIsSelected = new JSONObject();
        refresh();
    }

    /**
     * gridView的重选
     */
    public void gvReChoose() {
        for (int i = 0; i < imageGvAdapter.getCount(); i++) {
            String key = (String) imageGvAdapter.getItem(i);
            if (imageGvAdapter.isSelected.get(key) == true) {
                imageGvAdapter.isSelected.put(key, false); // 同时修改map的值保存状态
            }
        }
        imageGvAdapter.notifyDataSetChanged();
    }

    /**
     * 获取到文件路径
     *
     * @return
     */
    public String getPaths() {
        String paths = "";
        for (int i = 0; i < js.length(); i++) {
            try {
                JSONObject objecte = js.getJSONObject(i);
                String folderName = objecte.getString("folderName");
                if (joIsSelected.has(folderName)) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Boolean> isSelected = (HashMap<String, Boolean>) joIsSelected
                            .get(folderName);
                    for (Map.Entry<String, Boolean> entry : isSelected
                            .entrySet()) { // 遍历hashmap
                        if (entry.getValue().booleanValue()) {
                            paths += entry.getKey() + "\0";
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * 判断某项是否为空，并做出相应的操作
     *
     * @param adapter
     */
    public void isEmpty(Adapter adapter) {
        LinearLayout llEmpty = (LinearLayout) findViewById(R.id.ll_empty);
        if (adapter != null && adapter.getCount() == 0) {
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            llEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (gvChooseImage.getVisibility() == View.VISIBLE) {
            // 返回时将数据存到ChooseImageActivity的joIsSelected中
            try {
                if (imageGvAdapter.isSelected.size() != 0) {
                    joIsSelected.put(gvFolderName, imageGvAdapter.isSelected);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 隐藏gridview，显示listview
            gvChooseImage.setVisibility(View.GONE);
            lvChooseImage.setVisibility(View.VISIBLE);
            // 刷新
            refresh();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 选择好图片后返回
     */
    public void back() {
        if (gvChooseImage.getVisibility() == View.VISIBLE) {
            // 返回时将数据存到ChooseImageActivity的joIsSelected中
            try {
                if (imageGvAdapter.isSelected.size() != 0) {
                    joIsSelected.put(gvFolderName, imageGvAdapter.isSelected);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 返回数据
        Intent i = new Intent();
        i.putExtra("filePath", getPaths());
        setResult(RESULT_OK, i);
        finish();
    }
}
