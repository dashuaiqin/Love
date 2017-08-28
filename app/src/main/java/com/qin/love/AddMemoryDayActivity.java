package com.qin.love;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qin.Utils.LoveUtils;
import com.qin.Utils.StringUtils;
import com.qin.Utils.TimeUtils;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.MemorialDay;
import com.qin.myinterface.DialogCallBack;
import com.qin.view.SwitchView;
import com.qin.wheelview.LunarCalendar;
import com.qin.wheelview.WheelMain;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2015/12/30.
 */
public class AddMemoryDayActivity  extends BaseActivity {
    private ImageView ivBack;//返回按钮
    private TextView txtTitle;
    private TextView txtTitleRight;//标题栏右边
    private TextView txtDate;//时间
    private EditText editContent;// 内容
    private ImageView ivRight;
    private SwitchView svDate;//农历公历转换
    private int[] choosedDate;//选中的日期.最后一位，农历用来表示是否闰月，1闰月，0不是闰月。-1表示公历
    private int type;//类型
    private MemorialDay memDay;//用来保存传过来的纪念日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memoryday);
        choosedDate = new int[4];
        type=getIntent().getIntExtra("type",IntegerCons.TYPE_ADD_DIARY);
        if (type==IntegerCons.TYPE_UPDATE_MEM){
            memDay= (MemorialDay) getIntent().getSerializableExtra("mem");
            String date[] = memDay.getMemDate().split("%");
            for (int i = 0; i < date.length; i++) {
                choosedDate[i] = Integer.parseInt(date[i]);
            }
        }else {
            String date[] = TimeUtils.getDate().split("-");
            for (int i = 0; i < date.length; i++) {
                choosedDate[i] = Integer.parseInt(date[i]);
            }
            choosedDate[3] = -1;//无用，仅用来占位，与农历保持一致
        }
        initView();
    }
    /**
     * 界面初始化
     */
    private void initView() {
        txtTitle= (TextView) findViewById(R.id.txt_title);
        txtTitleRight= (TextView) findViewById(R.id.txt_title_right);
        txtDate= (TextView) findViewById(R.id.txt_date);
        txtTitle.setText(StringCons.TITLE_ADD_MEMORY_DAY);
        txtTitleRight.setText(StringCons.FINISH);
        txtTitleRight.setOnClickListener(listener);
        txtDate.setText(TimeUtils.getDate());
        txtDate.setOnClickListener(listener);
        editContent= (EditText) findViewById(R.id.edit_content);
        ivRight= (ImageView) findViewById(R.id.iv_right);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);

        svDate= (SwitchView) findViewById(R.id.sv_date);
        svDate.setOpened(true);


        svDate.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                svToSolar();
            }

            @Override
            public void toggleToOff(View view) {
                svToLunar();
            }
        });

        if (memDay!=null){//如果是编辑
            editContent.setText(memDay.getContent());
            txtTitle.setText(StringCons.TITLE_UPDATE_MEMORY_DAY);
            editContent.setSelection(editContent.length());
            String dt=memDay.getMemDate();
            if (!StringUtils.isBlank(dt)) {
                String[] date =dt.split("%");
                if (memDay.isCalenda()) {
                    dt = date[0]+"-"+date[1]+"-"+date[2];
                } else {
                    dt= LunarCalendar.lunartoChineseText(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]),Integer.parseInt(date[3]));
                }
                txtDate.setText(dt);
            }
            svDate.setOpened(memDay.isCalenda());
        }
        showKeyBorad();

    }

    /**
     *sv转成农历的操作
     */
private void svToLunar(){
    txtDate.setText(LunarCalendar.solarToLunarText(choosedDate[0], choosedDate[1], choosedDate[2]));
    choosedDate = LunarCalendar.solarToLunar(choosedDate[0], choosedDate[1], choosedDate[2]);
    Log.i("HH", "农历：" + choosedDate[0] + "-" + choosedDate[1] + "-" + choosedDate[2] + "-" + choosedDate[3]);
}
    /**
     *sv转成公历的操作
     */
private void svToSolar(){
    choosedDate = LunarCalendar.lunarToSolar(choosedDate[0],choosedDate[1],choosedDate[2],choosedDate[3]==1?true:false);
    txtDate.setText(choosedDate[0] + "-" + choosedDate[1] + "-" + choosedDate[2]);
}
    /**
     * 监听
     */
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.txt_title_right:
                    if (type==IntegerCons.TYPE_ADD_MEM){
                    addMemorialDay();}
                    else if(type==IntegerCons.TYPE_UPDATE_MEM){
                        updateMem();
                    }
                    break;
                case R.id.txt_date:
                    showDateDialog();
                    break;

                default:
                    break;
            }
        }
    };
    /**
     * 添加日记
     */
    private void addMemorialDay(){
        String content=editContent.getText().toString();
        if (StringUtils.isBlank(content)){
            shortToast("请输入内容！");
            return;
        }
        MemorialDay memDay=new MemorialDay();
        memDay.setContent(content);
        memDay.setIsCalenda(svDate.isOpened());

        memDay.setLoversId(LoveUtils.getLoversId());
        memDay.setMemDate(choosedDate[0] + "%" + choosedDate[1] + "%" + choosedDate[2] + "%"+choosedDate[3]);
        startProgressDialog("正在保存纪念日...");
        memDay.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) {
                    stopProgressDialog();
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_ADD_MEM, intent);
                    finish();
                    shortToast("保存成功");
                }else{
                    shortToast("保存失败：" + e.getMessage());
                }
            }

        });
    }




    @Override
    public void onBackPressed() {
        if (!StringUtils.isBlank(editContent.getText().toString())&&type==IntegerCons.TYPE_ADD_MEM) {
            showUniversalDialog("提示", "您编辑的内容尚未保存，确定退出？", "确定", "取消", new DialogCallBack() {
                @Override
                public void ok(Object obj) {
                    finish();
                }

                @Override
                public void cancle(Object obj) {

                }
            });
        }else{
            finish();
        }
    }

    /**
     * 显示时间的dialog
     */
    private  void showDateDialog(){
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setCanceledOnTouchOutside(false);// 点击其他地方对话框不消失
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_choose_date);
        window.setLayout((int) (mScreenWidth * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);// 设置宽高

        final WheelMain wheelMain = new WheelMain(this,window.findViewById(R.id.ll_date),svDate.isOpened());
            wheelMain.initDateTimePicker(choosedDate[0], choosedDate[3] == 1 ? choosedDate[1] + 1 : choosedDate[1], choosedDate[2]);


        Button btnDialogOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnDialogCancle = (Button) window
                .findViewById(R.id.btn_dialog_cancle);
        // 确定按钮
        btnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.cancel();
                choosedDate=wheelMain.getTimeArray();
                txtDate.setText(wheelMain.getTimeText());
            }
        });
        // 取消按钮
        btnDialogCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.cancel();
            }
        });
    }

    /**
     * 弹出软键盘
     */
    private void showKeyBorad(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) editContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editContent, 0);
                           }
                       },
                500);
    }

    /**
     * 更新纪念日
     */
    private void updateMem(){
        String content=editContent.getText().toString();
        if (StringUtils.isBlank(content)){
            shortToast("请输入内容！");
            return;
        }
        memDay.setMemDate(choosedDate[0] + "%" + choosedDate[1] + "%" + choosedDate[2] + "%"+choosedDate[3]);
        memDay.setIsCalenda(svDate.isOpened());
        memDay.setContent(content);
        memDay.update( memDay.getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_UPDATE_MEM, intent);
                    shortToast("保存成功");
                    finish();
                }else{
                    shortToast("保存失败：" + e.getMessage());
                }
            }

        });
    }
}