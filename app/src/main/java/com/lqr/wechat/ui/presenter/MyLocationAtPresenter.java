package com.lqr.wechat.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.model.data.LocationData;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.IMyLocationAtView;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;

import java.util.ArrayList;
import java.util.List;

public class MyLocationAtPresenter extends BasePresenter<IMyLocationAtView> {

    private List<Geo2AddressResultObject.ReverseAddressResult.Poi> mData = new ArrayList<>();
    private int mSelectedPosi = 0;
    private LQRAdapterForRecyclerView<Geo2AddressResultObject.ReverseAddressResult.Poi> mAdapter;

    public MyLocationAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadData(Geo2AddressResultObject obj) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("\n地址：" + obj.result.address);
//        sb.append("\npois:");
//        for (Geo2AddressResultObject.ReverseAddressResult.Poi poi : obj.result.pois) {
//            sb.append("\n\t" + poi.title);
//        }
//        LogUtils.e(sb.toString());
        mData.clear();
        mData.addAll(obj.result.pois);
        setAdapter();
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<Geo2AddressResultObject.ReverseAddressResult.Poi>(mContext, mData, R.layout.item_location_poi) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Geo2AddressResultObject.ReverseAddressResult.Poi item, int position) {
                    helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDesc, item.address)
                            .setViewVisibility(R.id.ivSelected, mSelectedPosi == position ? View.VISIBLE : View.GONE);
                }
            };
            getView().getRvPOI().setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                mSelectedPosi = position;
                setAdapter();
            });
        } else {
            mAdapter.notifyDataSetChangedWrapper();
        }
    }

    public void sendLocation() {
        if (mData != null && mData.size() > mSelectedPosi) {
            Geo2AddressResultObject.ReverseAddressResult.Poi poi = mData.get(mSelectedPosi);
            Intent data = new Intent();
            LocationData locationData = new LocationData(poi.location.lat, poi.location.lng, poi.title, getMapUrl(poi.location.lat, poi.location.lng));
            data.putExtra("location", locationData);
            mContext.setResult(Activity.RESULT_OK, data);
            mContext.finish();
        }
    }


    //    获取位置静态图
    //    http://apis.map.qq.com/ws/staticmap/v2/?center=39.8802147,116.415794&zoom=10&size=600*300&maptype=landform&markers=size:large|color:0xFFCCFF|label:k|39.8802147,116.415794&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77
    //    http://st.map.qq.com/api?size=708*270&center=114.215843,22.685120&zoom=17&referer=weixin
    //    http://st.map.qq.com/api?size=708*270&center=116.415794,39.8802147&zoom=17&referer=weixin
    private String getMapUrl(double x, double y) {
        String url = "http://st.map.qq.com/api?size=708*270&center=" + y + "," + x + "&zoom=17&referer=weixin";
        return url;
    }
}
