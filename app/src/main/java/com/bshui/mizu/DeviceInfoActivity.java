package com.bshui.mizu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_name;
    private TextView tv_id;
    private LinearLayout linear_puff;
    private LinearLayout linear_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initView();
    }

    private void initView(){
        tv_name = (TextView)findViewById(R.id.tv_name);
        tv_id   = (TextView)findViewById(R.id.tv_id);
        linear_puff = (LinearLayout)findViewById(R.id.linear_puff);
        linear_setting = (LinearLayout)findViewById(R.id.linear_setting);

        linear_puff.setOnClickListener(this);
        linear_setting.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linear_puff:
                Intent intent_puff = new Intent(DeviceInfoActivity.this,
                        PuffCountActivity.class);
                startActivity(intent_puff);
                break;
            case R.id.linear_setting:
                Intent intent_set = new Intent(DeviceInfoActivity.this,
                        SettingActivity.class);
                startActivity(intent_set);

                break;
        }
    }
}
