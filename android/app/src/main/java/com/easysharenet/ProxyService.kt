package com.easysharenet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket

class ProxyService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverSocket: ServerSocket? = null
    private val PROXY_PORT = 1080
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "proxy_service"
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("Başlatılıyor..."))
        
        serviceScope.launch {
            try {
                startProxyServer()
            } catch (e: Exception) {
                e.printStackTrace()
                stopSelf()
            }
        }
        
        return START_STICKY
    }
    
    private suspend fun startProxyServer() = withContext(Dispatchers.IO) {
        serverSocket = ServerSocket(PROXY_PORT)
        updateNotification("Çalışıyor - Port: $PROXY_PORT")
        
        while (true) {
            try {
                val clientSocket = serverSocket?.accept() ?: break
                serviceScope.launch {
                    handleClient(clientSocket)
                }
            } catch (e: Exception) {
                break
            }
        }
    }
    
    private suspend fun handleClient(clientSocket: Socket) = withContext(Dispatchers.IO) {
        try {
            val input = clientSocket.getInputStream()
            val output = clientSocket.getOutputStream()
            
            val version = input.read()
            if (version != 5) {
                clientSocket.close()
                return@withContext
            }
            
            val nmethods = input.read()
            input.skip(nmethods.toLong())
            
            output.write(byteArrayOf(5, 0))
            output.flush()
            
            input.read()
            val cmd = input.read()
            input.read()
            val atyp = input.read()
            
            if (cmd != 1) {
                sendSocks5Error(output, 7)
                clientSocket.close()
                return@withContext
            }
            
            val destAddr = when (atyp) {
                1 -> {
                    val addr = ByteArray(4)
                    input.read(addr)
                    addr.joinToString(".") { (it.toInt() and 0xFF).toString() }
                }
                3 -> {
                    val len = input.read()
                    val domain = ByteArray(len)
                    input.read(domain)
                    String(domain)
                }
                else -> {
                    sendSocks5Error(output, 8)
                    clientSocket.close()
                    return@withContext
                }
            }
            
            val destPort = (input.read() shl 8) or input.read()
            
            val destSocket = try {
                Socket(destAddr, destPort)
            } catch (e: Exception) {
                sendSocks5Error(output, 1)
                clientSocket.close()
                return@withContext
            }
            
            output.write(byteArrayOf(5, 0, 0, 1, 0, 0, 0, 0, 0, 0))
            output.flush()
            
            val destInput = destSocket.getInputStream()
            val destOutput = destSocket.getOutputStream()
            
            coroutineScope {
                launch { relay(input, destOutput) }
                launch { relay(destInput, output) }
            }
            
            destSocket.close()
            clientSocket.close()
            
        } catch (e: Exception) {
            e.printStackTrace()
            try { clientSocket.close() } catch (_: Exception) {}
        }
    }
    
    private suspend fun relay(input: InputStream, output: OutputStream) = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(8192)
            while (true) {
                val count = input.read(buffer)
                if (count == -1) break
                output.write(buffer, 0, count)
                output.flush()
            }
        } catch (e: Exception) {
        }
    }
    
    private fun sendSocks5Error(output: OutputStream, errorCode: Int) {
        output.write(byteArrayOf(5, errorCode.toByte(), 0, 1, 0, 0, 0, 0, 0, 0))
        output.flush()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Proxy Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
    
    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EasyShareNet")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(text))
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        try { serverSocket?.close() } catch (_: Exception) {}
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}
