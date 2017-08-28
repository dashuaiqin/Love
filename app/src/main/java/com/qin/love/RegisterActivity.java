package com.qin.love;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qin.Utils.StringUtils;
import com.qin.cons.StringCons;
import com.qin.model.MyUser;

//import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2015/11/5.
 */
public class RegisterActivity extends BaseActivity {

    private Button btnRegister;
    private EditText editName;
    private EditText editPass;
    private EditText editPassAg;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(listener);
        editName = (EditText) findViewById(R.id.edit_name);
        editPass = (EditText) findViewById(R.id.edit_pass);
        editPassAg = (EditText) findViewById(R.id.edit_pass_ag);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_REGISTER);
    }

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            signUp();
        }
    };

    /**
     * 注册用户
     */
    private void signUp() {
        if (StringUtils.isBlank(editName.getText().toString())) {
            shortToast(StringCons.PUT_IN_NAME);
        } else if (StringUtils.isBlank(editPass.getText().toString())) {
            shortToast(StringCons.PUT_IN_PASS);
        } else if (!StringUtils.isEquals(editPass.getText().toString(), editPassAg.getText().toString())) {
            shortToast(StringCons.PASS_NOT_SAME);
        } else {
            final MyUser myUser = new MyUser();
            myUser.setUsername(editName.getText().toString());
            myUser.setPassword(editPass.getText().toString());
            myUser.setAge(18);
            myUser.setSex(true);
            myUser.signUp(new SaveListener<String>() {

                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        shortToast("注册成功:" + myUser.getUsername() + "-"
                                + myUser.getObjectId() + "-" + myUser.getCreatedAt()
                                + "-" + myUser.getSessionToken() + ",是否验证：" + myUser.getEmailVerified());
                        finish();
                    } else {
                        shortToast("注册失败:" + e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 登陆用户
     */
    private void testLogin() {
        final BmobUser bu2 = new BmobUser();
        bu2.setUsername("smile");
        bu2.setPassword("123456");
        bu2.login(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    shortToast(bu2.getUsername() + "登陆成功");
                    testGetCurrentUser();
                } else {
                    shortToast("登陆失败:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 获取本地用户
     */
    private void testGetCurrentUser() {
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser != null) {
            Log.i("life", "本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
                    + ",age = " + myUser.getAge());
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
