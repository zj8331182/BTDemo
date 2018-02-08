package com.example.wifidemo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by Zhang Mingzhe on 2018/2/8.
 * mail:1084904209@qq.com
 * Describe
 */
class UdpAdapter(val mItemClickListener: UpdItemClickListener) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private lateinit var mDatas: MutableList<String>

    fun setDatas(list: MutableList<String>) {
        mDatas = list
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    override fun onBindViewHolder(holder: WifiAdapter.WifiViewHolder?, position: Int) {
        val divice = mDatas[position]
        holder!!.tv.text = divice
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WifiAdapter.WifiViewHolder {
        return WifiAdapter.WifiViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.wifi_item, parent, false), object : WifiAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                mItemClickListener.onItemClick(mDatas[position])
            }
        })
    }

    interface UpdItemClickListener {
        fun onItemClick(device: String)
    }

    fun addMessageValue(s: String) {
        mDatas.add(s)
    }
}