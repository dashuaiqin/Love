package com.qin.love;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.qin.Application.MyApplication;
import com.qin.Utils.ImageUtils;
import com.qin.Utils.LoveUtils;
import com.qin.Utils.PermissionUtils;
import com.qin.Utils.StringUtils;
import com.qin.adapter.AddTimePhotoAdapter;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.Time;
import com.qin.myinterface.GetpathJepgListener;
import com.qin.view.ActionSheetDialog;
import com.qin.view.NoScrollGridView;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class AddTimeActivity extends BaseActivity {
    private TextView txtTitle;
    private TextView txtWordCount;
    private EditText editContent;//内容
    private NoScrollGridView nsgvPicture;//显示照片的GridView
    private AddTimePhotoAdapter photoAdapter;//照片适配器
    private String photoName;//拍照的图片的名字
    private ImageView ivBack;//返回按钮
    private TextView txtRight;//完成按钮
    private TextView txtGetLocation;//获取地点


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(listener);
        txtRight = (TextView) findViewById(R.id.txt_title_right);
        txtRight.setText(StringCons.FINISH);
        txtRight.setOnClickListener(listener);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_TIME);
        txtGetLocation = (TextView) findViewById(R.id.txt_get_location);
        txtGetLocation.setOnClickListener(listener);
        txtWordCount = (TextView) findViewById(R.id.txt_word_count);
        editContent = (EditText) findViewById(R.id.edit_content);
        editContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtWordCount.setText(800 - s.length() + "");
            }
        });


        nsgvPicture = (NoScrollGridView) findViewById(R.id.nsgv_picture);
        nsgvPicture.setSelector(new ColorDrawable(Color.TRANSPARENT));// 去掉点击时的背景
        nsgvPicture.setOnItemClickListener(itemListener);


        photoAdapter = new AddTimePhotoAdapter(this, null);
        nsgvPicture.setAdapter(photoAdapter);
    }

    /**
     * 点击的监听事件
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.txt_title_right:
                    uploadImages();
                    break;
                case R.id.txt_get_location:
                    PermissionUtils.requestPermission(AddTimeActivity.this, PermissionUtils.CODE_ACCESS_FINE_LOCATION, mPermissionGrant);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * gv的单项点击监听
     */
    AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (photoAdapter.getItemViewType(position) == 1) {
                PermissionUtils.requestPermission(AddTimeActivity.this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
            }
        }
    };

    /**
     * 显示获取照片的对话框
     */
    private void showPhotoDialog() {
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照获取图片", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                PermissionUtils.requestPermission(AddTimeActivity.this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
                            }
                        })
                .addSheetItem("去相册选择图片", ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                PermissionUtils.requestPermission(AddTimeActivity.this, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
                            }
                        }).show();
    }

    /**
     * 权限获取回调
     */
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    photoName = ImageUtils.getInstance(AddTimeActivity.this).doTakePhoto();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    MyApplication.isSetIcon = false;
                    ImageUtils.getInstance(AddTimeActivity.this).doGetPhoto();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    showPhotoDialog();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    startActivityForResult(new Intent(AddTimeActivity.this, GetLocationActivity.class), IntegerCons.GET_LOCATION);
                    break;
                default:
                    break;

            }

        }
    };


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    /**
     * 接收传回的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IntegerCons.PHOTO_WITH_ALBUM:
                    String paths = data.getStringExtra("filePath");
                    photoAdapter.addPaths(paths);
                    break;
                case IntegerCons.PHOTO_WITH_CAMERA:
                    photoAdapter.addPaths(StringCons.PHOTO_DIR + "/" + photoName);
                    break;
                case IntegerCons.GET_LOCATION:
                    txtGetLocation.setText(((PoiItem) data.getParcelableExtra("location")).getTitle());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 批量上传图片
     */
    private void uploadImages() {
        startProgressDialog("解析中，准备上传...");
        photoAdapter.getPaths(new GetpathJepgListener() {
            @Override
            public void finishGetPaths(final String[] ps) {
                if (ps.length < 1) {
                    stopProgressDialog();
                    addTime(null);
                    return;
                }

                //详细示例可查看BmobExample工程中BmobFileActivity类
                BmobFile.uploadBatch( ps, new UploadBatchListener() {

                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                        //2、urls-上传文件的完整url地址
                        if (urls.size() == ps.length) {//如果数量相等，则代表文件全部上传完成
                            String imUrls = "";
                            for (int i = 0; i < urls.size(); i++) {
                                if (i == urls.size() - 1) {
                                    imUrls += urls.get(i);
                                } else {
                                    imUrls += urls.get(i) + "\0";
                                }
                            }
                            addTime(imUrls);
                        }

                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        shortToast("批量上传出错：" + statuscode + "--" + errormsg);
                        stopProgressDialog();
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                        //1、curIndex--表示当前第几个文件正在上传
                        //2、curPercent--表示当前上传文件的进度值（百分比）
                        //3、total--表示总的上传文件数
                        //4、totalPercent--表示总的上传进度（百分比）
                        setProDiaMessage("上传第" + curIndex + "张图片..." + curPercent + "%");
                    }
                });


                /*BmobProFile.getInstance(AddTimeActivity.this).uploadBatch(ps, new UploadBatchListener() {
                    @Override
                    public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files) {
                        if (isFinish) {
                            String imUrls = "";
                            for (int i = 0; i < files.length; i++) {
                                if (i == files.length - 1) {
                                    imUrls += files[i].getUrl();
                                } else {
                                    imUrls += files[i].getUrl() + "\0";
                                }
                            }
                            addTime(imUrls);
                        }
                        // isFinish ：批量上传是否完成
                        // fileNames：文件名数组
                        // urls        : url：文件地址数组
                        // files     : BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。
                        // 注：若上传的是图片，url(s)并不能直接在浏览器查看（会出现404错误），需要经过`URL签名`得到真正的可访问的URL地址,当然，`V3.4.1`版本可直接从BmobFile中获得可访问的文件地址。
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                        // curIndex    :表示当前第几个文件正在上传
                        // curPercent  :表示当前上传文件的进度值（百分比）
                        // total       :表示总的上传文件数
                        // totalPercent:表示总的上传进度（百分比）
                        Log.i("bmob", "onProgress :" + curIndex + "---" + curPercent + "---" + total + "----" + totalPercent);
                        setProDiaMessage("上传第" + curIndex + "张图片..." + curPercent + "%");
                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        // TODO Auto-generated method stub
                        Log.i("bmob", "批量上传出错：" + statuscode + "--" + errormsg);
                        shortToast("批量上传出错：" + statuscode + "--" + errormsg);
                        stopProgressDialog();
                    }
                });*/
            }
        });
    }

    /**
     * 添加拾光
     */
    private void addTime(String imagePaths) {
        String content = editContent.getText().toString();
        if (imagePaths == null) {
            imagePaths = "";
        }
        if (StringUtils.isBlank(imagePaths) && StringUtils.isBlank(content)) {
            shortToast("请输入内容！");
            return;
        }
        Time time = new Time();
        String lt = txtGetLocation.getText().toString();
        String location = lt.equals("我在...") ? "" : lt;
        time.setLocation(location);
        time.setLoversId(LoveUtils.getLoversId());
        time.setContent(content);
        time.setFromId(MyApplication.getInstance().getMyUser().getObjectId());
        time.setImagePaths(imagePaths);
        time.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopProgressDialog();
                if(e==null) {
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //设置返回数据
                    setResult(IntegerCons.TYPE_ADD_TIME, intent);
                    finish();
                    shortToast("保存成功");
                }else{
                    shortToast("保存失败：" + s);
                }
            }
        });
    }


}
