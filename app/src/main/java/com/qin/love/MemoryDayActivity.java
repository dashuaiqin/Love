package com.qin.love;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qin.Utils.LoveUtils;
import com.qin.adapter.MemAdapter;
import com.qin.cons.IntegerCons;
import com.qin.cons.StringCons;
import com.qin.model.MemorialDay;
import com.qin.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MemoryDayActivity extends BaseActivity {
        private LinearLayout llTitleRight;//标题右半部分
        private ImageView ivRight;
        private TextView txtTitle;
        private XListView xlvMem;
        private   int skipNum;//跳过的次数
        private  static int SELECTE_NUM=10;//每次查询十个
        private ArrayList<MemorialDay> diaryList;
        private MemAdapter adapter;
        private  int updatePosition;//记录更新的位置
        private ImageView ivBack;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_memory_day);
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
            xlvMem= (XListView) findViewById(R.id.xlv_mem);
            xlvMem.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    refresh();
                }

                @Override
                public void onLoadMore() {
                    getData();
                }
            });
            xlvMem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    updatePosition = position;
                    toMemActivity(IntegerCons.TYPE_UPDATE_MEM, adapter.getItem(position - 1));
                }
            });
            xlvMem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final String[] items = {"删除选中项的数据"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MemoryDayActivity.this);
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int item) {
                            deleteDiary(adapter.getItem(position-1));
                        }
                    });
                    builder.show();
                    return true;
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
                        toMemActivity(IntegerCons.TYPE_ADD_MEM, null);
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
            diaryList=new ArrayList<MemorialDay>();
            skipNum=0;
            getData();
        }
        /**
         * 从服务器拉数据
         */
        private void getData(){
            BmobQuery<MemorialDay> query = new BmobQuery<MemorialDay>();
            //查询
            query.addWhereEqualTo("loversId", LoveUtils.getLoversId());
            //返回10条数据，如果不加上这条语句，默认返回10条数据
            query.setLimit(SELECTE_NUM);
            query.setSkip(skipNum);
            query.order("-updatedAt");
            //执行查询方法
            query.findObjects(new FindListener<MemorialDay>() {
                @Override
                public void done(List<MemorialDay> list, BmobException e) {
                    stopProgressDialog();
                    if(e==null){
                        diaryList.addAll(list);
                        xlvMem.stopRefresh();
                        xlvMem.stopLoadMore();
                        skipNum += SELECTE_NUM;
                        if (list.size() < SELECTE_NUM) {//已经查完了
                            xlvMem.setPullLoadEnable(false);
                        } else {
                            xlvMem.setPullLoadEnable(true);
                        }
                        showData();
                    }else{
                        shortToast("查询失败：" + e.getMessage());
                        xlvMem.stopRefresh();
                        xlvMem.stopLoadMore();
                    }
                }
            });
        }

        /**
         * 显示数据
         */
        private void showData(){
            if (adapter==null){
                adapter= new MemAdapter(this,diaryList);
                xlvMem.setAdapter(adapter);
            }else{
                adapter.setList(diaryList);
            }

        }

        /**
         * 跳到其他页面
         */
        private void toMemActivity(int type,MemorialDay mem){
            Intent it=new Intent(MemoryDayActivity.this,AddMemoryDayActivity.class);
            it.putExtra("type", type);//查看或修改日记
            if(mem!=null){
                it.putExtra("mem", mem);
            }
            startActivityForResult(it,type);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode==IntegerCons.TYPE_UPDATE_MEM||resultCode==IntegerCons.TYPE_ADD_MEM){
                    refresh();
                    xlvMem.setSelection(0);
            }
        }

    /**
     * 删除纪念日
     */
    private void deleteDiary(MemorialDay memorialDay){
        startProgressDialog("正在删除");
        memorialDay.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    refresh();
                    xlvMem.setSelection(0);
                }else{
                    stopProgressDialog();
                    shortToast("删除失败：" + e.getMessage());
                }
            }
        });
    }
    }

