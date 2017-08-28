package com.qin.love;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.qin.Application.MyApplication;
import com.qin.Utils.StringUtils;
import com.qin.cons.StringCons;
import com.qin.model.MyUser;

import java.util.List;

//import cn.bmob.im.BmobUserManager;
//import cn.bmob.im.bean.BmobChatUser;
//import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2015/11/5.
 */
public class LoginActivity extends BaseActivity {

    private Button btnRegister;
    private Button btnLogin;
    private EditText editName;
    private EditText editPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    /**
     * 初始化界面
     */
    private void initView(){
        btnRegister= (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(listener);
        btnLogin= (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(listener);
        editName= (EditText) findViewById(R.id.edit_name);
        editPass= (EditText) findViewById(R.id.edit_pass);
    }

    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    login();
                    break;
                case R.id.btn_register:
                    startActivity(RegisterActivity.class,false);
                    break;

                default:
                    break;
            }
        }
    };


    /**
     * 登陆用户
     */
    private void login() {
        if (StringUtils.isBlank(editName.getText().toString())){
            shortToast(StringCons.PUT_IN_NAME);
        }else if (StringUtils.isBlank(editPass.getText().toString())){
            shortToast(StringCons.PUT_IN_PASS);
        }else {
            final BmobUser bu2 = new BmobUser();
            bu2.setUsername(editName.getText().toString());
            bu2.setPassword(editPass.getText().toString());
            bu2.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser s, BmobException e) {
                    if(e==null){
                        shortToast(bu2.getUsername() + "登陆成功");
                        GetCurrentUser();
                        GetMyFriends();
                        startActivity(MainActivity.class,true);
                    }else{
                        shortToast("登陆失败:" + e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 查询我的好友
     */
    private void GetMyFriends() {
//        //查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
//        //这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
//        BmobUserManager.getInstance(this).queryCurrentContactList(new FindListener<BmobChatUser>() {
//
//            @Override
//            public void onError(int arg0, String arg1) {
//                // TODO Auto-generated method stub
//                if (arg0 == BmobConfig.CODE_COMMON_NONE) {
//                    shortToast("您还没有添加另一半");
//                } else {
//                    shortToast("查询好友列表失败：" + arg1);
//                }
//            }
//
//            @Override
//            public void onSuccess(List<BmobChatUser> contacts) {
//                for (BmobChatUser myFriends : contacts) {
//                    if (MyApplication.getInstance().getMyUser().getMyFriend().equals(myFriends.getObjectId())) {
//                        MyApplication.getInstance().setMyFriends(myFriends);
//                    }
//                }
//            }
//        });
    }
    /**
     * 获取本地用户
     */
    private void GetCurrentUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser != null) {
            Log.i("life", "本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
                    + ",age = " + myUser.getAge());
            MyApplication.getInstance().setMyUser(myUser);
        } else {
            shortToast("本地用户为null,请登录。");
        }

    }

    /**
     * 清除本地用户
     */
    private void testLogOut() {
        BmobUser.logOut();
    }

}
