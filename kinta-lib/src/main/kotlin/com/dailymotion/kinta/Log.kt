package com.dailymotion.kinta

/**
 * Created by martin on 7/20/17.
 */
class Log {
    companion object {
        fun d(format: String, vararg params: Any, dryRun: Boolean = false) {
            System.out.format((if (dryRun) "DRY-RUN => " else "") + format, *params)
            System.out.print("\n")
        }

        fun e(format: String, vararg params: Any) {
            System.err.format(format, *params)
            System.err.print("\n")
        }
    }
}
