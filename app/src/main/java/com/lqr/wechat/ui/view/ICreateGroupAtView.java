package com.lqr.wechat.ui.view;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lqr.recyclerview.LQRRecyclerView;

public interface ICreateGroupAtView {

    Button getBtnToolbarSend();

    LQRRecyclerView getRvContacts();

    LQRRecyclerView getRvSelectedContacts();

    EditText getEtKey();

    View getHeaderView();

}
