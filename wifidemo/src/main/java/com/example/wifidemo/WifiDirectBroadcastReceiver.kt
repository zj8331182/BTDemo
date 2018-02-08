package com.example.wifidemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import com.example.zhangmingzhe.zmzdemo.util.LogUtils
import com.example.zlibrary.AppConstant

/**
 * Created by Zhang Mingzhe on 2018/2/8.
 * mail:1084904209@qq.com
 * Describe
 */
class WifiDirectBroadcastReceiver(private val mManager: WifiP2pManager, private val mChannel: Channel, private val mAvtivity: WifiMainActivity, private val mPeerListener: PeerListListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        LogUtils.i(AppConstant().ZTAG, "onReceive: action is $action")
        when (action) {
            WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    LogUtils.i(AppConstant().ZTAG, "onReceive:  Wifi P2P is enabled")
                } else {
                    // Wi-Fi P2P is not enabled
                    LogUtils.i(AppConstant().ZTAG, "onReceive:  Wi-Fi P2P is not enabled")
                }
            }
            WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                LogUtils.i(AppConstant().ZTAG, "onReceive: WIFI_P2P_PEERS_CHANGED_ACTION")
                mManager.requestPeers(mChannel, mPeerListener)
            }
            WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(EXTRA_NETWORK_INFO)
                if (networkInfo.isConnected) {
                    mManager.requestConnectionInfo(mChannel) { info ->
                        mAvtivity.mHostAddress = info.groupOwnerAddress
                        if (info.groupFormed and info.isGroupOwner) {
                            mAvtivity.startSockService()
                        } else if (info.groupFormed) {
                            mAvtivity.connectTcpToService()
                        }
                    }
                }
            }
            WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
            } // Respond to this device's wifi state changing
        }
    }
}