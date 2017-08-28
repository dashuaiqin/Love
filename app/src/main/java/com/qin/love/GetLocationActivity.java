package com.qin.love;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.qin.adapter.PoiAdapter;
import com.qin.cons.StringCons;
import com.qin.xlistview.XListView;

import java.util.ArrayList;
import java.util.List;

public class GetLocationActivity extends BaseActivity implements LocationSource,
        AMapLocationListener {

    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation amapLocation;//记录定位到的点；

    private PoiSearch poiSearch;//poi搜索
    private PoiSearch.Query query;// Poi查询条件类
    private int page = 0;//poi页码

    private TextView mLocationErrText;
    private XListView xlvPoi;
    private PoiAdapter adapter;
    private ImageView ivBack;
    private TextView txtRight;
    private TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_location);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(listener);
        txtRight = (TextView) findViewById(R.id.txt_title_right);
        txtRight.setVisibility(View.GONE);
        txtRight.setText(StringCons.CONFIRM);
        txtRight.setOnClickListener(listener);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText(StringCons.TITLE_GET_LOCATION);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    /**
     * 控件的监听事件
     */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.txt_title_right:
                    Intent intent = new Intent();
                    intent.putExtra("location", adapter.getChoosedItem());
                    setResult(RESULT_OK, intent); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
                    finish();//此处一定要调用finish()方法
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);
        xlvPoi = (XListView) findViewById(R.id.xlv_poi);
        //不能下拉，只能上拉
        xlvPoi.setPullRefreshEnable(false);
        xlvPoi.setPullLoadEnable(true);
        xlvPoi.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                //拉取下一页的poi数据
                page++;
                doSearchQuery(amapLocation.getCity(), amapLocation.getLatitude(), amapLocation.getLongitude());
            }
        });
        xlvPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setChoosedPosition(position - 1);
            }
        });
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                this.amapLocation = amapLocation;
                showFirstPoi();
                //开始搜索poi
                doSearchQuery(amapLocation.getCity(), amapLocation.getLatitude(), amapLocation.getLongitude());
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocation(true);//只定位一次
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String city, Double latutide, Double longitude) {

        query = new PoiSearch.Query("", "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(page);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(poiListener);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latutide, longitude), 5000, true));// 设置搜索区域为以lp点为圆心，其周围5000米范围
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    /**
     * poi搜索结果监听
     */
    PoiSearch.OnPoiSearchListener poiListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rcode) {
            if (rcode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(query)) {// 是否是同一条
                        ArrayList<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        showPoiData(poiItems);
                    }
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    /**
     * 将poi搜索结果显示到界面
     *
     * @param poiItems
     */
    private void showPoiData(ArrayList<PoiItem> poiItems) {
        if (adapter == null) {
            txtRight.setVisibility(View.VISIBLE);
            adapter = new PoiAdapter(this, poiItems);
            xlvPoi.setAdapter(adapter);
        } else {
            adapter.addList(poiItems);
        }
    }

    /**
     * 将定位到的位置当成第一个poi显示
     */
    private void showFirstPoi() {
        txtRight.setVisibility(View.VISIBLE);
        String poiname = amapLocation.getPoiName();
        if (!poiname.contains("(")) {
            poiname += "(" + amapLocation.getDistrict() + amapLocation.getStreet() + ")";
        }
        PoiItem poiFirst = new PoiItem("", new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude()),poiname , amapLocation.getAddress());
        ArrayList<PoiItem> pois = new ArrayList<PoiItem>();
        pois.add(poiFirst);
        adapter = new PoiAdapter(this, pois);
        xlvPoi.setAdapter(adapter);
    }
}