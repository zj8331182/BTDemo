package com.example.wifidemo

import android.net.wifi.p2p.WifiP2pDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Zhang Mingzhe on 2018/2/8.
 * mail:1084904209@qq.com
 * Describe
 */
class WifiAdapter(val mItemClickListener: WifiItemClickListener) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    private lateinit var mDatas: MutableList<WifiP2pDevice>

    fun setDatas(list: MutableList<WifiP2pDevice>) {
        mDatas = list
    }

    override fun onBindViewHolder(holder: WifiViewHolder?, position: Int) {
        val divice = mDatas[position]
        holder!!.tv.text = divice.deviceName
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WifiViewHolder {
        return WifiViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.wifi_item, parent, false), object : ItemClickListener {
            override fun onItemClick(position: Int) {
                mItemClickListener.onItemClick(mDatas[position])
            }
        })
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }

    class WifiViewHolder(itemView: View?, private val mItemClickListener: ItemClickListener) : RecyclerView.ViewHolder(itemView) {
        internal var tv: TextView = itemView!!.findViewById(R.id.textView_item)

        init {
            itemView?.setOnClickListener { mItemClickListener.onItemClick(adapterPosition) }
        }
    }

    interface WifiItemClickListener {
        fun onItemClick(device: WifiP2pDevice)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}