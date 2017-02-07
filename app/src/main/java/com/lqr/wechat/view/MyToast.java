package com.lqr.wechat.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lqr.wechat.R;


/**
 * @创建者 CSDN_LQR
 * @描述 自定义吐司
 */
public class MyToast {
    private Context mContext;
    private Toast mToast;

    private ImageView imageView;
    private TextView textView;

    private MyToast(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.toast_layout, null);
        imageView = (ImageView) view.findViewById(R.id.toast_img);
        textView = (TextView) view.findViewById(R.id.toast_text);

        mToast = new Toast(mContext);
        mToast.setView(view);
    }

    public static MyToast makeText(Context context, CharSequence text) {
        MyToast toast = new MyToast(context);
        toast.setText(text.toString())
                .setDuration(Toast.LENGTH_SHORT)
                .setGravity(Gravity.CENTER, 0, 300)
                .setTextColor(Color.rgb(255, 255, 255));
        return toast;
    }

    public static MyToast makeText(Context context, CharSequence text, int duration) {
        MyToast toast = new MyToast(context);
        toast.setText(text.toString())
                .setDuration(duration)
                .setGravity(Gravity.CENTER, 0, 300)
                .setTextColor(Color.rgb(255, 255, 255));
        return toast;
    }

    public MyToast setImageResource(int resId) {
        imageView.setImageResource(resId);
        return this;
    }

    public MyToast setText(String text) {
        textView.setText(text);
        return this;
    }

    public MyToast setTextColor(int color) {
        textView.setTextColor(color);
        return this;
    }

    public MyToast setGravity(int gravity, int xOffset, int yOffset) {
        mToast.setGravity(gravity, xOffset, yOffset);
        return this;
    }

    public MyToast setDuration(int duration) {
        mToast.setDuration(duration);
        return this;
    }

    public void show() {
        mToast.show();
    }
}
