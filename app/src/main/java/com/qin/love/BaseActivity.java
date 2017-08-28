package com.qin.love;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qin.Application.MyApplication;
import com.qin.cons.IntegerCons;
import com.qin.model.MyUser;
import com.qin.myinterface.DialogCallBack;
import com.qin.view.CustomProgressDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

//import com.qin.myinterface.MyEventListener;
//import com.qin.receiver.MyMessageReceiver;
//import cn.bmob.im.BmobUserManager;
//import cn.bmob.im.bean.BmobChatUser;
//import cn.bmob.im.bean.BmobInvitation;
//import cn.bmob.im.db.BmobDB;

/**
 * Created by Administrator on 2015/10/30.
 */
public class BaseActivity extends Activity {
    protected CustomProgressDialog proGressDialog;//进度dialog
    public static int mScreenWidth;// 屏幕宽
    public static int mScreenHeight;// 屏幕高

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        //沉浸式状态栏初始化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initTransparentStatusBar();
    }

    /*
        * transparent status bar
        * */
    public void initTransparentStatusBar() {
        SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
        systemBarTintManager.setStatusBarTintEnabled(true);
//        systemBarTintManager.setNavigationBarTintEnabled(true);
        systemBarTintManager.setTintColor(0);
        final Drawable drawable = ContextCompat.getDrawable(this, R.color.title_bg);
        systemBarTintManager.setStatusBarTintDrawable(drawable);
    }

    /**
     * 短提示
     */
    public void shortToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长提示
     */
    public void longToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
    }

    /**
     * 跳转到Activity
     *
     * @param cls
     * @param isFinish
     */
    public void startActivity(Class<?> cls, boolean isFinish) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        startActivity(intent);
        if (isFinish) {
            this.finish();
        }
    }

    /***
     * 弹出进度显示
     */
    public void startProgressDialog() {
        if (proGressDialog == null) {
            proGressDialog = new CustomProgressDialog(this);
        }
        proGressDialog.show();
    }

    /***
     * 弹出进度显示，并显示进度
     */
    public CustomProgressDialog startProgressDialog(String message) {
        if (proGressDialog == null) {
            proGressDialog = new CustomProgressDialog(this);
        }
        proGressDialog.show();
        setProDiaMessage(message);
        return proGressDialog;
    }

    /**
     * 显示进度
     *
     * @param message
     */
    public void setProDiaMessage(String message) {
        if (proGressDialog != null && proGressDialog.txtExplain != null) {
            proGressDialog.txtExplain.setText(message);
        }
    }

    /***
     * 隐藏进度显示
     */
    public void stopProgressDialog() {

        if (proGressDialog != null && proGressDialog.isShowing()) {
            proGressDialog.dismiss();
            proGressDialog = null;
        }
    }

    /**
     * 显示通用的Dialog,通过接口回调实现通用
     *
     * @param callback
     */
    public void showUniversalDialog(final String title, final String explain, final String ok,
                                    final String cancle, final DialogCallBack callback) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.setCanceledOnTouchOutside(false);// 点击其他地方对话框不消失
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_universal);
        window.setLayout((int) (mScreenWidth * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);// 设置宽高
        // 点击输入框不弹出软键盘的解决办法
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        TextView txtTitle = (TextView) window.findViewById(R.id.txt_title);
        TextView txtExpliain = (TextView) window.findViewById(R.id.txt_explain);
        if (title != null)
            txtTitle.setText(title);
        txtExpliain.setText(explain);
        Button btnDialogOk = (Button) window.findViewById(R.id.btn_dialog_ok);
        Button btnDialogCancle = (Button) window
                .findViewById(R.id.btn_dialog_cancle);
        if (ok != null)
            btnDialogOk.setText(ok);
        if (cancle != null)
            btnDialogCancle.setText(cancle);
        // 确定按钮
        btnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callback.ok(dlg);
                dlg.cancel();
            }
        });
        // 取消按钮
        btnDialogCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.cancle(dlg);
                dlg.cancel();
            }
        });
    }

    /**
     * 添加好友
     * agressAdd
     *
     * @param @param btn_add
     * @param @param msg
     * @return void
     * @throws
     * @Title: agressAdd
     * @Description: TODO
     */
//    public void agressAdd(final BmobInvitation msg) {
//        startProgressDialog();
//        try {
//            //同意添加好友
//            BmobUserManager.getInstance(this).agreeAddContact(msg, new UpdateListener() {
//
//                @Override
//                public void onSuccess() {
//                    stopProgressDialog();
//                    updateMyFriend(msg.getFromid());
//                }
//
//                @Override
//                public void onFailure(int arg0, final String arg1) {
//                    stopProgressDialog();
//                    shortToast("添加失败: " + arg1);
//                }
//            });
//        } catch (final Exception e) {
//            stopProgressDialog();
//            shortToast("添加失败: " + e.getMessage());
//        }
//    }

    /**
     * 修改用户对应的另一半
     *
     * @param myFriends
     */
    public void updateMyFriend(final String myFriends) {
        MyUser user = new MyUser();
        user.setMyFriend(myFriends);
        user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if (e == null) {
                    MyApplication.getInstance().getMyUser().setMyFriend(myFriends);
                    getMyFriends(myFriends);
                    shortToast("配对成功！");
                } else {
                    shortToast(e.getMessage());
                }

            }
        });
    }

    /**
     * 取缓存，用于自动登录
     */
    public void initialization() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser != null) {
            Log.i("life", "本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
                    + ",age = " + myUser.getAge());
            MyApplication.getInstance().setMyUser(myUser);
            getMyFriends(MyApplication.getInstance().getMyUser().getMyFriend());
        } else {
            shortToast("您还未登录");
        }

    }

    /**
     * 获取到另一半的信息
     *
     * @param friendId
     */
    public void getMyFriends(String friendId) {
        if (friendId == null) {
            return;
        }
        if (MyApplication.getInstance().getMyUser() == null) {
            return;
        }
//        List<BmobChatUser> contacts = BmobDB.create(this).getContactList();
//        Log.i("HH", "length：" + contacts.size());
//        for (BmobChatUser myFriends : contacts) {
//            Log.i("HH", "friendId：" + friendId + ":" + myFriends.getObjectId());
//            if (friendId.equals(myFriends.getObjectId())) {
//                MyApplication.getInstance().setMyFriends(myFriends);
//            }
//        }
        //查找Person表里面id为6b6c11c537的数据
        BmobQuery<MyUser> bmobQuery = new BmobQuery<MyUser>();

        bmobQuery.getObject(friendId, new QueryListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if (e == null) {
                    MyApplication.getInstance().setMyFriends(myUser);
                }
            }
        });


    }

    /**
     * 返回按钮
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 弹出软键盘
     */
    public void showKeyBorad(final EditText editText) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                200);
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyBorad() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 通过imageloader显示图片
     */
    public void showImageByLoader(String imageUrl, final ImageView imageView, int loading) {
        DrawableTypeRequest<String> dt = Glide.with(getApplicationContext()).load(imageUrl);
        if (loading != IntegerCons.NONE) {
            dt.placeholder(loading);
        }
        dt.diskCacheStrategy(DiskCacheStrategy.ALL);
        dt.into(imageView);
    }

    /**
     * 移出聊天事务监听
     */
//    public void removeEventListener(MyEventListener myEventListener) {
//        if (MyMessageReceiver.ehList.contains(myEventListener)) {
//            MyMessageReceiver.ehList.remove(myEventListener);
//        }
//    }

    /**
     * 添加聊天事务监听
     */
//    public void addEventListener(MyEventListener myEventListener) {
//        if (!MyMessageReceiver.ehList.contains(myEventListener)) {
//            MyMessageReceiver.ehList.add(myEventListener);
//        }
//    }

    /**
     * 播放提示音
     */
    public void playHintSound() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
    }

    /**
     * 链接聊天服务器
     */
    public void connectImService() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            getMyFriends(user.getMyFriend());
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        //EventBus.getDefault().post(new RefreshEvent());
                        shortToast("聊天服务器连接成功");
                    } else {
                        shortToast("聊天服务器连接失败" + e.getErrorCode() + "/" + e.getMessage());
                    }
                }
            });
        }
    }
}
