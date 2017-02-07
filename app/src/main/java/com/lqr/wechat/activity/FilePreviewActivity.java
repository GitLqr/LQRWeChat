package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.FileIconUtils;
import com.lqr.wechat.utils.FileOpenUtils;
import com.lqr.wechat.utils.FileUtils;
import com.lqr.wechat.utils.MimeTypeUtils;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * @创建者 CSDN_LQR
 * @描述 文件预览
 */
public class FilePreviewActivity extends BaseActivity {

    private Intent mIntent;
    private IMMessage mMessage;
    private FileAttachment mFa;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.ivPic)
    ImageView mIvPic;
    @InjectView(R.id.tvName)
    TextView mTvName;
    @InjectView(R.id.pbFile)
    ProgressBar mPbFile;
    @InjectView(R.id.btnOpen)
    Button mBtnOpen;//其他应用打开 下载

    @Override
    public void init() {
        mIntent = getIntent();
        mMessage = (IMMessage) mIntent.getSerializableExtra("message");
        if (mMessage == null) {
            interrupt();
            return;
        }
        mFa = (FileAttachment) mMessage.getAttachment();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_file_preview);
        ButterKnife.inject(this);
        initToolbar();

        setFileInfo();
    }

    @Override
    public void initData() {
        //判断文件是否已经下载到本地
//        if (TextUtils.isEmpty(mFa.getPath())) {
//            downloadFile();
//        }
    }

    @Override
    public void initListener() {
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBtnOpen.getText().equals("下载")) {
                    downloadFile();
                } else {
                    //打开文件
                    FileOpenUtils.openFile(FilePreviewActivity.this, mFa.getPath(), MimeTypeUtils.getMimeType(mFa.getDisplayName()));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("文件预览");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void setFileInfo() {
        mIvPic.setImageResource(FileIconUtils.getFileIconResId(mFa.getExtension()));
        mTvName.setText(mFa.getDisplayName());
        mPbFile.setVisibility(View.GONE);
        mBtnOpen.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mFa.getPath())) {
            mBtnOpen.setText("下载");
        } else {
            mBtnOpen.setText("其他应用打开");
        }
    }

    //下载文件
    private void downloadFile() {
        OkHttpUtils.get().url(mFa.getUrl()).build().execute(new FileCallBack(FileUtils.getDirFromPath(mFa.getPathForSave()), FileUtils.getFileNameFromPath(mFa.getPathForSave())) {


            @Override
            public void onError(Call call, Exception e, int id) {
                mIvPic.setImageResource(R.mipmap.default_img_failed);
                mTvName.setText("文件已过期或已被清理");
                mPbFile.setVisibility(View.GONE);
                mBtnOpen.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(File response, int id) {
                setFileInfo();
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                mPbFile.setMax((int) total);
                mPbFile.setProgress((int) (progress * 100));
            }

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                mPbFile.setVisibility(View.VISIBLE);
                mBtnOpen.setVisibility(View.GONE);
            }
        });
    }
}
