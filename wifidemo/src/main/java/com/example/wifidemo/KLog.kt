package com.example.zhangmingzhe.zmzdemo.util

import android.text.TextUtils
import android.util.Log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by ZMZ on 2017/7/13.
 */
internal object KLog {

    private var IS_SHOW_LOG = true

    private val DEFAULT_MESSAGE = "execute"
    private val LINE_SEPARATOR = System.getProperty("line.separator")
    private val JSON_INDENT = 4

    private val V = 0x1
    private val D = 0x2
    private val I = 0x3
    private val W = 0x4
    private val E = 0x5
    private val A = 0x6
    private val JSON = 0x7

    fun init(isShowLog: Boolean) {
        IS_SHOW_LOG = isShowLog
    }

    fun v() {
        printLog(V, null, DEFAULT_MESSAGE)
    }

    fun v(msg: String) {
        printLog(V, null, msg)
    }

    fun v(tag: String, msg: String) {
        printLog(V, tag, msg)
    }

    fun d() {
        printLog(D, null, DEFAULT_MESSAGE)
    }

    fun d(msg: String) {
        printLog(D, null, msg)
    }

    fun d(tag: String, msg: String) {
        printLog(D, tag, msg)
    }

    fun i() {
        printLog(I, null, DEFAULT_MESSAGE)
    }

    fun i(msg: String) {
        printLog(I, null, msg)
    }

    fun i(tag: String, msg: String) {
        printLog(I, tag, msg)
    }

    fun w() {
        printLog(W, null, DEFAULT_MESSAGE)
    }

    fun w(msg: String) {
        printLog(W, null, msg)
    }

    fun w(tag: String, msg: String) {
        printLog(W, tag, msg)
    }

    fun e() {
        printLog(E, null, DEFAULT_MESSAGE)
    }

    fun e(msg: String) {
        printLog(E, null, msg)
    }

    fun e(tag: String, msg: String) {
        printLog(E, tag, msg)
    }

    fun a() {
        printLog(A, null, DEFAULT_MESSAGE)
    }

    fun a(msg: String) {
        printLog(A, null, msg)
    }

    fun a(tag: String, msg: String) {
        printLog(A, tag, msg)
    }


    fun json(jsonFormat: String) {
        printLog(JSON, null, jsonFormat)
    }

    fun json(tag: String, jsonFormat: String) {
        printLog(JSON, tag, jsonFormat)
    }


    private fun printLog(type: Int, tagStr: String?, msg: String?) {

        if (!IS_SHOW_LOG) {
            return
        }

        val stackTrace = Thread.currentThread().stackTrace

        val index = 5
        val className = stackTrace[index].fileName
        var methodName = stackTrace[index].methodName
        val lineNumber = stackTrace[index].lineNumber

        val tag = tagStr ?: className
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1)

        val stringBuilder = StringBuilder()
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodName).append(" ] ")

        if (msg != null && type != JSON) {
            stringBuilder.append(msg)
        }

        val logStr = stringBuilder.toString()

        when (type) {
            V -> Log.v(tag, logStr)
            D -> Log.d(tag, logStr)
            I -> Log.i(tag, logStr)
            W -> Log.w(tag, logStr)
            E -> Log.e(tag, logStr)
            A -> Log.wtf(tag, logStr)
            JSON -> {

                if (TextUtils.isEmpty(msg)) {
                    Log.d(tag, "Empty or Null json content")
                    return
                }

                var message: String? = null

                try {
                    if (msg!!.startsWith("{")) {
                        val jsonObject = JSONObject(msg)
                        message = jsonObject.toString(JSON_INDENT)
                    } else if (msg.startsWith("[")) {
                        val jsonArray = JSONArray(msg)
                        message = jsonArray.toString(JSON_INDENT)
                    }
                } catch (e: JSONException) {
                    e(tag, e.cause!!.message + "\n" + msg)
                    return
                }

                printLine(tag, true)
                message = logStr + LINE_SEPARATOR + message
                val lines = message.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val jsonContent = StringBuilder()
                for (line in lines) {
                    jsonContent.append("║ ").append(line).append(LINE_SEPARATOR)
                }
                Log.d(tag, jsonContent.toString())
                printLine(tag, false)
            }
        }

    }

    private fun printLine(tag: String, isTop: Boolean) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════")
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════")
        }
    }

}//do nothing
