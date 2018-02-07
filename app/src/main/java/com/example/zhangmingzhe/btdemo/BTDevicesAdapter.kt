package com.example.zhangmingzhe.btdemo

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Zhang Mingzhe on 2018/2/7.
 * mail:1084904209@qq.com
 * Describe
 */
class BTDevicesAdapter(val mItemClickListener: DeviceClickListener) : RecyclerView.Adapter<BTDevicesAdapter.BTDevicesViewHolder>() {

    private lateinit var mDatas: List<BluetoothDevice>

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BTDevicesViewHolder {
        return BTDevicesViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.bt_devices_list, parent, false), object : DeviceClickListenerItem {
            override fun onItemClick(position: Int) {
                mItemClickListener.onItemClick(mDatas[position])
            }
        })
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: BTDevicesViewHolder?, position: Int) {
        val device = mDatas[position]
        holder!!.textName!!.text = device.name
    }

    fun setDatas(datas: List<BluetoothDevice>) {
        mDatas = datas
    }

    class BTDevicesViewHolder(itemView: View?, private val mItemClickListener: DeviceClickListenerItem) : RecyclerView.ViewHolder(itemView) {
        internal val textName = itemView?.findViewById<TextView>(R.id.textView_devices_name)

        init {
            itemView?.setOnClickListener { mItemClickListener.onItemClick(adapterPosition) }
        }
    }

    interface DeviceClickListener {
        fun onItemClick(device: BluetoothDevice)
    }

    interface DeviceClickListenerItem {
        fun onItemClick(position: Int)
    }
}