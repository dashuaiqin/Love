package com.qin.love;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

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
        mCutPicView = (CutPicView) findViewById(R.id.cut_pic_view);
//		BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(R.drawable.beauty);
//		Bitmap bitmap = bd.getBitmap();
        mCutPicView.setPath(getIntent().getStringExtra("path"));
        findViewById(R.id.btn_done).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                bitmap = mCutPicView.clipImage();
                ImageUtils.getInstance(this).savePhotoToSDCard(bitmap);
                startProgressDialog();
                setProDiaMessage("正在上传头像...0%");
                uploadIcon();
                bitmap.recycle();
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



		/*BTPFileResponse response = BmobProFile.getInstance(this).upload(StringCons.PHOTO_DIR+"/icon.jpg", new UploadListener() {


			@Override
			public void onSuccess(String fileName, String url, BmobFile file) {
				Log.i("bmob", "文件上传成功：" + fileName + ",可访问的文件地址：" + file.getUrl());

				// TODO Auto-generated method stub
				// fileName ：文件名（带后缀），这个文件名是唯一的，开发者需要记录下该文件名，方便后续下载或者进行缩略图的处理
				// url        ：文件地址
				// file        :BmobFile文件类型，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
//				注：若上传的是图片，url地址并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`的版本可直接从'file.getUrl()'中获得可访问的文件地址。
			}

			@Override
			public void onProgress(int progress) {
				// TODO Auto-generated method stub
				Log.i("bmob","onProgress :"+progress);
				setProDiaMessage("正在上传头像..." + progress+"%");
			}

			@Override
			public void onError(int statuscode, String errormsg) {
				// TODO Auto-generated method stub
				Log.i("bmob","文件上传失败："+errormsg);
			}
		});*/
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
