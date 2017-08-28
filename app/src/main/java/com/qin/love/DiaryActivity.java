package com.qin.love;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qin.Application.MyApplication;
import com.qin.adapter.DiaryAdapter;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.Diary;
import com.qin.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class DiaryActivity extends BaseActivity {
    private LinearLayout llTitleRight;//标题右半部分
    private ImageView ivRight;
    private TextView txtTitle;
    private XListView xlvDiary;
    private   int skipNum;//跳过的次数
    private  static int SELECTE_NUM=10;//每次查询十个
    private ArrayList<Diary> diaryList;
    private DiaryAdapter adapter;
    private  int updatePosition;//记录更新的位置
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        llTitleRight= (LinearLayout) findViewById(R.id.ll_title_right);
        llTitleRight.setOnClickListener(listener);
        ivRight= (ImageView) findViewById(R.id.iv_right);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.drawable.add);
        txtTitle= (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_DIARY);
        ivBack= (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        xlvDiary= (XListView) findViewById(R.id.xlv_diary);
        xlvDiary.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onLoadMore() {
                getData();
            }
        });
        xlvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        updatePosition = position;
                toDiaActivity(IntegerCons.TYPE_UPDATE_DIARY, adapter.getItem(position - 1));
            }
        });
        startProgressDialog();
        refresh();
    }

    /**
     *点击事件
     */
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_title_right:
                    toDiaActivity(IntegerCons.TYPE_ADD_DIARY, null);
                        break;
                default:
                    break;
            }
        }
    };

    /**
     * 刷新
     */
private void refresh(){
    diaryList=new ArrayList<Diary>();
    skipNum=0;
    getData();
}
    /**
     * 从服务器拉数据
     */
    private void getData(){
        BmobQuery<Diary> query = new BmobQuery<Diary>();
         //查询playerName叫“比目”的数据
        query.addWhereEqualTo("userId", MyApplication.getInstance().getMyUser().getObjectId());
         //返回10条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(SELECTE_NUM);
        query.setSkip(skipNum);
        query.order("-sendDate");
         //执行查询方法
        query.findObjects(new FindListener<Diary>() {
            @Override
            public void done(List<Diary> list, BmobException e) {
                stopProgressDialog();
                if(e==null){
                    diaryList.addAll(list);
                    xlvDiary.stopRefresh();
                    xlvDiary.stopLoadMore();
                    skipNum+=SELECTE_NUM;
                    if (list.size()<SELECTE_NUM){//已经查完了
                        xlvDiary.setPullLoadEnable(false);
                    }else{
                        xlvDiary.setPullLoadEnable(true);
                    }
                    showData();
                }else{
                    shortToast("查询失败："+e.getMessage());
                    xlvDiary.stopRefresh();
                    xlvDiary.stopLoadMore();
                }
            }
        });
    }

    /**
     * 显示数据
     */
    private void showData(){
        if (adapter==null){
           adapter= new DiaryAdapter(this,diaryList);
            xlvDiary.setAdapter(adapter);
        }else{
            adapter.setList(diaryList);
        }

    }

    /**
     * 跳到其他页面
     */
    private void toDiaActivity(int type,Diary dia){
        Intent it=new Intent(DiaryActivity.this,AddDiaryActivity.class);
        it.putExtra("type", type);//查看或修改日记
        if(dia!=null){
            it.putExtra("diary", dia);
        }
        startActivityForResult(it,IntegerCons.TYPE_ADD_DIARY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==IntegerCons.TYPE_UPDATE_DIARY){
            refresh();
            if (updatePosition>SELECTE_NUM) {
                xlvDiary.setSelection(0);
            }
        }else if(resultCode==IntegerCons.TYPE_ADD_DIARY){
            refresh();
            xlvDiary.setSelection(0);
        }
    }
}
