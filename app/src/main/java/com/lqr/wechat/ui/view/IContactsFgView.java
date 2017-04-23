package com.lqr.wechat.ui.view;


import android.view.View;
import android.widget.TextView;

import com.lqr.recyclerview.LQRRecyclerView;

public interface IContactsFgView {

    View getHeaderView();

    LQRRecyclerView getRvContacts();

    TextView getFooterView();
}
