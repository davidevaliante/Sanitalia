package aqua.extensions

import android.os.Handler


class Do {
    enum class duration {
        SECONDS, MILLISECONDS
    }
    infix fun seconds(function: () -> Unit) {
        Do.mFormat = duration.SECONDS
        Do.handle(function)
    }
    infix fun milliseconds(function: () -> Unit) {
        Do.mFormat = duration.MILLISECONDS
        Do.handle(function)
    }
    companion object {
        var mDuration = 0
        var mFormat: duration = duration.MILLISECONDS
        infix fun now(function: () -> Unit) {
            function()
        }
        fun handle(function: () -> Unit) {
            Handler().postDelayed(function,
                    when (mFormat) {
                        duration.SECONDS -> mDuration * 1000L
                        duration.MILLISECONDS -> (mDuration).toLong()
                        else -> (mDuration).toLong()
                    })
        }
        infix fun after(seconds: Int): Do {
            Do.mDuration = seconds
            return Do()
        }
    }
}