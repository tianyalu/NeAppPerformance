package com.lqr.wechat.activity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.appcompat.widget.Toolbar;

import com.lqr.wechat.R;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 附言
 */
public class PostscriptActivity extends BaseActivity {

    public String mAccount;//账号
    public String mMsg;//附言



    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.btnOk)
    Button mBtnOk;

    @BindView(R.id.etMsg)
    EditText mEtMsg;

    @OnClick({R.id.btnOk, R.id.ibClear})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                showWaitingDialog("请稍等");
                mMsg = mEtMsg.getText().toString();
                //发送添加好友请求
                NimFriendSDK.addFriend(mAccount, mMsg, new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void param) {
                        hideWaitingDialog();
                        UIUtils.showToast("添加好友申请成功");
                        finish();
                    }

                    @Override
                    public void onFailed(int code) {
                        UIUtils.showToast("添加好友失败" + code);
                        hideWaitingDialog();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        exception.printStackTrace();
                        hideWaitingDialog();
                    }
                });
                break;
            case R.id.ibClear:
                mEtMsg.setText("");
                break;
        }
    }

    @Override
    public void init() {
        mAccount = getIntent().getStringExtra("account");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_postscript);
        ButterKnife.bind(this);
        initToolbar();
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
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(UIUtils.getString(R.string.app_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText("发送");
    }
}
