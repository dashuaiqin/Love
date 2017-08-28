package com.qin.love;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.fragment.CenterFragment;
import com.qin.fragment.HomeFragment;
import com.qin.fragment.TimeFragment;
import com.qin.model.AddFriendMessage;
import com.qin.model.AgreeAddFriendMessage;
import com.qin.model.Dim;
import com.qin.model.MessageNum;
import com.qin.model.MyUser;
import com.qin.myinterface.DialogCallBack;
//import com.qin.myinterface.MyEventListener;
//import com.qin.receiver.MyMessageReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectChangeReceiver;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

//import cn.bmob.im.bean.BmobInvitation;
//import cn.bmob.im.bean.BmobMsg;
//import cn.bmob.im.config.BmobConfig;

public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    List<Integer> temp = new ArrayList<Integer>();
    public List<Fragment> fragments = new ArrayList<Fragment>();
    static boolean flag = true;
    private int currentTab; // 当前Tab页面索引
    private long exitTime = 0;// 退出的初始化时间
    private TextView txtMsgNum;//消息个数
    private ConnectChangeReceiver connectChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        MyApplication.mScreenWidth = dm.widthPixels;
        MyApplication.mScreenHeight = dm.heightPixels;
        initFragment();
        setRadioGroupListener();
//        if (getIntent().getIntExtra("type",-1)== IntegerCons.TYPE_FROM_NOTIFY_MESSAGE){//从消息通知来的(添加好友)
//            final BmobInvitation bmobInvitation= (BmobInvitation) getIntent().getSerializableExtra("message");
//            if (bmobInvitation!=null) {
//                showUniversalDialog("请求添加好友", bmobInvitation.getFromname() + "请求添加您为好友！", "同意", "拒绝", new DialogCallBack() {
//                    @Override
//                    public void ok(Object obj) {
//                        agressAdd(bmobInvitation);//同意添加
//                    }
//
//                    @Override
//                    public void cancle(Object obj) {
//
//                    }
//                });
//            }
//        }
        txtMsgNum = (TextView) findViewById(R.id.txt_msg_num);
        connectImService();
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                if (status.getCode() == 0) {
                }
            }
        });
        //注册EventBus
        EventBus.getDefault().register(this);
//        registerNetReceiver();
    }



    @Override
    protected void onResume() {
        super.onResume();
        initialization();
//        if (currentTab == 0) {
//            ((HomeFragment) getCurrentFragment()).getCommonData();
//        }

//        addEventListener(chartEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(connectChangeReceiver!=null) {
            unregisterReceiver(connectChangeReceiver);
            connectChangeReceiver=null;
        }
    }

    /**
     * 动态注册网络监听广播
     */
    private void registerNetReceiver(){
        connectChangeReceiver=new ConnectChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("cn.bmob.action.RECONNECT");			//添加动态广播的Action
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");			//添加动态广播的Action
        filter.addAction("android.intent.action.BOOT_COMPLETED");			//添加动态广播的Action
        filter.addAction("android.intent.action.USER_PRESENT");			//添加动态广播的Action
        registerReceiver(connectChangeReceiver, filter);	// 注册自定义动态广播消息
        shortToast("注册广播！");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == IntegerCons.TYPE_ADD_TIME && currentTab == 1) {
            ((TimeFragment) getCurrentFragment()).refresh();
        }
        if (resultCode == IntegerCons.TYPE_HOME_BG && currentTab == 0) {
            ((HomeFragment) getCurrentFragment()).onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK && currentTab == 0) {
            ((HomeFragment) getCurrentFragment()).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        fragments.add(new HomeFragment());
        fragments.add(new TimeFragment());
        fragments.add(new CenterFragment());
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        // 默认显示第一页
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content, fragments.get(0));
        temp.add(R.id.rb_home);
        transaction.commit();
    }

    // 设置改变事件
    private void setRadioGroupListener() {
        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        temp.add(checkedId);
                        showCurrentFragment(checkedId);
                    }
                });

    }

    /**
     * 设置显示checkedId对应的fragment
     *
     * @param checkedId
     */
    private void showCurrentFragment(int checkedId) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            if (radioGroup.getChildAt(i).getId() == checkedId) {
                Fragment fragment = fragments.get(i);
                FragmentTransaction transaction = fragmentManager
                        .beginTransaction();
                getCurrentFragment().onPause(); // 暂停当前tab
                if (fragment.isAdded()) {
                    fragment.onResume(); // 启动目标tab的onResume()
                } else {
                    transaction.add(R.id.content, fragment);
                }
                showTab(i);
                transaction.commit();
            }
        }
    }

    /**
     * 显示目标tab
     *
     * @param idx
     */
    private void showTab(int idx) {
        if (idx == 0) {
            txtMsgNum.setVisibility(View.GONE);
        }
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();

            if (idx == i) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
            transaction.commit();
        }
        currentTab = idx; // 更新目标tab为当前tab
    }

    /**
     * 设置当前界面以及状态
     *
     * @param indexPreView
     */
    protected void setPreView(int indexPreView) {
        RadioButton PreViewbtn = (RadioButton) findViewById(indexPreView);
        PreViewbtn.setChecked(true);
        showCurrentFragment(indexPreView);
        temp.clear();
        temp.add(indexPreView);
    }

    /**
     * 获取当前Tab
     *
     * @return
     */
    public int getCurrentTab() {
        return currentTab;
    }

    /**
     * 获取当前fragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        return fragments.get(currentTab);
    }

    // 避免页面的重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        // super.onSaveInstanceState(outState);
    }

    /**
     * 退出
     */
    public void exitSystem() {
        if ((System.currentTimeMillis() - exitTime) > 1500) {
            shortToast(StringCons.EXIT);
            exitTime = System.currentTimeMillis();
        } else {
            MyApplication.getInstance().exitAll();
        }
    }

    /**
     * 点击返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exitSystem();
        }
        return true;
    }

    /**
     * 聊天的各种事务监听
     */
//    MyEventListener chartEventListener = new MyEventListener() {
//        @Override
//        public void agreeAddUser() {
//
//        }
//
//        @Override
//        public void onMessage(BmobMsg bmobMsg) {
//            playHintSound();
//            MyMessageReceiver.mNewNum+=1;
//            EventBus.getDefault().post(
//                    new MessageNum(MyMessageReceiver.mNewNum));
//            if (currentTab!=0){
//                txtMsgNum.setVisibility(View.VISIBLE);
//                txtMsgNum.setText(MyMessageReceiver.mNewNum + "");
//            }
//        }
//
//        @Override
//        public void onReaded(String conversionId, String msgTime) {
//
//        }
//
//        @Override
//        public void onNetChange(boolean b) {
//
//        }
//
//        @Override
//        public void onAddUser(final BmobInvitation bmobInvitation) {
//            playHintSound();
//            showUniversalDialog("请求添加好友", bmobInvitation.getFromname() + "请求添加您为好友！", "同意", "拒绝", new DialogCallBack() {
//                @Override
//                public void ok(Object obj) {
//                    agressAdd(bmobInvitation);//同意添加
//                }
//
//                @Override
//                public void cancle(Object obj) {
//
//                }
//            });
//        }
//
//        @Override
//        public void onOffline() {
//
//        }
//    };
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
//        removeEventListener(chartEventListener);
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    /**
     * eventbus订阅函数
     *
     * @param msg
     */
    @Subscribe
    public void onEventMainThread(final BmobIMMessage msg) {
        if (!msg.getMsgType().equals("add")) {
            return;
        }
        playHintSound();
        try {
            showUniversalDialog("请求添加好友", new JSONObject(msg.getExtra()).getString("name") + "请求添加您为好友:" + msg.getContent(), "同意", "拒绝", new DialogCallBack() {
                @Override
                public void ok(Object obj) {
                    //                agressAdd(bmobInvitation);//同意添加
                    try {
                        JSONObject extra = new JSONObject(msg.getExtra());
                        MyUser friend = new MyUser();
                        friend.setAvatar(extra.getString("avatar"));
                        friend.setObjectId(extra.getString("objecteId"));
                        friend.setNick(extra.getString("name"));
                        sendAgreeAddFriendMessage(friend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void cancle(Object obj) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * eventbus订阅函数
     *
     * @param friend
     */
    @Subscribe
    public void onEventAsync(MyUser friend) {
        updateMyFriend(friend.getObjectId());
    }


    /**
     * 发送同意添加好友的请求
     */
    private void sendAgreeAddFriendMessage(final MyUser add) {
        BmobIMUserInfo info = new BmobIMUserInfo(add.getObjectId(), add.getNick(), add.getAvatar());
        //如果为true,则表明为暂态会话，也就是说该会话仅执行发送消息的操作，不会保存会话和消息到本地数据库中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        msg.setContent("我通过了你的好友验证请求，我们可以开始聊天了!");//---这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getNick());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("objecteId", currentUser.getObjectId());
        map.put("avatar", currentUser.getAvatar());
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    updateMyFriend(add.getObjectId());
                } else {//发送失败
                    shortToast("添加好友失败！");
                }
            }
        });
    }


}
