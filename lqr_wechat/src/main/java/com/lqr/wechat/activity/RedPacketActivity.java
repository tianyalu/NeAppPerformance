package com.lqr.wechat.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @创建者 CSDN_LQR
 * @描述 红包
 */
public class RedPacketActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tvTip)
    TextView mTvTip;

    @BindView(R.id.tvMoneyLeft)
    TextView mTvMoneyLeft;
    @BindView(R.id.etMoney)
    EditText mEtMoney;
    @BindView(R.id.tvMoneyRight)
    TextView mTvMoneyRight;

    @BindView(R.id.etMessage)
    EditText mEtMessage;
    @BindView(R.id.tvHint)
    TextView mTvHint;

    @BindView(R.id.tvMoney)
    TextView mTvMoney;
    @BindView(R.id.btnOk)
    Button mBtnOk;

    @Override
    public void initView() {
        setContentView(R.layout.activity_red_packet);
        ButterKnife.bind(this);
        initToolbar();
    }

    @Override
    public void initListener() {
        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mBtnOk.setEnabled(false);
                    mTvMoney.setText("￥0.00");
                } else {
                    mBtnOk.setEnabled(true);
                    String result = String.format("%.2f", Double.valueOf(s.toString()));
                    mTvMoney.setText("￥" + result);
                    Double money = Double.valueOf(result);
                    if (money > 200) {
                        mTvTip.setVisibility(View.VISIBLE);
                        mTvMoneyLeft.setTextColor(UIUtils.getColor(R.color.red5));
                        mEtMoney.setTextColor(UIUtils.getColor(R.color.red5));
                        mTvMoneyRight.setTextColor(UIUtils.getColor(R.color.red5));
                        mBtnOk.setEnabled(false);
                    } else {
                        mTvTip.setVisibility(View.GONE);
                        mTvMoneyLeft.setTextColor(UIUtils.getColor(R.color.black0));
                        mEtMoney.setTextColor(UIUtils.getColor(R.color.black0));
                        mTvMoneyRight.setTextColor(UIUtils.getColor(R.color.black0));
                        mBtnOk.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvHint.setVisibility(TextUtils.isEmpty(s) ? View.VISIBLE : View.GONE);
                if (s.length() > 25) {
                    mEtMessage.setText(s.subSequence(0, 25));
                    mEtMessage.setSelection(25);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_one_icon, menu);
        menu.getItem(0).setIcon(R.mipmap.ic_question);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemOne:
                UIUtils.showToast("有问题？");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("发红包");
        getSupportActionBar().setSubtitle("微信安全支付");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mToolbar.setBackgroundColor(UIUtils.getColor(R.color.red1));
    }

}
