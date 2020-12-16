package com.lqr.wechat.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @创建者 CSDN_LQR
 * @描述 其他方式登录界面
 */
public class OtherLoginActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.etPhone)
    EditText mEtPhone;
    @BindView(R.id.etPwd)
    EditText mEtPwd;
    @BindView(R.id.vLinePhone)
    View mVLinePhone;
    @BindView(R.id.vLinePwd)
    View mVLinePwd;

    @BindView(R.id.btnLogin)
    Button mBtnLogin;

    @Override
    public void initView() {
        setContentView(R.layout.activity_other_login);
        ButterKnife.bind(this);

        initToolbar();
    }

    @Override
    public void initListener() {
        /*------------------ 监听编辑框，变换样式 begin ------------------*/
        mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.green0));
                } else {
                    mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.line));
                }
            }
        });
        mEtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.green0));
                } else {
                    mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.line));
                }
            }
        });
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mEtPhone.getText().toString()) && !TextUtils.isEmpty(mEtPwd.getText().toString())) {
                    mBtnLogin.setEnabled(true);
                } else {
                    mBtnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mEtPhone.getText().toString()) && !TextUtils.isEmpty(mEtPwd.getText().toString())) {
                    mBtnLogin.setEnabled(true);
                } else {
                    mBtnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*------------------ 监听编辑框，变换样式 end ------------------*/
        super.initListener();
    }

    /**
     * 设置Toolbar
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("登录微信");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
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
}
