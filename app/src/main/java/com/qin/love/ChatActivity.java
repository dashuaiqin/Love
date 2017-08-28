package com.qin.love;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qin.Application.MyApplication;
import com.qin.Utils.CommonUtils;
import com.qin.Utils.FaceTextUtils;
import com.qin.Utils.ImageUtils;
//import com.qin.Utils.NewRecordPlayClickListener;
//import com.qin.adapter.ChatAdapter;
import com.qin.adapter.ChatAdapter;
import com.qin.adapter.EmoViewPagerAdapter;
import com.qin.adapter.EmoteAdapter;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.FaceText;
import com.qin.model.MyUser;
import com.qin.myinterface.DialogCallBack;
//import com.qin.myinterface.MyEventListener;
//import com.qin.receiver.MyMessageReceiver;
import com.qin.view.EmoticonsEditText;
import com.qin.xlistview.XListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;


/**
 * 聊天页面
 */
public class ChatActivity extends BaseActivity implements MessageListHandler, ObseverListener {

    private MyUser myFriend;//另一半
    private Button btnSend;//发送
    private XListView xlChart;//对话列表
    private EmoticonsEditText eeditMessage;//消息编辑框
    private ChatAdapter adapter;//消息适配器
    private List<BmobIMMessage> list;//数据源
    private Handler handler = new Handler();
    //输入框旁边的那一系列按钮
    private Button btnEmo, btnAdd, btnKeyboard, btnSpeak, btnVoice;
    //输入框下面那一坨
    private LinearLayout llMore, llEmo, llAdd;
    private RelativeLayout rlRecord;
    private TextView txtPicture, txtCamera, txtLocation, txtVoiceTips;
    private ViewPager vpEmo;//表情
    private List<FaceText> emos;//表情字符串
    //    private BmobChatManager chatManager;//聊天管理
    private String photoName;//用来保存照的照片名字你们
    private String[] photoPaths;//照片的路径
    private int photoPosition;//路径数组中的位置
    private ImageView ivRecord;//录音动画的载体
    private Drawable[] drawableAnims;// 话筒动画
    private BmobRecordManager recordManager;//录音

    private BmobIMConversation conversation;//会话


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initData();
    }


    /**
     * 界面初始化
     */
    private void initView() {
        xlChart = (XListView) findViewById(R.id.xl_chat);
        xlChart.setPullLoadEnable(false);
        xlChart.setXListViewListener(xlListener);
        initBottomView();
        initVoiceView();
    }

    /**
     * 初始化语音布局
     *
     * @param
     * @return void
     * @throws
     * @Title: initVoiceView
     * @Description: TODO
     */
    private void initVoiceView() {
        rlRecord = (RelativeLayout) findViewById(R.id.layout_record);
        txtVoiceTips = (TextView) findViewById(R.id.tv_voice_tips);
        ivRecord = (ImageView) findViewById(R.id.iv_record);
        btnSpeak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * 初始化录音
     */
    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                // TODO Auto-generated method stub
                ivRecord.setImageDrawable(drawableAnims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    btnSpeak.setPressed(false);
                    btnSpeak.setClickable(false);
                    // 取消录音框
                    rlRecord.setVisibility(View.INVISIBLE);
                    // 发送语音消息
//                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            btnSpeak.setClickable(true);
                        }
                    }, 1000);
                } else {

                }
            }
        });
    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @throws
     * @Title: initVoiceAnimRes
     * @Description: TODO
     */
    private void initVoiceAnimRes() {
        drawableAnims = new Drawable[]{
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    /**
     * 消息接收监听
     *
     * @param list
     */
    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i = 0; i < list.size(); i++) {
            adapter.addMsg(list.get(i).getMessage());
        }
    }

    /**
     * 长按说话监听
     *
     * @author smile
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @date 2014-7-1 下午6:10:16
     */
    class VoiceTouchListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSdCard()) {
                        shortToast("发送语音需要sdcard支持！");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        rlRecord.setVisibility(View.VISIBLE);
                        txtVoiceTips.setText(getString(R.string.voice_cancel_tips));
                        // 开始录音
                        recordManager.startRecording(myFriend.getObjectId());
                    } catch (Exception e) {
                        Log.i("HH", "录音失败：" + e.getMessage());
                        shortToast("录音失败：" + e.getMessage());
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        txtVoiceTips
                                .setText(getString(R.string.voice_cancel_tips));
                        txtVoiceTips.setTextColor(Color.RED);
                    } else {
                        txtVoiceTips.setText(getString(R.string.voice_up_tips));
                        txtVoiceTips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    rlRecord.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// 放弃录音
                            recordManager.cancelRecording();
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
//                                BmobLog.i("voice", "发送语音");
//                                sendVoiceMessage(
//                                        recordManager.getRecordFilePath(myFriend.getObjectId()),
//                                        recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                rlRecord.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * 初始化输入框那一系列的View
     */
    private void initBottomView() {
        // 最左边
        btnEmo = (Button) findViewById(R.id.btn_emo);
        btnAdd = (Button) findViewById(R.id.btn_add);
        btnEmo.setOnClickListener(listener);
        btnAdd.setOnClickListener(listener);
        // 最右边
        btnKeyboard = (Button) findViewById(R.id.btn_keyboard);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(listener);
        btnVoice.setOnClickListener(listener);
        btnKeyboard.setOnClickListener(listener);
        // 最下面
        llMore = (LinearLayout) findViewById(R.id.ll_more);
        llAdd = (LinearLayout) findViewById(R.id.ll_add);
        llEmo = (LinearLayout) findViewById(R.id.ll_emo);
        initAddView();
        initEmoView();

        // 最中间
        // 语音框
        btnSpeak = (Button) findViewById(R.id.btn_speak);
        btnSpeak.setOnClickListener(listener);
        // 输入框
        eeditMessage = (EmoticonsEditText) findViewById(R.id.edit_message);
        eeditMessage.setOnClickListener(listener);
        eeditMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(s)) {
                    btnSend.setVisibility(View.VISIBLE);
                    btnKeyboard.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.GONE);
                } else {
                    if (btnVoice.getVisibility() != View.VISIBLE) {
                        btnVoice.setVisibility(View.VISIBLE);
                        btnSend.setVisibility(View.GONE);
                        btnKeyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @throws
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        Toast toast = new Toast(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    /**
     * 初始化表情布局
     *
     * @param
     * @return void
     * @throws
     * @Title: initEmoView
     * @Description: TODO
     */
    private void initEmoView() {
        vpEmo = (ViewPager) findViewById(R.id.vp_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        vpEmo.setAdapter(new EmoViewPagerAdapter(views));
    }

    /**
     * 初始化加号下面的布局
     */
    private void initAddView() {
        txtPicture = (TextView) findViewById(R.id.txt_picture);
        txtCamera = (TextView) findViewById(R.id.txt_camera);
        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtPicture.setOnClickListener(listener);
        txtLocation.setOnClickListener(listener);
        txtCamera.setOnClickListener(listener);
    }

    /**
     * 获取显示表情的gridView
     *
     * @param i
     * @return
     */
    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (eeditMessage != null && !TextUtils.isEmpty(key)) {
                        int start = eeditMessage.getSelectionStart();
                        CharSequence content = eeditMessage.getText()
                                .insert(start, key);
                        eeditMessage.setText(content);
                        // 定位光标位置
                        CharSequence info = eeditMessage.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    /**
     * 上拉下拉监听
     */
    XListView.IXListViewListener xlListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            handler.postDelayed(new Runnable() {//延时一秒

                @Override
                public void run() {
                    conversation.queryMessages(adapter.getFirstMsg(), 10, new MessagesQueryListener() {
                        @Override
                        public void done(final List<BmobIMMessage> mList, BmobException e) {
                            xlChart.stopRefresh();
                            if (e == null) {

                                if (null != mList && mList.size() > 0) {
                                    final int currents = adapter.getCount();
                                    handler.postDelayed(new Runnable() {//延时130ms
                                        @Override
                                        public void run() {
                                            adapter.insertMsgList(mList);
                                            xlChart.setSelection(adapter.getCount() - currents);
                                        }
                                    }, 130);
                                }
                            } else {
                                shortToast(e.getMessage() + "(" + e.getErrorCode() + ")");
                            }
                        }
                    });
                }
            }, 800);
        }

        @Override
        public void onLoadMore() {

        }
    };
    /**
     * 控件的监听
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send:
                    String mes = eeditMessage.getText().toString();
                    if ("".equals(mes)) {
                        shortToast("您还没有输入消息呢！");
                    } else {
                        sTextMessage(mes);
                        eeditMessage.setText("");
                    }
                    break;
                case R.id.btn_emo:
                    if (llMore.getVisibility() == View.GONE) {
                        showEditState(true);
                    } else {
                        if (llAdd.getVisibility() == View.VISIBLE) {
                            llAdd.setVisibility(View.GONE);
                            llEmo.setVisibility(View.VISIBLE);
                        } else {
                            llMore.setVisibility(View.GONE);
                        }
                    }

                    break;
                case R.id.btn_add:// 添加按钮-显示图片、拍照、位置
                    if (llMore.getVisibility() == View.GONE) {
                        llMore.setVisibility(View.VISIBLE);
                        llAdd.setVisibility(View.VISIBLE);
                        llEmo.setVisibility(View.GONE);
                        hideKeyBorad();
                    } else {
                        if (llEmo.getVisibility() == View.VISIBLE) {
                            llEmo.setVisibility(View.GONE);
                            llAdd.setVisibility(View.VISIBLE);
                        } else {
                            llMore.setVisibility(View.GONE);
                        }
                    }

                    break;
                case R.id.txt_picture://从相册获取照片
                    MyApplication.isSetIcon = false;
                    ImageUtils.getInstance(ChatActivity.this).doGetPhoto();
                    break;
                case R.id.txt_camera://拍照
                    photoName = ImageUtils.getInstance(ChatActivity.this).doTakePhoto();
                    break;
                case R.id.edit_message:// 点击文本输入框
                    xlChart.setSelection(adapter.getCount() - 1);
                    if (llMore.getVisibility() == View.VISIBLE) {
                        llAdd.setVisibility(View.GONE);
                        llEmo.setVisibility(View.GONE);
                        llMore.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_voice:// 语音按钮
                    eeditMessage.setVisibility(View.GONE);
                    llMore.setVisibility(View.GONE);
                    btnVoice.setVisibility(View.GONE);
                    btnKeyboard.setVisibility(View.VISIBLE);
                    btnSpeak.setVisibility(View.VISIBLE);
                    hideKeyBorad();
                    break;
                case R.id.btn_keyboard:// 键盘按钮，点击就弹出键盘并隐藏掉声音按钮
                    showEditState(false);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 根据是否点击笑脸来显示文本输入框的状态
     *
     * @param @param isEmo: 用于区分文字和表情
     * @return void
     * @throws
     * @Title: showEditState
     * @Description: TODO
     */
    private void showEditState(boolean isEmo) {
        eeditMessage.setVisibility(View.VISIBLE);
        btnKeyboard.setVisibility(View.GONE);
        btnVoice.setVisibility(View.VISIBLE);
        btnSpeak.setVisibility(View.GONE);
        eeditMessage.requestFocus();
        if (isEmo) {
            llMore.setVisibility(View.VISIBLE);
            llMore.setVisibility(View.VISIBLE);
            llEmo.setVisibility(View.VISIBLE);
            llAdd.setVisibility(View.GONE);
            hideKeyBorad();
        } else {
            llMore.setVisibility(View.GONE);
            showKeyBorad(eeditMessage);
        }
    }

    //    /**
//     * 聊天的各种事务监听
//     */
//    MyEventListener chartEventListener=new MyEventListener() {
//        @Override
//        public void agreeAddUser() {
//
//        }
//
//        @Override
//        public void onMessage(BmobMsg bmobMsg) {
//           //  ((MainActivity)getActivity()).shortToast("号挂呀！");
//            refreshData();
//        }
//
//        @Override
//        public void onReaded(String conversionId, String msgTime) {
//           // 此处应该过滤掉不是和当前用户的聊天的回执消息界面的刷新
//                // 修改界面上指定消息的阅读状态
//                for (BmobMsg msg : adapter.getList()) {
//                    if (msg.getConversationId().equals(conversionId)
//                            && msg.getMsgTime().equals(msgTime)) {
//                        msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//        }
//
//        @Override
//        public void onNetChange(boolean b) {
//
//        }
//
//        @Override
//        public void onAddUser(final BmobInvitation bmobInvitation) {
//
//        }
//
//        @Override
//        public void onOffline() {
//
//        }
//    };
//
//    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);//消息监听
        BmobNotificationManager.getInstance(this).cancelNotification();//清空通知
//        BmobNotificationManager.getInstance(this).addObserver(this);//通知 观察者
//        BmobNotifyManager.getInstance(this).cancelAll();
//        addEventListener(chartEventListener);
//        if(MyMessageReceiver.mNewNum>0) {
//            refreshData();
//        }
    }
//
//

    /**
     * 发文本消息
     *
     * @param msgText
     */
    public void sTextMessage(String msgText) {
        if (myFriend == null) {
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(msgText);
        //可设置额外信息
        Map<String, Object> map = new HashMap<>();
        map.put("level", "1");//随意增加信息
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void onStart(BmobIMMessage bmobIMMessage) {
                super.onStart(bmobIMMessage);
                if (adapter == null) {
                    list = new ArrayList<BmobIMMessage>();
                    list.add(bmobIMMessage);
                    adapter = new ChatAdapter(ChatActivity.this, list);
                } else {
                    refreshData(bmobIMMessage);
                }
            }

            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                adapter.notifyDataSetChanged();
            }
        });
    }

//    /**
//     * 加载消息历史，从数据库中读出
//     */
//    private List<BmobMsg> initMsgData() {
//        List<BmobMsg> list = null;
//        BmobChatUser myFriends=MyApplication.getInstance().getMyFriends();
//        if (myFriends!=null&&myFriends.getObjectId()!=null) {
//            list = BmobDB.create(this).queryMessages(myFriends.getObjectId(), MsgPagerNum);
//        }
//        return list;
//    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (getIntent().getIntExtra("type", -1) == IntegerCons.TYPE_FROM_NOTIFY_MESSAGE) {//从消息通知来的
            initialization();
        }
        myFriend = MyApplication.getInstance().getMyFriends();
        if (myFriend == null) {
            shortToast("您还没有添加另一半！");
        } else {
            BmobIMUserInfo info = new BmobIMUserInfo(myFriend.getObjectId(), myFriend.getNick(), myFriend.getAvatar());
            BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, false, null);
            conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
            //首次加载，可设置msg为null，
            //下拉刷新的时候，可用消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列，limit由开发者控制
            c.queryMessages(null, 10, new MessagesQueryListener() {
                @Override
                public void done(List<BmobIMMessage> list, BmobException e) {
                    if (e == null) {
                        if (null != list && list.size() > 0) {
                            adapter = new ChatAdapter(ChatActivity.this, list);
                            xlChart.setAdapter(adapter);
                            xlChart.setSelection(adapter.getCount() - 1);
                        }
                    } else {
                        shortToast(e.getMessage() + "(" + e.getErrorCode() + ")");
                    }
                }
            });
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
//        list= initMsgData();
//        if (list==null){
//            return;
//        }
//        adapter.setList(list);
//        xlChart.setSelection(adapter.getCount() - 1);
    }

    /**
     * 发送数据后刷新数据
     */
    private void refreshData(BmobIMMessage message) {
        adapter.addMsg(message);
        xlChart.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("type", -1) == IntegerCons.TYPE_FROM_NOTIFY_MESSAGE) {//从消息通知来的
            startActivity(MainActivity.class, true);
        } else {
            super.onBackPressed();
        }
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
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case IntegerCons.PHOTO_WITH_ALBUM:
//                    String  paths=data.getStringExtra("filePath");
//                    final String[] path=paths.split("\0");
//                    //发送多张图片，没别的办法，只能采取这种恶心的方式了
//                    photoPosition=0;
//                    photoPaths=path;
//                    sendImageMessage(photoPaths[photoPosition]);
//                    break;
//                case IntegerCons.PHOTO_WITH_CAMERA:
//                    sendImageMessage(StringCons.PHOTO_DIR + "/"+photoName);
//                    break;
//                default:
//                    break;
//            }
//        }
    }

    //    /**
//     * 默认先上传本地图片，之后才显示出来 sendImageMessage
//     * @Title: sendImageMessage
//     * @Description: TODO
//     * @param @param localPath
//     * @return void
//     * @throws
//     */
//    private void sendImageMessage(String paths) {
//        if (llMore.getVisibility() == View.VISIBLE) {
//            llMore.setVisibility(View.GONE);
//            llAdd.setVisibility(View.GONE);
//            llEmo.setVisibility(View.GONE);
//        }
//        chatManager.sendImageMessage(myFriend, paths, new UploadListener() {
//            @Override
//            public void onStart(BmobMsg bmobMsg) {
//                refreshData(bmobMsg);
//                if (photoPaths!=null&&bmobMsg.getStatus()==Integer.valueOf(2)){//发送失败
//                    photoPosition++;
//                    if (photoPosition<photoPaths.length) {
//                        sendImageMessage(photoPaths[photoPosition]);
//                    }
//                }
//            }
//
//            @Override
//            public void onSuccess() {
//                adapter.notifyDataSetChanged();
//                if (photoPaths!=null){
//                photoPosition++;
//                if (photoPosition<photoPaths.length) {
//                    sendImageMessage(photoPaths[photoPosition]);
//                }
//                }
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                adapter.notifyDataSetChanged();
//                if (photoPaths!=null) {
//                    photoPosition++;
//                    if (photoPosition < photoPaths.length) {
//                        sendImageMessage(photoPaths[photoPosition]);
//                    }
//                }
//            }
//        });
//
//    }
//    /**
//     * 发送语音消息
//     * @Title: sendVoiceMessage
//     * @Description: TODO
//     * @param @param localPath
//     * @return void
//     * @throws
//     */
//    private void sendVoiceMessage(String local, int length) {
//        chatManager.sendVoiceMessage(myFriend, local, length,
//                new UploadListener() {
//
//                    @Override
//                    public void onStart(BmobMsg msg) {
//                        // TODO Auto-generated method stub
//                        refreshData(msg);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        // TODO Auto-generated method stub
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(int error, String arg1) {
//                        // TODO Auto-generated method stub
////                        ShowLog("上传语音失败 -->arg1：" + arg1);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//    }
//
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
//        BmobNotificationManager.getInstance(this).removeObserver(this);
//        removeEventListener(chartEventListener);
//        // 停止录音
//        if (recordManager.isRecording()) {
//            recordManager.cancelRecording();
//            rlRecord.setVisibility(View.GONE);
//        }
//        // 停止播放录音
//        if (NewRecordPlayClickListener.isPlaying
//                && NewRecordPlayClickListener.currentPlayListener != null) {
//            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BmobNotificationManager.getInstance(this).clearObserver();
    }
}
