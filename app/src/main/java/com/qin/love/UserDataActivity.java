package com.qin.love;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.Utils.ImageUtils;
import com.qin.Utils.PermissionUtils;
import com.qin.Utils.StringUtils;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.db.CommonDataSp;
import com.qin.model.MyUser;
import com.qin.view.ActionSheetDialog;
import com.qin.wheelview.AddressData;
import com.qin.wheelview.ArrayWheelAdapter;
import com.qin.wheelview.OnWheelChangedListener;
import com.qin.wheelview.ProvinceAdapter;
import com.qin.wheelview.WheelView;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2015/12/14.
 */
public class UserDataActivity extends BaseActivity {

    private ImageView ivIcon, ivBack;
    private Button btnLogout;
    private TextView txtMyId;//我的id
    private TextView txtNickName;//昵称
    private TextView txtSex;//性别
    private TextView txtLocation;//地区
    private RelativeLayout rlUpdateNick;//昵称
    private RelativeLayout rlUpdateSex;//性别
    private RelativeLayout rlUpdateLOcation;//性别
    private String locationCode = "";//地区位置代码
    private String photoName;//用来保存照的照片名字
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        // 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= 14) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
        setContentView(R.layout.activity_user_data);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(listener);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_USER_DATA);

        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        ivIcon.setOnClickListener(listener);
        btnLogout.setOnClickListener(listener);

        txtMyId = (TextView) findViewById(R.id.txt_my_id);
        txtNickName = (TextView) findViewById(R.id.txt_nickname);
        txtSex = (TextView) findViewById(R.id.txt_sex);
        txtLocation = (TextView) findViewById(R.id.txt_location);

        rlUpdateNick = (RelativeLayout) findViewById(R.id.rl_update_nickname);
        rlUpdateNick.setOnClickListener(listener);
        rlUpdateSex = (RelativeLayout) findViewById(R.id.rl_update_sex);
        rlUpdateSex.setOnClickListener(listener);
        rlUpdateLOcation = (RelativeLayout) findViewById(R.id.rl_update_loc);
        rlUpdateLOcation.setOnClickListener(listener);
        initMeData();
    }

    /**
     * 显示我的信息
     */
    private void initMeData() {
        MyUser me = MyApplication.getInstance().getMyUser();
        if (me != null) {
            txtMyId.setText(me.getObjectId());
            txtNickName.setText(me.getNick());
            txtSex.setText(me.getSex() == true ? "男" : "女");
            showLocation(me.getLocationCode());
        }
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_icon:
                    PermissionUtils.requestPermission(UserDataActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
                    break;
                case R.id.btn_logout:
                    BmobUser.logOut();
                    MyApplication.getInstance().setMyUser(null);
                    MyApplication.getInstance().setMyFriends(null);
                    new CommonDataSp(UserDataActivity.this).clear();
                    finish();
                    break;
                case R.id.rl_update_nickname:
                    showNickDialog();
                    break;
                case R.id.rl_update_sex:
                    showSexDialog();
                    break;
                case R.id.rl_update_loc:
                    showLocationDialog();
                    break;
                case R.id.iv_back:
                    finish();
                    break;

                default:
                    break;
            }

        }
    };
    /**
     * 权限获取回调
     */
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    photoName = ImageUtils.getInstance(UserDataActivity.this).doTakePhoto();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    MyApplication.isSetIcon = true;
                    ImageUtils.getInstance(UserDataActivity.this).doGetPhoto();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    new ActionSheetDialog(UserDataActivity.this)
                            .builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem("用相机更换头像", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            PermissionUtils.requestPermission(UserDataActivity.this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
                                        }
                                    })
                            .addSheetItem("去相册选择头像", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            PermissionUtils.requestPermission(UserDataActivity.this, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
                                        }
                                    }).show();
                    break;
                default:
                    break;

            }

        }
    };


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    @Override
    public void onResume() {
        super.onResume();
        showImageByLoader(MyApplication.getInstance().getMyUser().getAvatar(), ivIcon, R.drawable.default_avatar);
    }

    /**
     * 接收拍照
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IntegerCons.PHOTO_WITH_CAMERA:
                    Intent i = new Intent(UserDataActivity.this, CutPicActivity.class);
                    i.putExtra("path", StringCons.PHOTO_DIR + "/" + photoName);
                    startActivity(i);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示修改昵称的dialog
     */
    public void showNickDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setCanceledOnTouchOutside(false);// 点击其他地方对话框不消失
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_update_nick);
        window.setLayout((int) (mScreenWidth * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);// 设置宽高
        // 点击输入框不弹出软键盘的解决办法
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final EditText editNick = (EditText) window.findViewById(R.id.edit_nick);
        Button btnDialogOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnDialogCancle = (Button) window
                .findViewById(R.id.btn_dialog_cancle);
        // 确定按钮
        btnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String nick = editNick.getText().toString();
                if ("".equals(nick)) {
                    shortToast("昵称不能为空！");
                } else {
                    dlg.cancel();
                    startProgressDialog();
                    updateNick(nick);
                }
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
     * 修改昵称
     *
     * @param nick
     */
    public void updateNick(final String nick) {
        MyUser user = new MyUser();
        user.setNick(nick);
        user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if (e == null) {
                    shortToast("昵称修改成功！");
                    txtNickName.setText(nick);
                    MyApplication.getInstance().getMyUser().setNick(nick);
                } else {
                    shortToast("昵称修改失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 显示修改性别的dialog
     */
    public void showSexDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setCanceledOnTouchOutside(false);// 点击其他地方对话框不消失
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_update_sex);
        window.setLayout((int) (mScreenWidth * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);// 设置宽高
        // 点击输入框不弹出软键盘的解决办法
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final RadioGroup rgSex = (RadioGroup) window.findViewById(R.id.rg_sex);
        MyUser me = MyApplication.getInstance().getMyUser();
        if (me != null) {
            rgSex.check(me.getSex() == true ? R.id.rb_male : R.id.rb_female);
        }
        Button btnDialogOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnDialogCancle = (Button) window
                .findViewById(R.id.btn_dialog_cancle);
        // 确定按钮
        btnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.cancel();
                startProgressDialog();
                updateSex(rgSex.getCheckedRadioButtonId() == R.id.rb_male ? true : false);
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
     * 修改性别
     *
     * @param ismale 是否是男
     */
    private void updateSex(final boolean ismale) {
        MyUser user = new MyUser();
        user.setSex(ismale);
        user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if (e == null) {
                    shortToast("性别修改成功！");
                    txtSex.setText(ismale == true ? "男" : "女");
                    MyApplication.getInstance().getMyUser().setSex(ismale);
                } else {
                    shortToast("性别修改失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 显示地区的dialog
     */
    private void showLocationDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setCanceledOnTouchOutside(false);// 点击其他地方对话框不消失
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_choose_location);
        window.setLayout((int) (mScreenWidth * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);// 设置宽高
        // 点击输入框不弹出软键盘的解决办法
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final WheelView province = (WheelView) window
                .findViewById(R.id.wheelcity_province);
        province.setVisibleItems(3);
        province.setViewAdapter(new ProvinceAdapter(this));

        final String cities[][] = AddressData.CITIES;
        final WheelView city = (WheelView) window
                .findViewById(R.id.wheelcity_city);
        city.setVisibleItems(3);
        province.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(View wheel, int oldValue, int newValue) {
                refreshCities(city, cities, newValue);
                locationCode = province.getCurrentItem() + ":" + city.getCurrentItem();
            }
        });
        city.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(View wheel, int oldValue, int newValue) {
                locationCode = province.getCurrentItem() + ":" + city.getCurrentItem();
            }
        });
        MyUser me = MyApplication.getInstance().getMyUser();
        if (!StringUtils.isBlank(me.getLocationCode())) {
            String[] position = me.getLocationCode().split(":");
            province.setCurrentItem(Integer.parseInt(position[0]));
            refreshCities(city, cities, Integer.parseInt(position[0]));
            city.setCurrentItem(Integer.parseInt(position[1]));
        } else {
            province.setCurrentItem(0);
            refreshCities(city, cities, 0);
            city.setCurrentItem(0);
        }

        Button btnDialogOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnDialogCancle = (Button) window
                .findViewById(R.id.btn_dialog_cancle);
        // 确定按钮
        btnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.cancel();
                startProgressDialog();
                updateLocation(locationCode);
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
     * Updates the city wheel
     */
    private void refreshCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
                cities[index]);
        adapter.setTextSize(16);
        city.setViewAdapter(adapter);
        city.setCurrentItem(0);
    }

    /**
     * 修改地区
     *
     * @param code
     */
    public void updateLocation(final String code) {
        MyUser user = new MyUser();
        user.setLocationCode(code);
        user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if(e==null){
                    shortToast("地区修改成功！");
                    showLocation(code);
                    MyApplication.getInstance().getMyUser().setLocationCode(code);
                }else{
                    shortToast("地区修改失败：" + e.getMessage());
                }
            }
        });
    }

    /**
     * 显示地区
     *
     * @param code
     */
    private void showLocation(String code) {
        if (code != null && !"".equals(code)) {
            String[] position = code.split(":");
            String city = AddressData.CITIES[Integer.parseInt(position[0])][Integer.parseInt(position[1])];
            txtLocation.setText(city);
        }
    }
}
