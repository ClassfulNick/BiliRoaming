@file:Suppress("unused")

package me.iacn.biliroaming.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XposedBridge
import me.iacn.biliroaming.Constant.TAG
import android.util.Log as ALog

object Log {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var toast: Toast? = null

    fun toast(msg: String, force: Boolean = false, excessive: Boolean = false) {
        if (!force && !sPrefs.getBoolean("show_info", true)) return
        if (!force && excessive && !sPrefs.getBoolean("show_excessive_info", false)) return
        handler.post {
            toast?.cancel()
            toast = Toast.makeText(currentContext, "", Toast.LENGTH_SHORT).apply {
                setText("哔哩漫游：$msg")
                show()
            }
        }
    }

    @JvmStatic
    private fun doLog(f: (String, String) -> Int, obj: Any?, toXposed: Boolean = false) {
        val str = if (obj is Throwable) ALog.getStackTraceString(obj) else obj.toString()

        if (str.length > maxLength) {
            val chunkCount: Int = str.length / maxLength
            for (i in 0..chunkCount) {
                val max: Int = maxLength * (i + 1)
                if (max >= str.length) {
                    doLog(f, str.substring(maxLength * i))
                } else {
                    doLog(f, str.substring(maxLength * i, max))
                }
            }
        } else {
            f(TAG, str)
            if (toXposed)
                XposedBridge.log("$TAG : $str")
        }
    }

    @JvmStatic
    fun d(obj: Any?) {
        doLog(ALog::d, obj)
    }

    @JvmStatic
    fun i(obj: Any?) {
        doLog(ALog::i, obj)
    }

    @JvmStatic
    fun e(obj: Any?) {
        doLog(ALog::e, obj, true)
    }

    @JvmStatic
    fun v(obj: Any?) {
        doLog(ALog::v, obj)
    }

    @JvmStatic
    fun w(obj: Any?) {
        doLog(ALog::w, obj)
    }

    private const val maxLength = 3000
}

