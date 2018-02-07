package com.example.zhangmingzhe.btdemo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by Zhang Mingzhe on 2018/2/7.
 * mail:1084904209@qq.com
 * Describe
 */
class MeassageAdapter(val mItemClickListener: MessageClickListener) : RecyclerView.Adapter<BTDevicesAdapter.BTDevicesViewHolder>() {

    private lateinit var mDatas: MutableList<String>

    fun setDatas(datas: List<String>) {
        mDatas = datas.toMutableList()
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: BTDevicesAdapter.BTDevicesViewHolder?, position: Int) {
        val device = mDatas[position]
        holder!!.textName!!.text = device
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BTDevicesAdapter.BTDevicesViewHolder {
        return BTDevicesAdapter.BTDevicesViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.bt_devices_list, parent, false), object : BTDevicesAdapter.DeviceClickListenerItem {
            override fun onItemClick(position: Int) {
                mItemClickListener.onItemClick(mDatas[position])
            }
        })
    }

    interface MessageClickListener {
        fun onItemClick(device: String)
    }

    fun addData(data: String) {
        mDatas.add(data)
    }
}