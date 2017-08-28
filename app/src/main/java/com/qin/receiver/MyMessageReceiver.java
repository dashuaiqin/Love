//package com.qin.receiver;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.qin.Application.MyApplication;
//import com.qin.cons.IntegerCons;
//import com.qin.headsup.HeadsUp;
//import com.qin.headsup.HeadsUpManager;
//import com.qin.love.ChatActivity;
//import com.qin.love.MainActivity;
//import com.qin.love.R;
//import com.qin.model.MyUser;
//import com.qin.myinterface.MyEventListener;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.bmob.im.BmobChatManager;
//import cn.bmob.im.BmobNotifyManager;
//import cn.bmob.im.BmobUserManager;
//import cn.bmob.im.bean.BmobChatUser;
//import cn.bmob.im.bean.BmobInvitation;
//import cn.bmob.im.bean.BmobMsg;
//import cn.bmob.im.config.BmobConfig;
//import cn.bmob.im.config.BmobConstant;
//import cn.bmob.im.db.BmobDB;
//import cn.bmob.im.inteface.OnReceiveListener;
//import cn.bmob.im.util.BmobJsonUtil;
//import cn.bmob.im.util.BmobLog;
//import cn.bmob.v3.listener.FindListener;
//import cn.bmob.v3.listener.UpdateListener;
//
//public class MyMessageReceiver extends BroadcastReceiver {
//    // 事件监听列表
//    public static ArrayList<MyEventListener> ehList = new ArrayList<MyEventListener>();
//    public static int mNewNum = 0;//新消息条数
//    private Context context;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String json = intent.getStringExtra("msg");
//        Log.i("HH", "收到的message = " + json);
////        Toast.makeText(context,"收到的message = " + json,Toast.LENGTH_LONG).show();
//        parseMessage(context, json);
//        this.context = context;
//        //省略其他代码
//    }
//
//    /**
//     * 解析Json字符串
//     *
//     * @param @param context
//     * @param @param json
//     * @return void
//     * @throws
//     * @Title: parseMessage
//     * @Description: TODO
//     */
//    private void parseMessage(final Context context, String json) {
//        JSONObject jo;
//        try {
//            jo = new JSONObject(json);
//            String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
//            if (tag.equals(BmobConfig.TAG_OFFLINE)) {//下线通知
//
//            } else {
//                final String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
//                //增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
//                final String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
//                String msgTime = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_MSGTIME);
//                if (fromId != null && !BmobDB.create(context, toId).isBlackUser(fromId)) {//该消息发送方不为黑名单用户
//                    if (TextUtils.isEmpty(tag)) {//不携带tag标签--此可接收陌生人的消息
//                        BmobChatManager.getInstance(context).createReceiveMsg(json, new OnReceiveListener() {
//
//                            @Override
//                            public void onSuccess(BmobMsg msg) {
//                                if (ehList.size() > 0) {// 有监听的时候，传递下去,每个监听依次执行，哈哈哈
//                                    for (MyEventListener handler : ehList)
//                                        handler.onMessage(msg);
//                                } else {//没得监听
//                                    mNewNum++;
//                                    showMsgNotify(context, msg);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(int code, String arg1) {
//
//                            }
//                        });
//
//                    } else {//带tag标签
//                        if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {//加好友
//                            //保存好友请求道本地，并更新后台的未读字段
//                            BmobInvitation message = BmobChatManager.getInstance(context).saveReceiveInvite(json, toId);
//                            if (toId.equals(MyApplication.getInstance().getMyUser().getObjectId())) {
//                                if (ehList.size() > 0) {// 有监听的时候，传递下去,每个监听依次执行，哈哈哈
//                                    for (MyEventListener handler : ehList)
//                                        handler.onAddUser(message);
//                                } else {//没得监听
//                                    showOtherNotify(context, message, message.getFromname() + "请求添加好友");
//                                }
//                            }
//                        } else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {//同意添加好友
//                            final String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
//                            if (!fromId.equals(MyApplication.getInstance().getMyUser().getObjectId())) {//不是自己加自己
//                                //收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
//                                BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {
//
//                                    @Override
//                                    public void onError(int arg0, final String arg1) {
//                                        // TODO Auto-generated method stub
//
//                                    }
//
//                                    @Override
//                                    public void onSuccess(List<BmobChatUser> arg0) {
//                                        updateMyFriend(context, fromId, username);
//                                    }
//                                });
//                                //创建一个临时验证会话--用于在会话界面形成初始会话
//                                BmobMsg.createAndSaveRecentAfterAgree(context, json);
//                            }
//
//                        } else if (tag.equals(BmobConfig.TAG_READED)) {//已读回执
//                            String conversionId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_READED_CONVERSIONID);
//
//                        }
//                    }
//                } else {//在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
//                    BmobChatManager.getInstance(context).updateMsgReaded(true, fromId, msgTime);
//                    BmobLog.i("该消息发送方为黑名单用户");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            //这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
//            BmobLog.i("parseMessage错误：" + e.getMessage());
//        }
//    }
//
//    /**
//     * 显示与聊天消息的通知
//     *
//     * @return void
//     * @throws
//     * @Title: showNotify
//     */
//    public void showMsgNotify(Context context, BmobMsg msg) {
//
//
//        // 更新通知栏
//        int icon = R.mipmap.ic_launcher;
//        String trueMsg = "";
//        if (msg.getMsgType() == BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")) {
//            trueMsg = "[表情]";
//        } else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
//            trueMsg = "[图片]";
//        } else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
//            trueMsg = "[语音]";
//        } else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
//            trueMsg = "[位置]";
//        } else {
//            trueMsg = msg.getContent();
//        }
//        CharSequence tickerText = trueMsg;
//        String contentTitle = msg.getBelongNick() + " (" + mNewNum + "条新消息)";
//        String largeIconUrl=msg.getBelongAvatar();
//
//        Intent intent = new Intent(context, ChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("type", IntegerCons.TYPE_FROM_NOTIFY_MESSAGE);
//
////        boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
////        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
//
////        BmobNotifyManager.getInstance(context).showNotifyWithExtras(true, true, icon, tickerText.toString(), contentTitle, tickerText.toString(), intent);
//
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        HeadsUpManager manage = HeadsUpManager.getInstant(context);
//        HeadsUp.Builder builder = new HeadsUp.Builder(context);
//        builder.setContentTitle(contentTitle).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
//                //要显示通知栏通知,这个一定要设置
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIconUrl(largeIconUrl)
//                        //2.3 一定要设置这个参数,负责会报错
//                .setContentIntent(pendingIntent)
////                .setFullScreenIntent(pendingIntent,false)
//                .setContentText(tickerText.toString())
//                .setAutoCancel(true);//设置这个标志当用户单击面板就可以让通知将自动取消
//
//        HeadsUp headsUp = builder.buildHeadUp();
//        headsUp.setSticky(false);
//        manage.notify(2, headsUp);
//    }
//
//
//    /**
//     * 显示其他Tag的通知
//     * showOtherNotify
//     */
//    public void showOtherNotify(Context context, BmobInvitation message, String ticker) {
////        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowPushNotify();
////        boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
////        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
////        if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("type", IntegerCons.TYPE_FROM_NOTIFY_MESSAGE);
//        if (message != null) {
//            intent.putExtra("message", message);
//        }
//
////        boolean isAllowVoice = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
////        boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
//
//        BmobNotifyManager.getInstance(context).showNotifyWithExtras(true, true, R.mipmap.ic_launcher, ticker, "添加好友", ticker.toString(), intent);
//    }
//
//    /**
//     * 修改用户对应的另一半
//     *
//     * @param myFriends
//     */
//    public void updateMyFriend(final Context context, final String myFriends, final String userNick) {
//        if (!myFriends.equals(MyApplication.getInstance().getMyUser().getObjectId())) {
//            MyUser user = new MyUser();
//            user.setMyFriend(myFriends);
//            user.update(context, MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {
//
//                @Override
//                public void onSuccess() {
//                    MyApplication.getInstance().getMyUser().setMyFriend(myFriends);
//                    getMyFriends(myFriends);
//                    if (ehList.size() > 0) {// 有监听的时候，传递下去,每个监听依次执行，哈哈哈
//                        for (MyEventListener handler : ehList)
//                            handler.agreeAddUser();
//                    } else {//没得监听
//                        //显示通知
//                        showOtherNotify(context, null, userNick + "同意添加您为好友");
//                    }
//                }
//
//                @Override
//                public void onFailure(int code, String msg) {
//                    // TODO Auto-generated method stub
//                    Log.i("bmob", "更新失败：" + msg);
//                }
//            });
//        }
//    }
//
//    /**
//     * 获取到另一半的信息
//     *
//     * @param friendId
//     */
//    private void getMyFriends(String friendId) {
//        if (friendId == null) {
//            return;
//        }
//        if (MyApplication.getInstance().getMyUser() == null) {
//            return;
//        }
//        List<BmobChatUser> contacts = BmobDB.create(context).getContactList();
//        Log.i("HH", "length：" + contacts.size());
//        for (BmobChatUser myFriends : contacts) {
//            Log.i("HH", "friendId：" + friendId + ":" + myFriends.getObjectId());
//            if (friendId.equals(myFriends.getObjectId())) {
//                MyApplication.getInstance().setMyFriends(myFriends);
//            }
//        }
//
//    }
//}
