package com.example.zhangmingzhe.btdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.example.zhangmingzhe.btdemo.AppConstant.MY_UUID;

/**
 * @author Zhang Mingzhe
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1001;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private BTDevicesAdapter adapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mDeviceList.add(device);
                adapter.setDatas(mDeviceList);
                adapter.notifyDataSetChanged();
            }
        }
    };
    private MeassageAdapter messageAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private EditText mEditText;
    private InputStream mInputStream;
    private OutputStream mOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_bt_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mEditText = findViewById(R.id.editText_message);
        findViewById(R.id.button_start_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(MainActivity.this, "Your device not support bt", Toast.LENGTH_SHORT).show();
                } else if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    // Add the name and address to an array adapter to show in a ListView
                    mDeviceList.addAll(pairedDevices);
                    adapter = new BTDevicesAdapter(new BTDevicesAdapter.DeviceClickListener() {
                        @Override
                        public void onItemClick(@NotNull BluetoothDevice device) {
                            connectToDevice(device);
                        }
                    });
                    adapter.setDatas(mDeviceList);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });

        findViewById(R.id.button_start_revicer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Register the BroadcastReceiver
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
                // Don't forget to unregister during onDestroy
            }
        });

        findViewById(R.id.button_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServices();
            }
        });

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOutputStream = mBluetoothSocket.getOutputStream();
                    byte[] bytes = mEditText.getText().toString().getBytes();
                    mOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startServices() {
        Observable.just(mBluetoothAdapter)
                .map(new Function<BluetoothAdapter, BluetoothServerSocket>() {
                    @Override
                    public BluetoothServerSocket apply(BluetoothAdapter bluetoothAdapter) throws Exception {
                        BluetoothServerSocket tmp = null;
                        try {
                            // MY_UUID is the app's UUID string, also used by the client code
                            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("ZMZ", UUID.fromString(MY_UUID));
                        } catch (IOException ignored) {
                        }
                        return tmp;
                    }
                })
                .map(new Function<BluetoothServerSocket, BluetoothSocket>() {
                    @Override
                    public BluetoothSocket apply(BluetoothServerSocket bluetoothServerSocket) throws Exception {
                        BluetoothSocket socket;
                        // Keep listening until exception occurs or a socket is returned
                        while (true) {
                            try {
                                socket = bluetoothServerSocket.accept();
                            } catch (IOException e) {
                                return null;
                            }
                            // If a connection was accepted
                            if (socket != null) {
                                // Do work to manage the connection (in a separate thread)
                                try {
                                    bluetoothServerSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return socket;
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BluetoothSocket>() {
                    @Override
                    public void accept(BluetoothSocket bluetoothSocket) throws Exception {
                        connectSuccess(bluetoothSocket);
                    }
                });
    }

    private void connectSuccess(BluetoothSocket bluetoothSocket) {
        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        mBluetoothSocket = bluetoothSocket;
        try {
            mInputStream = mBluetoothSocket.getInputStream();
            mOutputStream = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearMessageView();
        startReviceMessage();
    }

    private void connectToDevice(BluetoothDevice device) {
        Observable.just(device)
                .map(new Function<BluetoothDevice, BluetoothSocket>() {
                    @Override
                    public BluetoothSocket apply(BluetoothDevice bluetoothDevice) throws Exception {
                        return bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    }
                })
                .map(new Function<BluetoothSocket, BluetoothSocket>() {
                    @Override
                    public BluetoothSocket apply(BluetoothSocket bluetoothSocket) throws Exception {
                        mBluetoothAdapter.cancelDiscovery();
                        bluetoothSocket.connect();
                        return bluetoothSocket;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BluetoothSocket>() {
                    @Override
                    public void accept(BluetoothSocket bluetoothSocket) throws Exception {
                        connectSuccess(bluetoothSocket);
                    }
                });
    }

    private void startReviceMessage() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (mBluetoothSocket != null) {
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while (true) {
                        try {
                            bytes = mInputStream.read(buffer);
                            e.onNext(bytes);
                        } catch (IOException e1) {
                            break;
                        }
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        messageAdapter.addData(String.valueOf(integer));
                        messageAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void clearMessageView() {
        messageAdapter = new MeassageAdapter(new MeassageAdapter.MessageClickListener() {
            @Override
            public void onItemClick(@NotNull String device) {

            }
        });
        messageAdapter.setDatas(new ArrayList<String>());
        mRecyclerView.setAdapter(messageAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


