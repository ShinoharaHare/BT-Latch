package io.shinoharahare.btlatch

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object Communicator {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    enum class State {
        None,
        Connecting,
        Connected
    }

    private var mState = State.None
        set(v: State) {
            field = v
            emit("stateChange", v)
        }
    private var mSocket: BluetoothSocket? = null

    private val mRequestQueue = mutableListOf<String>()
    private val mRequestCallbacks = mutableMapOf<String, (JSONObject?) -> Unit>()
    private val mHandlers = mutableMapOf<String, (Any?) -> Unit>()
    private val mResponseBuffer = mutableListOf<Byte>()
    private val mMutex = Mutex()

    val state get() = mState
    val isConnected get() = mState == State.Connected

    private fun emit(event: String, data: Any?) {
        mHandlers[event]?.invoke(data)
    }

    fun on(event: String, handler: (Any?) -> Unit) {
        mHandlers[event] = handler
    }

    suspend fun connect(address: String) : Boolean {
        if (mState == State.None) {
            mState = State.Connecting
            return withContext(Dispatchers.IO) {
                try {
                    val device = adapter.getRemoteDevice(address)
                    mSocket = device?.createRfcommSocketToServiceRecord(uuid)
                    mSocket?.connect()
                    onConnected()
                    return@withContext true
                } catch (e: IOException) {
                    onConnectionFailed()
                    return@withContext false
                }
            }
        }
        return false
    }

    suspend fun disconnect() {
        if (mState == State.Connected) {
            withContext(Dispatchers.IO) {
                mSocket?.close()
            }
        }
    }

    private suspend fun wait() {
        withContext(Dispatchers.IO) {
            val ack: Byte = 6
            while (isConnected) {
                try {
                    if (mResponseBuffer.isNotEmpty()) {
                        if (mResponseBuffer.first() == ack) {
                            mResponseBuffer.removeFirst()
                            break
                        }
                    }
                } catch (e: NullPointerException) {
                    mResponseBuffer.removeFirst()
                }
            }
        }
    }

//    private suspend fun wait(): Boolean {
//        return try {
//            withTimeout(500) {
//                val ACK: Byte = 6
//                while (isConnected) {
//                    try {
//                        if (mResponseBuffer.isNotEmpty()) {
//                            if (mResponseBuffer.first() == ACK) {
//                                mResponseBuffer.removeFirst()
//                                break
//                            }
//                        }
//                    } catch (e: NullPointerException) {
//                        mResponseBuffer.removeFirst()
//                    }
//                    delay(500)
//                }
//            }
//            true
//        } catch (e: TimeoutCancellationException) {
//            false
//        }
//    }

    private suspend fun writeAndWait(byte: Int) {
        withContext(Dispatchers.IO) {
            try {
                mSocket?.outputStream?.write(byte)
                mSocket?.outputStream?.flush()
                wait()
            } catch (e: IOException) {}
        }
    }

    private suspend fun writeAndWait(bytes: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                mSocket?.outputStream?.write(bytes)
                mSocket?.outputStream?.flush()
                wait()
            } catch (e: IOException) {}
        }
    }

    private suspend fun send(data: String)  {
        if (isConnected) {
            val chunks = data.chunked(61)
            writeAndWait(1)
            for (c in chunks) {
                var bytes = byteArrayOf()
                bytes += 2
                bytes += c.toByteArray()
                bytes += 3
//                println("Sending: ${String(bytes)}")
                writeAndWait(bytes)
            }
            writeAndWait(4)
        }
    }

    private fun onConnectionFailed() {
        mState = State.None
        mSocket?.close()
        mSocket = null
    }

    suspend fun request(command: String): JSONObject = suspendCoroutine { cont ->
        val obj = JSONObject()
        val id = UUID.randomUUID().toString()
        obj.put("_id", id)
        obj.put("command", command)

        mRequestCallbacks[id] = {
            if (it == null) {
                cont.resumeWithException(Exception("Connection Lost"))
            } else {
                cont.resume(it)
                mRequestCallbacks.remove(id)
            }
        }
        mRequestQueue.add(obj.toString())
    }

    private suspend fun receive() {
        withContext(Dispatchers.IO) {
            var done = false
            while (isConnected && !done) {
                mMutex.withLock {
                    val json = String(mResponseBuffer.toByteArray())
//                    println(json)
                    try {
                        val obj = JSONObject(json)
                        mRequestCallbacks[obj["_id"]]?.invoke(obj)
                        mResponseBuffer.clear()
                        done = true
                    } catch (e: JSONException) { }
                }
                delay(50)
            }
        }
    }

    private fun onConnected() {
        mState = State.Connected

        GlobalScope.launch {
            while (isConnected) {
                if (mRequestQueue.isNotEmpty()) {
//                    println("Sending...")
                    send(mRequestQueue.removeFirst())
                    receive()
//                    println("Done.")
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            while (isConnected) {
                try {
                    val input = mSocket!!.inputStream!!
                    val byte = input.read()
                    mMutex.withLock {
                        mResponseBuffer += byte.toByte()
                    }
                } catch (e: IOException) {
                    onConnectionLost()
                }
            }
        }
    }

    private fun onConnectionLost() {
        mState = State.None
        mSocket?.close()
        mSocket = null
        mRequestQueue.clear()
        for (c in mRequestCallbacks.values) {
            c(null)
        }
        mRequestCallbacks.clear()
    }
}
