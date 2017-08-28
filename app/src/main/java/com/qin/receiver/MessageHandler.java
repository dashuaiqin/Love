package com.qin.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.qin.Application.MyApplication;
import com.qin.cons.IntegerCons;
import com.qin.headsup.HeadsUp;
import com.qin.headsup.HeadsUpManager;
import com.qin.love.ChatActivity;
import com.qin.love.R;
import com.qin.model.Dim;
import com.qin.model.MyUser;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MessageHandler extends BmobIMMessageHandler {
    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessageReceive(final MessageEvent event) {
        //当接收到服务器发来的消息时，此方法被调用
        excuteMessage(event);
    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
    }

    /**
     * 处理消息
     *
     * @param event
     */
    private void excuteMessage(final MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (BmobIMMessageType.getMessageTypeValue(msg.getMsgType()) == 0) {//用户自定义的消息类型，其类型值均为0
            processCustomMessage(msg, event.getFromUserInfo());
        } else {//SDK内部内部支持的消息类型
            if (BmobIM.getInstance().messageListeners.size() == 0) {//没有监听时，显示通知栏，SDK提供以下两种显示方式：
                Intent pendingIntent = new Intent(context, ChatActivity.class);
                pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //1、多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
                permission(pendingIntent, msg);
                BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);
                //2、自定义通知消息：始终只有一条通知，新消息覆盖旧消息
//                        BmobIMUserInfo info =event.getFromUserInfo();
//                        //这里可以是应用图标，也可以将聊天头像转成bitmap
//                        Bitmap largetIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//                        BmobNotificationManager.getInstance(context).showNotification(largetIcon,
//                                info.getName(),msg.getContent(),"您有一条新消息",pendingIntent);
            } else {//直接发送消息事件
//                        Logger.i("当前处于应用内，发送event");
//                        EventBus.getDefault().post(event);
            }
        }
    }

    /**
     * 处理自定义消息类型
     *
     * @param msg
     */
    private void processCustomMessage(BmobIMMessage msg, BmobIMUserInfo info) {
        //自行处理自定义消息类型
        Toast.makeText(context, msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra(), Toast.LENGTH_SHORT).show();

        String type = msg.getMsgType();
        //处理消息
        if (type.equals("add")) {//接收到的添加好友的请求
            EventBus.getDefault().post(msg);
        } else if (type.equals("agree")) {//接收到的对方同意添加自己为好友,此时需要做的事情：1、添加对方为好友，2、显示通知
            updateMyFriend(msg);
        } else {
            Toast.makeText(context, "接收到的自定义消息：" + msg.getMsgType() + "," + msg.getContent() + "," + msg.getExtra(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示与聊天消息的通知
     *
     * @return void
     * @throws
     * @Title: showNotify
     */
    public void showMsgNotify(Intent pendingIntent, BmobIMMessage msg) {
        PendingIntent hupendingIntent = PendingIntent.getActivity(context, 11, pendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        HeadsUpManager manage = HeadsUpManager.getInstant(context);
        HeadsUp.Builder builder = new HeadsUp.Builder(context);
        builder.setContentTitle(msg.getBmobIMUserInfo().getName()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                //要显示通知栏通知,这个一定要设置
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIconUrl(msg.getBmobIMUserInfo().getAvatar())
                        //2.3 一定要设置这个参数,负责会报错
                .setContentIntent(hupendingIntent)
//                        .setFullScreenIntent(pendingIntent, false)
                .setContentText(msg.getContent())
                .setAutoCancel(true);//设置这个标志当用户单击面板就可以让通知将自动取消

        HeadsUp headsUp = builder.buildHeadUp();
        headsUp.setSticky(false);
        headsUp.setActivateStatusBar(false);
        manage.notify(2, headsUp);
    }

    /**
     * 修改用户对应的另一半
     *
     * @param msg
     */
    public void updateMyFriend(final BmobIMMessage msg) {
        final MyUser friend = new MyUser();
        try {
            JSONObject extra = new JSONObject(msg.getExtra());
            friend.setAvatar(extra.getString("avatar"));
            friend.setObjectId(extra.getString("objecteId"));
            friend.setNick(extra.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!friend.getObjectId().equals(MyApplication.getInstance().getMyUser().getObjectId())) {
            MyUser user = new MyUser();
            user.setMyFriend(friend.getObjectId());
            user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyApplication.getInstance().getMyUser().setMyFriend(friend.getObjectId());
                        EventBus.getDefault().post(friend);
                    } else {

                    }
                }
            });
        }
    }

    public void permission(Intent pendingIntent, BmobIMMessage msg) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            } else {
                showMsgNotify(pendingIntent, msg);
            }
        } else {
            //Android6.0以下，不用动态声明权限
            showMsgNotify(pendingIntent, msg);
        }
    }


}