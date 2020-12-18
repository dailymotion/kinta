package com.dailymotion.kinta.integration.appgallery

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException


open class ProgressRequestBody(delegate: RequestBody, listener: Listener) : RequestBody() {
    private var mDelegate: RequestBody = delegate
    protected var mListener: Listener = listener
    private var mCountingSink: CountingSink? = null

    override fun contentType(): MediaType {
        return mDelegate.contentType()!!
    }

    override fun contentLength(): Long {
        try {
            return mDelegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        mCountingSink = CountingSink(sink)
        val bufferedSink = Okio.buffer(mCountingSink as Sink)
        mDelegate.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    protected inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer?, byteCount: Long) {
            super.write(source!!, byteCount)
            bytesWritten += byteCount
            mListener.onProgress((100f * bytesWritten / contentLength()).toInt())
        }
    }

    interface Listener {
        fun onProgress(progress: Int)
    }

}