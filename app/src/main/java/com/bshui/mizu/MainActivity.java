package com.bshui.mizu;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.bshui.mizu.adapter.DeviceAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button bt_find;
    private ListView lv_device;
    private DeviceAdapter mDeviceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        //1.初始化蓝牙
        BleManager.getInstance().init(getApplication());
        //2.判断当前设备是否支持蓝牙ble
        if(BleManager.getInstance().isSupportBle()){
            //3.如果没有打开蓝牙,请求打开
            //判断本机是否打开蓝牙
            if(!BleManager.getInstance().isBlueEnable()){

                //引导界面引导用户打开蓝牙
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

            }
        }else{

            Toast.makeText(this,"The Device is not support the Ble",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    private  void initView(){
        bt_find = (Button)findViewById(R.id.bt_find);
        bt_find.setOnClickListener(this);
        lv_device = (ListView)findViewById(R.id.lv_device);
        mDeviceAdapter = new DeviceAdapter(this);
        lv_device.setAdapter(mDeviceAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_find:
                //发现设备
                startScan();
                break;
        }
    }

    private void startScan(){
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }

            @Override
            public void onScanStarted(boolean success) {
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }
        });
    }
}
