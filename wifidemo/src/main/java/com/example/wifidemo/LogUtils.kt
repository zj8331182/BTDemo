package com.example.zhangmingzhe.zmzdemo.util

import android.util.Log

/**
 * Created by zhangmingzhe on 2016/7/17.
 * mail:zhangmingzhe@navinfo.com
 */
object LogUtils {
    init {
        KLog.init(true)
    }

    fun v(tag: String, msg: String) {
        KLog.v(tag, msg)
    }

    fun d(tag: String, msg: String) {
        KLog.d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        KLog.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        KLog.w(tag, msg)
    }

    fun e(tag: String, tr: Throwable) {
        KLog.e(tag, Log.getStackTraceString(tr))
    }

    fun e(tag: String, tr: String) {
        KLog.e(tag, tr)
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        KLog.e(tag, msg + '\n' + Log.getStackTraceString(tr))
    }
}//do nothing
