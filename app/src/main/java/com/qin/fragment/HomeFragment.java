package com.qin.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qin.Application.MyApplication;
import com.qin.Utils.ImageUtils;
import com.qin.Utils.LoveUtils;
import com.qin.Utils.NativeUtil;
import com.qin.Utils.PermissionUtils;
import com.qin.Utils.StringUtils;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.db.CommonDataSp;
import com.qin.love.BaseActivity;
import com.qin.love.ChatActivity;
import com.qin.love.CutPicActivity;
import com.qin.love.DiaryActivity;
import com.qin.love.LoginActivity;
import com.qin.love.MainActivity;
import com.qin.love.MemoryDayActivity;
import com.qin.love.R;
import com.qin.model.AddFriendMessage;
import com.qin.model.Dim;
import com.qin.model.LoversCommonData;
import com.qin.model.MessageNum;
import com.qin.model.MyUser;
import com.qin.model.Time;
import com.qin.view.ActionSheetDialog;
//import com.qin.receiver.MyMessageReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class HomeFragment extends BaseFragment {
    private EditText editId;//好友的id
    private Button btnAddFriends;
    private View view = null;
    private LinearLayout llChat;//聊天
    private LinearLayout llDiary;//日记
    private LinearLayout llMemoryDay;//日记
    private TextView txtMsgNum;//消息个数
    private ImageView ivHomeBg;
    private String photoName;//用来保存照的照片名字
    private String oilPhotoPath = "";//老的背景图片
    private CommonDataSp commonDataSp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container,
                    false);
            initView();
            initData();
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initData() {
        commonDataSp = new CommonDataSp(getActivity());
        oilPhotoPath = commonDataSp.getHomeBgPath();
        if (!StringUtils.isBlank(oilPhotoPath)) {
            showImageByLoader(oilPhotoPath, ivHomeBg, IntegerCons.NONE);
        }
    }

    /**
     * 界面初始化
     */
    private void initView() {
        editId = (EditText) view.findViewById(R.id.edit_id);
        btnAddFriends = (Button) view.findViewById(R.id.btn_add_friends);
        btnAddFriends.setOnClickListener(listener);
        llChat = (LinearLayout) view.findViewById(R.id.ll_chat);
        llChat.setOnClickListener(listener);
        llDiary = (LinearLayout) view.findViewById(R.id.ll_diary);
        llDiary.setOnClickListener(listener);
        llMemoryDay = (LinearLayout) view.findViewById(R.id.ll_memory_day);
        llMemoryDay.setOnClickListener(listener);
        txtMsgNum = (TextView) view.findViewById(R.id.txt_msg_num);
        ivHomeBg = (ImageView) view.findViewById(R.id.iv_home_bg);
        ivHomeBg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
                return true;
            }
        });


    }

    public void getCommonData() {
        if (MyApplication.getInstance().getMyUser() == null) {//未登录
            return;
        }
        BmobQuery<LoversCommonData> query = new BmobQuery<LoversCommonData>();
        //查询
        query.addWhereEqualTo("loversId", LoveUtils.getLoversId());
        //返回10条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<LoversCommonData>() {
            @Override
            public void done(List<LoversCommonData> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        MyApplication.getInstance().setLoversCommDataId(list.get(0).getObjectId());
                        if (oilPhotoPath == null || !oilPhotoPath.equals(list.get(0).getHomeImgPath())) {
                            oilPhotoPath = list.get(0).getHomeImgPath();
                            commonDataSp.saveHomeBgPath(oilPhotoPath);
                            showImageByLoader(oilPhotoPath, ivHomeBg, IntegerCons.NONE);
                        }
                    }
                }
            }
        });

    }

    /**
     * 通过imageloader显示图片
     */
    public void showImageByLoader(String imageUrl, ImageView imageView, int loading) {
        DrawableTypeRequest<String> dt = Glide.with(getActivity().getApplicationContext()).load(imageUrl);
        if (loading != IntegerCons.NONE) {
            dt.placeholder(loading);
        }
        dt.diskCacheStrategy(DiskCacheStrategy.ALL);
        if (new CommonDataSp(getActivity()).getIsDim()) {
            dt.bitmapTransform(new BlurTransformation(getActivity(), 23));// “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
        }
        dt.into(imageView);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_friends:
                    addFriends();
                    break;
                case R.id.ll_chat:
                    if (MyApplication.getInstance().getMyUser() == null) {//未登录
                        ((MainActivity) getActivity()).startActivity(LoginActivity.class, false);
                    } else {
                        ((MainActivity) getActivity()).startActivity(ChatActivity.class, false);
                    }
                    break;
                case R.id.ll_diary:
                    if (MyApplication.getInstance().getMyUser() == null) {//未登录
                        ((MainActivity) getActivity()).startActivity(LoginActivity.class, false);
                    } else {
                        ((MainActivity) getActivity()).startActivity(DiaryActivity.class, false);
                    }
                    break;
                case R.id.ll_memory_day:
                    if (MyApplication.getInstance().getMyUser() == null) {//未登录
                        ((MainActivity) getActivity()).startActivity(LoginActivity.class, false);
                    } else {
                        ((MainActivity) getActivity()).startActivity(MemoryDayActivity.class, false);
                    }
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
                    photoName = ImageUtils.getInstance(getActivity()).doTakePhoto();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    MyApplication.isSetIcon = true;
                    MyApplication.isHomeBg = true;
                    ImageUtils.getInstance(getActivity()).doGetPhoto();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    if (MyApplication.getInstance().getMyUser() == null) {//未登录
                        return;
                    }
                    new ActionSheetDialog(getActivity())
                            .builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem("用相机拍照", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_CAMERA, mPermissionGrant);
                                        }
                                    })
                            .addSheetItem("去相册选择照片", ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            PermissionUtils.requestPermission(getActivity(), PermissionUtils.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
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
        PermissionUtils.requestPermissionsResult(getActivity(), requestCode, permissions, grantResults, mPermissionGrant);
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

        if (resultCode == IntegerCons.TYPE_HOME_BG || resultCode == Activity.RESULT_OK) {
            String path = "";
            switch (requestCode) {
                case IntegerCons.PHOTO_WITH_ALBUM:
                    path = data.getStringExtra("path");
                    break;
                case IntegerCons.PHOTO_WITH_CAMERA:
                    path = StringCons.PHOTO_DIR + "/" + photoName;
                    break;
                default:
                    break;
            }
            if (!StringUtils.isBlank(path)) {
                upLoadHomeBg(path);
            }
        }

    }

    private void upLoadHomeBg(String picPath) {
        ((MainActivity) getActivity()).startProgressDialog();
        ((MainActivity) getActivity()).setProDiaMessage("正在上传...0%");

        final BmobFile bmobFile = new BmobFile(new File(ImageUtils.Jpeg(picPath, 40, getActivity())));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                ((MainActivity) getActivity()).setProDiaMessage("正在上传..." + value + "%");
            }

            @Override
            public void done(BmobException e) {
                if(e==null){
                    ImageUtils.deletePhoto((BaseActivity) getActivity(), oilPhotoPath);
                    addOrUpdateComData(bmobFile.getFileUrl());
                }else{
                    Log.i("bmob", "文件上传失败：" + e.getMessage());
                }
            }

        });
    }


    /**
     * 添加通用信息（主页背景等）
     */
    private void addOrUpdateComData(final String imagePaths) {
        if (MyApplication.getInstance().getLoversCommDataId() != null) {
            LoversCommonData data = new LoversCommonData();
            data.setHomeImgPath(imagePaths);
            data.update( MyApplication.getInstance().getLoversCommDataId(), new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    ((MainActivity) getActivity()).stopProgressDialog();
                    if(e==null){
                        ((MainActivity) getActivity()).shortToast("保存成功");
                        showImageByLoader(imagePaths, ivHomeBg, IntegerCons.NONE);
                        oilPhotoPath = imagePaths;
                        commonDataSp.saveHomeBgPath(oilPhotoPath);
                    }else{
                        ((MainActivity) getActivity()).stopProgressDialog();
                        ((MainActivity) getActivity()).shortToast("保存失败：" + e.getMessage());
                    }

                }
            });
        } else {
            LoversCommonData data = new LoversCommonData(LoveUtils.getLoversId(), imagePaths);
            data.save( new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    ((MainActivity) getActivity()).stopProgressDialog();
                    if(e==null){
                        ((MainActivity) getActivity()).shortToast("保存成功");
                        showImageByLoader(imagePaths, ivHomeBg, IntegerCons.NONE);
                        oilPhotoPath = imagePaths;
                        commonDataSp.saveHomeBgPath(oilPhotoPath);
                    }else{
                        ((MainActivity) getActivity()).shortToast("保存失败：" + s);
                    }
                }
            });
        }
    }


    /**
     * 添加好友
     */
    private void addFriends() {
        String addId = editId.getText().toString();
        if ("".equals(addId)) {
            ((BaseActivity) getActivity()).shortToast("请输入对方的id");
            return;
        }
        if (addId.equals(MyApplication.getInstance().getMyUser().getObjectId())) {
            ((BaseActivity) getActivity()).shortToast("不能与自己配对！");
            return;
        }
        BmobQuery<MyUser> query = new BmobQuery<MyUser>();
        query.getObject(addId, new QueryListener<MyUser>() {

            @Override
            public void done(MyUser myUser, BmobException e) {
                if(e==null){
                    //发送添加好友请求
                    sendAddFriendMessage(new BmobIMUserInfo(myUser.getObjectId(), myUser.getNick(), myUser.getAvatar()));
                }else{
                    ((MainActivity) getActivity()).shortToast("没有找到此用户，请输入正确的对方的id");
                }
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    /**
     * eventbus订阅函数
     *
     * @param msgNum
     */
    @Subscribe
    public void onEventMainThread(MessageNum msgNum) {
        txtMsgNum.setVisibility(View.VISIBLE);
        txtMsgNum.setText(msgNum.getNum() + "");
    }

    /**
     * eventbus订阅函数
     *
     * @param dim
     */
    @Subscribe
    public void onEventMainThread(Dim dim) {
        showImageByLoader(oilPhotoPath, ivHomeBg, IntegerCons.NONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        getCommonData();
//        if(MyMessageReceiver.mNewNum==0){
//            txtMsgNum.setVisibility(View.GONE);
//        }else{
//            txtMsgNum.setVisibility(View.VISIBLE);
//            txtMsgNum.setText(MyMessageReceiver.mNewNum + "");
//        }
    }

    /**
     * 发送添加好友的请求
     */
    private void sendAddFriendMessage(BmobIMUserInfo info) {
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        AddFriendMessage msg = new AddFriendMessage();
        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getNick());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("objecteId", currentUser.getObjectId());
        map.put("avatar", currentUser.getAvatar());
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    ((BaseActivity) getActivity()).shortToast("发送请求成功，等待对方验证!");
                } else {//发送失败
                    ((BaseActivity) getActivity()).shortToast("发送请求失败，请重新添加!");
                }
            }
        });
    }
}
