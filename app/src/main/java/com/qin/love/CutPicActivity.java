package com.qin.love;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.Utils.ImageUtils;
import com.qin.cons.StringCons;
import com.qin.model.MyUser;
import com.qin.view.CutPicView;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class CutPicActivity extends BaseActivity implements OnClickListener {

    private CutPicView mCutPicView;
    private Bitmap bitmap;
    private String oldIconName;
    private TextView txtTitle,txtTitleRight;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_pic);
        initView();
        oldIconName = MyApplication.getInstance().getMyUser().getAvatarFileName();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        txtTitleRight = (TextView) findViewById(R.id.txt_title_right);
        txtTitleRight.setText(StringCons.CONFIRM);
        txtTitle= (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_CUT_PIC);
        mCutPicView = (CutPicView) findViewById(R.id.cut_pic_view);
//		BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.beauty);
//		Bitmap bitmap = bd.getBitmap();
        mCutPicView.setPath(getIntent().getStringExtra("path"));
        txtTitleRight.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_title_right:
                bitmap = mCutPicView.clipImage();
                ImageUtils.getInstance(this).savePhotoToSDCard(bitmap);
                startProgressDialog();
                setProDiaMessage("正在上传头像...0%");
                uploadIcon();
                bitmap.recycle();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 上传头像
     */
    public void uploadIcon() {
        String picPath = StringCons.PHOTO_DIR + "/icon.jpg";
        final BmobFile bmobFile = new BmobFile(new File(picPath));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                setProDiaMessage("正在上传头像..." + value + "%");
            }

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateIconUrl(bmobFile.getFileUrl(), bmobFile.getUrl());
                }
            }
        });




    }

    /**
     * 修改用户对应的头像
     *
     * @param url
     */
    public void updateIconUrl(final String url, final String fileName) {
        MyUser user = new MyUser();
        user.setAvatar(url);
        user.setAvatarFileName(fileName);
        user.update(MyApplication.getInstance().getMyUser().getObjectId(), new UpdateListener() {

            @Override
            public void done(BmobException e) {
                stopProgressDialog();
                if(e==null){
                    shortToast("头像上传成功！");
                    MyApplication.getInstance().getMyUser().setAvatar(url);
                    MyApplication.getInstance().getMyUser().setAvatarFileName(fileName);
                    deleteOldIcon();//成功后删除老的
                    finish();
                }else{
                    shortToast(e.getMessage());
                }
            }
        });
    }

    /**
     * 删除旧的icon
     */
    private void deleteOldIcon() {
        if (oldIconName == null) {
            return;
        }
        BmobFile file = new BmobFile();
        file.setUrl(oldIconName);//此url是上传文件成功之后通过bmobFile.getUrl()方法获取的。
        file.delete(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    shortToast("删除文件成功");
                } else {
                    shortToast("删除文件失败：" + e.getMessage() );
                }
            }
        });

		/*BmobProFile.getInstance(this).deleteFile(oldIconName, new DeleteFileListener() {

			@Override
			public void onError(int errorcode, String errormsg) {
				// TODO Auto-generated method stub
				Log.i("bmob", "删除文件失败：" + errormsg + "(" + errorcode + ")");
				shortToast("删除文件失败：" + errormsg + "(" + errorcode + ")");
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.i("bmob", "删除文件成功");
				shortToast("删除文件成功");
			}
		});*/
    }

}
