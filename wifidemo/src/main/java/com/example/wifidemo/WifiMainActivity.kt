package com.example.wifidemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.zhangmingzhe.zmzdemo.util.LogUtils
import com.example.zlibrary.AppConstant
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.*


class WifiMainActivity : AppCompatActivity() {
    private lateinit var mManager: WifiP2pManager
    private lateinit var mChannel: WifiP2pManager.Channel
    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mIntentFilter: IntentFilter
    private lateinit var mAdapter: WifiAdapter
    private lateinit var mUpdAdapter: UdpAdapter
    private lateinit var mTextView: TextView
    private val mPeerListener: WifiP2pManager.PeerListListener = WifiP2pManager.PeerListListener { peers ->
        run {
            LogUtils.i(AppConstant().ZTAG, "PeerListListener:call ")
            mAdapter = WifiAdapter(object : WifiAdapter.WifiItemClickListener {
                override fun onItemClick(device: WifiP2pDevice) {
                    connectToDevice(device)
                }
            })
            val list = peers.deviceList.toMutableList()
            LogUtils.i(AppConstant().ZTAG, "List size: $list.size")
            mAdapter.setDatas(list)
            mRecyclerView.adapter = mAdapter
        }
    }
    private lateinit var mHostAddress: String

    private val mActionListener: WifiP2pManager.ActionListener = object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            Toast.makeText(this@WifiMainActivity, "Success", Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(reason: Int) {
            Toast.makeText(this@WifiMainActivity, "Failure", Toast.LENGTH_SHORT).show()
        }
    }


    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        mManager.connect(mChannel, config, mActionListener)
    }

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_main)

        mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager.initialize(this, mainLooper, null)
        mReceiver = WifiDirectBroadcastReceiver(mManager, mChannel, this, mPeerListener)
        mIntentFilter = IntentFilter()
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        mRecyclerView = findViewById(R.id.rv_wifi_list)
        mTextView = findViewById(R.id.textView_udp_address)
        mRecyclerView.layoutManager = LinearLayoutManager(this@WifiMainActivity)
        findViewById<Button>(R.id.button_discover).setOnClickListener({
            mManager.discoverPeers(mChannel, mActionListener)
        })

        findViewById<Button>(R.id.button_receive_udp).setOnClickListener({
            setViewToUpd()
            startReceiveUdp()
        })
    }

    private fun setViewToUpd() {
        mUpdAdapter = UdpAdapter(object : UdpAdapter.UpdItemClickListener {
            override fun onItemClick(device: String) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        mUpdAdapter.setDatas(ArrayList())
        mRecyclerView.adapter = mUpdAdapter
    }

    private fun startReceiveUdp() {
        Observable.create<String> { e ->
            val ds = DatagramSocket(1993)
            val buf = ByteArray(1024)
            val dp = DatagramPacket(buf, buf.size)
            while (true) {
                ds.receive(dp)
                mHostAddress = dp.address.hostAddress
                val data = String(dp.data)
                e.onNext(data)
                if (data == "zmz") {
                    connectTcpToService()
                    ds.close()
                    break
                }
            }
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { s ->
                    mTextView.text = mHostAddress
                    mUpdAdapter.addMessageValue(s)
                    mUpdAdapter.notifyDataSetChanged()
                }
    }

    private fun connectTcpToService() {

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mReceiver, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReceiver)
    }
}
