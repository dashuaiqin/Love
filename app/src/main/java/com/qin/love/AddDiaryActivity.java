package com.qin.love;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.Utils.StringUtils;
import com.qin.Utils.TimeUtils;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.Diary;
import com.qin.myinterface.DialogCallBack;
import com.qin.view.ActionSheetDialog;

import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 查看日记，修改日记，写日记
 */
public class AddDiaryActivity extends BaseActivity {
    private ImageView ivBack;//返回按钮
    private TextView txtTitle;
    private TextView txtTitleRight;//标题栏右边
    private TextView txtDate;//时间
    private EditText editContent;// 内容
    private ImageView ivRight;
    private int type = IntegerCons.TYPE_ADD_DIARY;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        type = getIntent().getIntExtra("type", IntegerCons.TYPE_ADD_DIARY);
        if (type == IntegerCons.TYPE_UPDATE_DIARY) {
            diary = (Diary) getIntent().getSerializableExtra("diary");
        }
        initView();
    }

    /**
     * 界面初始化
     */
    private void initView() {
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitleRight = (TextView) findViewById(R.id.txt_title_right);
        txtDate = (TextView) findViewById(R.id.txt_date);
        txtTitle.setText(StringCons.TITLE_ADD_DIARY);
        txtTitleRight.setText(StringCons.FINISH);
        txtTitleRight.setOnClickListener(listener);
        txtDate.setText(TimeUtils.getDateTime());
        editContent = (EditText) findViewById(R.id.txt_content);
        ivRight = (ImageView) findViewById(R.id.iv_right);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        if (diary != null) {
            txtDate.setText(diary.getSendDate().getDate());
            editContent.setEnabled(false);
            editContent.setText(diary.getContent());
            txtTitle.setText(StringCons.TITLE_READ_DIARY);
            txtTitleRight.setVisibility(View.GONE);
            ivRight.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.add);
            ivRight.setOnClickListener(listener);
        } else {
            showKeyBorad(editContent);
        }
    }

    /**
     * 监听
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_title_right:
                    if (type == IntegerCons.TYPE_ADD_DIARY) {
                        addDiary();
                    } else if (type == IntegerCons.TYPE_UPDATE_DIARY) {
                        updateDiary();
                    }
                    break;
                case R.id.iv_right:
                    new ActionSheetDialog(AddDiaryActivity.this)
                            .builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            editing();
                                        }
                                    })
                            .addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            showUniversalDialog("提示", "确定要删除日记?", "删除", "取消", new DialogCallBack() {
                                                @Override
                                                public void ok(Object obj) {
                                                    deleteDiary();
                                                }

                                                @Override
                                                public void cancle(Object obj) {

                                                }
                                            });
                                        }
                                    }).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 添加日记
     */
    private void addDiary() {
        String content = editContent.getText().toString();
        if (StringUtils.isBlank(content)) {
            shortToast("请输入内容！");
            return;
        }
        Diary diary = new Diary();
        diary.setContent(content);
        diary.setSendDate(BmobDate.createBmobDate("yyyy-MM-dd HH:mm:ss", TimeUtils.getDateTime()));
        diary.setLocation("成都");//暂时写死
        diary.setWeather("晴");
        diary.setUserId(MyApplication.getInstance().getMyUser().getObjectId());
        startProgressDialog("正在保存日记...");
        diary.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopProgressDialog();
                if (e == null) {
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_ADD_DIARY, intent);
                    finish();
                    shortToast("日记保存成功");
                } else {
                    shortToast("保存失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 删除日记
     */
    private void deleteDiary() {
        diary.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_ADD_DIARY, intent);
                    finish();
                } else {
                    shortToast("删除失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 编辑
     */
    private void editing() {
        editContent.setEnabled(true);
        editContent.setSelection(editContent.length());
        showKeyBorad(editContent);
        txtTitle.setText(StringCons.TITLE_UPDATE_DIARY);
        ivRight.setVisibility(View.GONE);
        txtTitleRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (editContent.isEnabled() && !StringUtils.isBlank(editContent.getText().toString())) {
            showUniversalDialog("提示", "您编辑的内容尚未保存，确定退出？", "确定", "取消", new DialogCallBack() {
                @Override
                public void ok(Object obj) {
                    finish();
                }

                @Override
                public void cancle(Object obj) {

                }
            });
        } else {
            finish();
        }
    }

    /**
     * 更新日记
     */
    private void updateDiary() {
        String content = editContent.getText().toString();
        if (StringUtils.isBlank(content)) {
            shortToast("请输入内容！");
            return;
        }
        diary.setContent(content);
        diary.update(diary.getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_UPDATE_DIARY, intent);
                    finish();
                } else {
                    shortToast("保存失败：" + e.getMessage());
                }
            }
        });
    }


}
