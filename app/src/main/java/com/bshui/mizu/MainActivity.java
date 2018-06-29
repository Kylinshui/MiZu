package com.bshui.mizu;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothGatt;

import android.bluetooth.BluetoothGattServer;

import android.bluetooth.le.BluetoothLeAdvertiser;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.bshui.mizu.adapter.DeviceAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button bt_find;
    private ListView lv_device;
    private DeviceAdapter mDeviceAdapter;
    private ProgressDialog progressDialog;



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
        progressDialog = new ProgressDialog(MainActivity.this);
        mDeviceAdapter = new DeviceAdapter(this);
        lv_device.setAdapter(mDeviceAdapter);
        lv_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //点击蓝牙设备选项 连接蓝牙 并进入相应的操作页面

                final BleDevice bleDevice = mDeviceAdapter.getItem(i);
                if(bleDevice == null)
                    return;

                if(!BleManager.getInstance().isConnected(bleDevice)){
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);

                }else{
                    //如果已经连上,直接转到设备信息页面
                    Intent intent = new Intent(MainActivity.this,DeviceInfoActivity.class);
                    intent.putExtra("BleDevice",bleDevice);
                    startActivity(intent);
                }
            }
        });
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

    private void connect(final BleDevice bleDevice){
        //Connect with device
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                //开始连接
                progressDialog.show();

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                //连接失败
                progressDialog.dismiss();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                //连接成功

                progressDialog.dismiss();
                Intent intent = new Intent(MainActivity.this,DeviceInfoActivity.class);
                intent.putExtra("BleDevice",bleDevice);
                startActivity(intent);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                //连接断开
                progressDialog.dismiss();
                Log.i("TAG","---onDisConnected");
            }
        });

    }


}
