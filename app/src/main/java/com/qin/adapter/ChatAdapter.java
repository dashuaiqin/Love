package com.qin.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
//import com.qin.Utils.NewRecordPlayClickListener;
import com.qin.Application.MyApplication;
import com.qin.Utils.StringUtils;
import com.qin.Utils.TimeUtils;
import com.qin.cons.IntegerCons;
import com.qin.love.R;
import com.qin.love.ShowPhotoActivity;
import com.qin.model.MyUser;
import com.qin.view.ChatProgress;
import com.qin.view.EmoticonsTextView;
import com.qin.view.ProgressImageView;

import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobConfig;

//import cn.bmob.im.BmobDownloadManager;
//import cn.bmob.im.BmobUserManager;
//import cn.bmob.im.bean.BmobMsg;
//import cn.bmob.im.config.BmobConfig;
//import cn.bmob.im.inteface.DownloadListener;

/**
 * Created by Administrator on 2015/12/21.
 */
public class ChatAdapter extends BasePhotoAdapter {
    private EmoticonsTextView txtMessage;
    private TextView txtNick;//昵称
    private TextView txtStatus;//状态
    private TextView txtTime;//聊天时间
    private List<BmobIMMessage> list;//数据源
    private Context context;
    private ImageView ivAvatar;//头像
    private ProgressImageView ivPicture;//发送的图片
    private ImageView ivFailResend;//重发失败
    private ImageView ivVoice;//播放语音的图片
    //8种Item的类型
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;

    private String currentObjectId = "";//当前用户id
    private MyUser me,friend;


    public ChatAdapter(Context context, List<BmobIMMessage> list) {
        super();
        this.list = list;
        this.context = context;
        me=MyApplication.getInstance().getMyUser();
        currentObjectId = MyApplication.getInstance().getMyUser().getObjectId();
        friend=MyApplication.getInstance().getMyFriends();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<BmobIMMessage> getList() {
        return list;
    }
    public BmobIMMessage getFirstMsg() {
        return list.get(0);
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage msg = list.get(position);
        if (msg.getMsgType().equals(BmobIMMessageType.IMAGE.getType())) {
            return msg.getFromId().equals(currentObjectId) ? TYPE_SEND_IMAGE : TYPE_RECEIVER_IMAGE;
        } else if (msg.getMsgType() == BmobIMMessageType.LOCATION.getType()) {
            return msg.getFromId().equals(currentObjectId) ? TYPE_SEND_LOCATION : TYPE_RECEIVER_LOCATION;
        } else if (msg.getMsgType() == BmobIMMessageType.VOICE.getType()) {
            return msg.getFromId().equals(currentObjectId) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        } else {
            return msg.getFromId().equals(currentObjectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 8;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BmobIMMessage bMsg = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(bMsg, position);
        }
        txtNick = ViewHolder.get(convertView, R.id.txt_nick);
        ivAvatar = ViewHolder.get(convertView, R.id.iv_avatar);
        final ChatProgress pgLoad = ViewHolder.get(convertView, R.id.progress_load);//进度条
        ivFailResend = ViewHolder.get(convertView, R.id.iv_fail_resend);
        txtStatus = ViewHolder.get(convertView, R.id.tv_send_status);
        txtTime = ViewHolder.get(convertView, R.id.tv_time);
        if (bMsg!= null) {
            if(!bMsg.getFromId().equals(currentObjectId)) {
                if(bMsg.getBmobIMUserInfo()!=null) {
                    showImageByLoaderCacheAll(context, bMsg.getBmobIMUserInfo().getAvatar(), ivAvatar, R.drawable.default_avatar);
                    txtNick.setText(bMsg.getBmobIMUserInfo().getName());
                    txtTime.setText(TimeUtils.getChatTime(bMsg.getCreateTime()));
                }else{
                    showImageByLoaderCacheAll(context, friend.getAvatar(), ivAvatar, R.drawable.default_avatar);
                    txtNick.setText(friend.getNick());
                    txtTime.setText(TimeUtils.getChatTime(bMsg.getCreateTime()));
                }
            }else{
                showImageByLoaderCacheAll(context, me.getAvatar(), ivAvatar, R.drawable.default_avatar);
                txtNick.setText(me.getNick());
                txtTime.setText(TimeUtils.getChatTime(bMsg.getCreateTime()));
            }
        }
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_picture:
//                        Intent it = new Intent(context, ShowPhotoActivity.class);
//                        String paths = getImagePaths();
//                        it.putExtra("paths", paths);
//                        it.putExtra("position", getCurImaPosition(list.get(position).getContent()));
//                        context.startActivity(it);
                        break;
                    default:
                        break;
                }

            }
        };
        //根据类型显示内容
        final String text = bMsg.getContent();
        String type=bMsg.getMsgType();
        if (type.equals(BmobIMMessageType.IMAGE.getType())) {//图片类型
        } else if (type.equals(BmobIMMessageType.LOCATION.getType())) {//位置类型
        } else if (type.equals(BmobIMMessageType.VOICE.getType())) {//语音类型
        } else {//剩下默认的都是文本
            txtMessage = ViewHolder.get(convertView, R.id.txt_message);
            txtMessage.setText(text);
        }

//        switch (bMsg.getMsgType()) {
//            case BmobConfig.TYPE_TEXT:
//                txtMessage = ViewHolder.get(convertView, R.id.txt_message);
//                txtMessage.setText(text);
//                break;
//
//            case BmobConfig.TYPE_IMAGE://图片类
//                ivPicture = ViewHolder.get(convertView, R.id.iv_picture);
//                ivPicture.setOnClickListener(listener);
//                if (!StringUtils.isBlank(text)) {//发送成功之后存储的图片类型的content和接收到的是不一样的
//                    dealWithImage(position, ivFailResend, txtStatus, ivPicture, bMsg);
//                }
//
//                break;
//
//            case BmobConfig.TYPE_LOCATION://位置信息
//
//                break;
//            case BmobConfig.TYPE_VOICE://语音消息
//                //语音
//                ivVoice = ViewHolder.getImageView(convertView, R.id.iv_voice);
//                ivVoice.setTag(bMsg.getContent());
////                Toast.makeText(context,"oid:"+bMsg.getContent(),Toast.LENGTH_LONG).show();
//                //语音长度
//                final TextView txtVoiceLength = ViewHolder.get(convertView, R.id.tv_voice_length);
//                try {
//                    if (text != null && !text.equals("")) {
//                        txtVoiceLength.setVisibility(View.VISIBLE);
//                        String content = bMsg.getContent();
//                        if (bMsg.getBelongId().equals(currentObjectId)) {//发送的消息
//                            if (bMsg.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
//                                    || bMsg.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {//当发送成功或者发送已阅读的时候，则显示语音长度
//                                txtVoiceLength.setVisibility(View.VISIBLE);
//                                String length = content.split("&")[2];
//                                txtVoiceLength.setText(length + "\''");
//                            } else {
//                                txtVoiceLength.setVisibility(View.INVISIBLE);
//                            }
//                        } else {//收到的消息
//                            boolean isExists = BmobDownloadManager.checkTargetPathExist(currentObjectId, bMsg);
//                            if (!isExists) {//若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
//                                String netUrl = content.split("&")[0];
//                                final String length = content.split("&")[1];
//                                BmobDownloadManager downloadTask = new BmobDownloadManager(context, bMsg, new DownloadListener() {
//
//                                    @Override
//                                    public void onStart() {
//                                        // TODO Auto-generated method stub
//                                        pgLoad.setVisibility(View.VISIBLE);
//                                        txtVoiceLength.setVisibility(View.GONE);
//                                        ivVoice.setVisibility(View.INVISIBLE);//只有下载完成才显示播放的按钮
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//                                        txtVoiceLength.setVisibility(View.VISIBLE);
//                                        txtVoiceLength.setText(length + "\''");
//                                        ivVoice.setVisibility(View.VISIBLE);
//                                        pgLoad.setVisibility(View.INVISIBLE);
//                                    }
//
//                                    @Override
//                                    public void onError(String error) {
//                                        // TODO Auto-generated method stub
//                                        pgLoad.setVisibility(View.INVISIBLE);
//                                        txtVoiceLength.setVisibility(View.GONE);
//                                        ivVoice.setVisibility(View.INVISIBLE);
//                                    }
//                                });
//                                downloadTask.execute(netUrl);
//                            } else {
//                                String length = content.split("&")[2];
//                                txtVoiceLength.setText(length + "\''");
//                            }
//
//                        }
//                    }
//                    ivVoice.setOnClickListener(null);
//                    //播放语音文件
//                    if (bMsg.getContent().equals(ivVoice.getTag())) {
//                        ivVoice.setOnClickListener(new NewRecordPlayClickListener(context, bMsg));
//                    }else{
//                        ivVoice.setOnClickListener(null);
//                    }
//                } catch (Exception e) {
//
//                }
//                break;
//            default:
//                break;
//        }
        return convertView;
    }

    /**
     * 根据不同的类型加载UI
     *
     * @param message
     * @param position
     * @return
     */
    private View createViewByType(BmobIMMessage message, int position) {
        String type = message.getMsgType();
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (type.equals(BmobIMMessageType.IMAGE.getType())) {//图片类型
            return getItemViewType(position) == TYPE_RECEIVER_IMAGE ?
                    mInflater.inflate(R.layout.item_chat_received_image, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_image, null);
        } else if (type.equals(BmobIMMessageType.LOCATION.getType())) {//位置类型
            return getItemViewType(position) == TYPE_RECEIVER_LOCATION ?
                    mInflater.inflate(R.layout.item_chat_sent_message, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_message, null);
        } else if (type.equals(BmobIMMessageType.VOICE.getType())) {//语音类型
            return getItemViewType(position) == TYPE_RECEIVER_VOICE ?
                    mInflater.inflate(R.layout.item_chat_received_voice, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_voice, null);
        } else {//剩下默认的都是文本
            return getItemViewType(position) == TYPE_RECEIVER_TXT ?
                    mInflater.inflate(R.layout.item_chat_received_message, null)
                    :
                    mInflater.inflate(R.layout.item_chat_sent_message, null);
        }
    }

    /**
     * 添加一条数据，刷新
     *
     * @param message
     */
    public void addMsg(BmobIMMessage message) {
        list.add(message);
        this.notifyDataSetChanged();
    }

    /**
     * 设置数据源
     *
     * @param list
     */
    public void setList(List<BmobIMMessage> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    /**
     * 添加数据
     *
     * @param addList
     */
    public void insertMsgList(List<BmobIMMessage> addList) {
        addList.addAll(list);
        list=addList;
        notifyDataSetChanged();
    }

    /**
     * 处理图片
     *
     * @param @param position
     * @param @param progress_load
     * @param @param iv_fail_resend
     * @param @param tv_send_status
     * @param @param iv_picture
     * @param @param item
     * @return void
     * @throws
     * @Description: TODO
     */
//    private void dealWithImage(int position, ImageView iv_fail_resend, TextView tv_send_status, final ProgressImageView iv_picture, BmobMsg item) {
//        String text = item.getContent();
//        if (getItemViewType(position) == TYPE_SEND_IMAGE) {//发送的消息
//            if (item.getStatus() == BmobConfig.STATUS_SEND_START) {
//                iv_fail_resend.setVisibility(View.INVISIBLE);
//                tv_send_status.setVisibility(View.INVISIBLE);
//                iv_picture.startProgress();
//            } else if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {
//                iv_fail_resend.setVisibility(View.INVISIBLE);
//                tv_send_status.setVisibility(View.VISIBLE);
//                tv_send_status.setText("已发送");
//                iv_picture.stopProgress();
//            } else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {
//                iv_fail_resend.setVisibility(View.VISIBLE);
//                tv_send_status.setVisibility(View.INVISIBLE);
//                iv_picture.stopProgress();
//            } else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {
//                iv_fail_resend.setVisibility(View.INVISIBLE);
//                tv_send_status.setVisibility(View.VISIBLE);
//                iv_picture.stopProgress();
//                tv_send_status.setText("已阅读");
//            }
////			如果是发送的图片的话，因为开始发送存储的地址是本地地址，发送成功之后存储的是本地地址+"&"+网络地址，因此需要判断下
//            String showUrl = "";
//            if (text.contains("&")) {
//                showUrl = text.split("&")[0];
//            } else {
//                showUrl = text;
//            }
//            //为了方便每次都是取本地图片显示
//            showImageByLoaderCacheAll(context, showUrl, iv_picture, IntegerCons.NONE);
//        } else {
//            iv_picture.startProgress();
//            Glide.with(context)
//                    .load(text)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new GlideDrawableImageViewTarget(iv_picture) {
//                        @Override
//                        public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                            super.onResourceReady(drawable, anim);
//                            iv_picture.stopProgress();
//                        }
//                    });
//        }
//    }

    /**
     * 获取到全部图片路径
     *
     * @return
     */
//    private String getImagePaths() {
//        String result = "";
//        for (int i = 0; i < list.size(); i++) {
//            BmobMsg msg = list.get(i);
//            if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
//                String showUrl = "";
//                String text = msg.getContent();
//                if (text.contains("&")) {
//                    showUrl = text.split("&")[0];
//                } else {
//                    showUrl = text;
//                }
//                result = result + showUrl + "\0";
//            }
//        }
//        return result.substring(0, result.length() - 1);
//    }

    /**
     * 获取到当前图片的位置
     *
     * @param curpath
     * @return
     */
//    private int getCurImaPosition(String curpath) {
//        int position = 0;
//        for (int i = 0; i < list.size(); i++) {
//            BmobMsg msg = list.get(i);
//            if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
//                if (curpath.equals(msg.getContent())) {
//                    break;
//                } else {
//                    position++;
//                }
//            }
//        }
//        return position;
//    }
}
